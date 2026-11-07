package japicmp.output.html;

import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.model.*;
import japicmp.output.OutputFilter;
import japicmp.output.OutputGenerator;
import japicmp.output.semver.SemverOut;
import japicmp.util.Streams;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Generates a single page HTML report. The complete report is rendered as static HTML, so
 * it is readable without JavaScript. The embedded script only adds search, filters and the
 * expand/collapse buttons.
 */
public class HtmlOutputGenerator extends OutputGenerator<HtmlOutput> {
	private static final String N_A = "n.a.";
	private static final List<String> MODIFIER_ORDER = Arrays.asList("public", "protected", "private", "abstract",
		"static", "final", "transient", "volatile");
	private static final Pattern QUALIFIED_NAME = Pattern.compile("\\b((?:[a-z_][\\w]*\\.)+)([A-Z][\\w$]*)");

	private final HtmlOutputGeneratorOptions htmlOutputGeneratorOptions;
	private final TemplateEngine templateEngine = new TemplateEngine();
	private final Set<String> usedIds = new HashSet<>();

	public HtmlOutputGenerator(List<JApiClass> jApiClasses, Options options, HtmlOutputGeneratorOptions htmlOutputGeneratorOptions) {
		super(options, jApiClasses);
		this.htmlOutputGeneratorOptions = htmlOutputGeneratorOptions;
	}

	enum Severity {
		BINARY("bin", "Binary incompatible", "✖"),
		SOURCE("src", "Source incompatible", "⚠"),
		COMPATIBLE("ok", "Compatible", "✓");

		final String css;
		final String label;
		final String icon;

		Severity(String css, String label, String icon) {
			this.css = css;
			this.label = label;
			this.icon = icon;
		}

		static Severity of(JApiCompatibility compatibility) {
			if (!compatibility.isBinaryCompatible()) {
				return BINARY;
			}
			if (!compatibility.isSourceCompatible()) {
				return SOURCE;
			}
			return COMPATIBLE;
		}
	}

	private static final String[] GROUPS = {"Type hierarchy", "Serialization", "Annotations", "Fields", "Constructors", "Methods"};

	/**
	 * One change inside a class, rendered as a row with a signature diff.
	 */
	private static class Row {
		int group;
		String id;
		Severity severity;
		JApiChangeStatus status;
		String kind;
		String label;
		String badgeText;
		List<String> compatibilityChanges = new ArrayList<>();
		String lineNumbers = "";
		String diff;
	}

	private static class ClassEntry {
		JApiClass jApiClass;
		String id;
		Severity severity;
		List<Row> rows = new ArrayList<>();
	}

	@Override
	public HtmlOutput generate() {
		OutputFilter outputFilter = new OutputFilter(options);
		outputFilter.filter(jApiClasses);
		List<ClassEntry> classes = jApiClasses.stream().map(this::classEntry).collect(Collectors.toList());

		Map<String, String> params = new HashMap<>();
		params.put("title", escape(getTitle()));
		params.put("style", getStyle());
		params.put("script", templateEngine.loadTemplate("/html/report.js"));
		params.put("header", header());
		params.put("summary", summary(classes));
		params.put("index", index(classes));
		params.put("classes", options.isReportOnlySummary() ? "" : classes.stream().map(this::classHtml).collect(Collectors.joining()));
		params.put("footer", footer(classes));
		return new HtmlOutput(fill(templateEngine.loadTemplate("/html/report.html"), params));
	}

	/**
	 * Replaces the placeholders in a single pass, so that inserted content (scripts, type names)
	 * is never interpreted as a placeholder itself.
	 */
	private static String fill(String template, Map<String, String> params) {
		Matcher matcher = Pattern.compile("\\$\\{(\\w+)}").matcher(template);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String value = params.get(matcher.group(1));
			matcher.appendReplacement(sb, Matcher.quoteReplacement(value != null ? value : matcher.group(0)));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/* ------------------------------------------------------------------ header and summary */

	private String header() {
		StringBuilder sb = new StringBuilder();
		sb.append("<header class=\"head\">\n<span class=\"eyebrow\">japicmp report</span>\n");
		sb.append("<h1 class=\"title\">").append(escape(getTitle())).append("</h1>\n");
		sb.append("<div class=\"compare\"><code>").append(escape(options.joinOldArchives())).append("</code><span aria-hidden=\"true\">→</span><code>")
			.append(escape(options.joinNewArchives())).append("</code></div>\n");
		sb.append("<div class=\"settings\">");
		if (!options.isReproducibleReport()) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
			sb.append(setting("Created", dateFormat.format(new Date())));
		}
		sb.append("<span>Access filter: <b id=\"meta-accessmodifier-value\">").append(escape(options.getAccessModifier().name())).append("</b></span>");
		sb.append(setting("Only modifications", String.valueOf(options.isOutputOnlyModifications())));
		sb.append(setting("Only binary incompatible", String.valueOf(options.isOutputOnlyBinaryIncompatibleModifications())));
		sb.append(setting("Ignore missing classes", String.valueOf(options.getIgnoreMissingClasses().isIgnoreAllMissingClasses())));
		sb.append(setting("Includes", japicmp.util.StringHelper.filtersAsString(options.getIncludes(), true)));
		sb.append(setting("Excludes", japicmp.util.StringHelper.filtersAsString(options.getExcludes(), false)));
		sb.append("</div>\n");
		if (options.getIgnoreMissingClasses().isIgnoreAllMissingClasses()) {
			sb.append("<p class=\"warning\" id=\"warning-missingclasses\">WARNING: You are using the option '--ignore-missing-classes'. ")
				.append("Superclasses and interfaces that could not be found on the classpath are ignored, so changes caused by them are not shown.</p>\n");
		}
		sb.append("</header>\n");
		return sb.toString();
	}

	private String setting(String key, String value) {
		return "<span>" + escape(key) + ": <b>" + escape(value) + "</b></span>";
	}

	private String summary(List<ClassEntry> classes) {
		Map<Severity, Integer> counts = new EnumMap<>(Severity.class);
		Map<Severity, String> firstRow = new EnumMap<>(Severity.class);
		for (Severity severity : Severity.values()) {
			counts.put(severity, 0);
		}
		int classFileChanges = 0;
		String firstClassFileChange = null;
		for (ClassEntry entry : classes) {
			for (Row row : entry.rows) {
				counts.put(row.severity, counts.get(row.severity) + 1);
				if (!firstRow.containsKey(row.severity)) {
					firstRow.put(row.severity, row.id);
				}
			}
			if (entry.jApiClass.getClassFileFormatVersion().getChangeStatus() == JApiChangeStatus.MODIFIED) {
				classFileChanges++;
				if (firstClassFileChange == null) {
					firstClassFileChange = entry.id;
				}
			}
		}
		StringBuilder sb = new StringBuilder("<section class=\"summary\" aria-label=\"Summary\">\n");
		sb.append(verdict());
		for (Severity severity : Severity.values()) {
			sb.append(tile(severity.css, counts.get(severity), severity.label, firstRow.get(severity)));
		}
		sb.append(tile("info", classFileChanges, "Class file version changed", firstClassFileChange));
		sb.append("</section>\n");
		return sb.toString();
	}

	private String tile(String css, int count, String label, String target) {
		String content = "<span class=\"n\">" + count + "</span><span class=\"l\">" + escape(label) + "</span>";
		if (target == null || options.isReportOnlySummary()) {
			return "<div class=\"tile " + css + "\">" + content + "</div>\n";
		}
		return "<a class=\"tile " + css + "\" href=\"#" + target + "\">" + content + "</a>\n";
	}

	private String verdict() {
		String semver = htmlOutputGeneratorOptions.getSemanticVersioningInformation();
		String level;
		String text;
		if (SemverOut.SEMVER_MAJOR.equals(semver)) {
			level = "major";
			text = "Incompatible changes. Increase the major version.";
		} else if (SemverOut.SEMVER_MINOR.equals(semver)) {
			level = "minor";
			text = "New API, but no incompatible changes.";
		} else if (SemverOut.SEMVER_PATCH.equals(semver) || SemverOut.SEMVER_COMPATIBLE.equals(semver)) {
			level = "patch";
			text = "No changes to the public API.";
		} else {
			return "<div class=\"tile verdict\"><span class=\"n\">Semver: n/a</span><span class=\"l\">No semantic versioning information available. "
				+ "<span class=\"sv\" id=\"semver-version\">" + escape(semver) + "</span></span></div>\n";
		}
		return "<div class=\"tile verdict " + level + "\"><span class=\"n\">Semver: " + level + "</span><span class=\"l\">" + text
			+ " <span class=\"sv\" id=\"semver-version\">" + escape(semver) + "</span></span></div>\n";
	}

	private String index(List<ClassEntry> classes) {
		if (classes.isEmpty()) {
			return "";
		}
		String prefix = commonPackagePrefix(classes);
		StringBuilder sb = new StringBuilder();
		sb.append("<details class=\"index\" id=\"toc\" open>\n<summary>All classes (").append(classes.size()).append(")</summary>\n<nav class=\"idx\" aria-label=\"Class index\">\n");
		for (ClassEntry entry : classes) {
			String dot = entry.severity != Severity.COMPATIBLE ? entry.severity.css : entry.jApiClass.getChangeStatus() == JApiChangeStatus.NEW ? "new" : "";
			String fqn = entry.jApiClass.getFullyQualifiedName();
			String href = options.isReportOnlySummary() ? "" : " href=\"#" + entry.id + "\"";
			sb.append("<a").append(href).append(" title=\"").append(escape(fqn)).append("\"><span class=\"dot ").append(dot).append("\"></span><span>")
				.append(escape(fqn.substring(prefix.length()))).append("</span></a>\n");
		}
		sb.append("</nav>\n</details>\n");
		return sb.toString();
	}

	private String commonPackagePrefix(List<ClassEntry> classes) {
		String[] common = null;
		for (ClassEntry entry : classes) {
			String[] pkg = packageOf(entry.jApiClass.getFullyQualifiedName()).split("\\.");
			if (common == null) {
				common = pkg;
				continue;
			}
			int i = 0;
			while (i < common.length && i < pkg.length && common[i].equals(pkg[i])) {
				i++;
			}
			common = Arrays.copyOf(common, i);
		}
		// keep the last common segment, so that the entries still show which package they belong to
		if (common == null || common.length <= 1) {
			return "";
		}
		return String.join(".", Arrays.copyOf(common, common.length - 1)) + ".";
	}

	private String footer(List<ClassEntry> classes) {
		long newCount = classes.stream().filter(c -> c.jApiClass.getChangeStatus() == JApiChangeStatus.NEW).count();
		long modified = classes.stream().filter(c -> c.jApiClass.getChangeStatus() == JApiChangeStatus.MODIFIED).count();
		long removed = classes.stream().filter(c -> c.jApiClass.getChangeStatus() == JApiChangeStatus.REMOVED).count();
		long unchanged = classes.stream().filter(c -> c.jApiClass.getChangeStatus() == JApiChangeStatus.UNCHANGED).count();
		return "<p class=\"foot\">" + classes.size() + " classes: " + newCount + " new, " + modified + " modified, " + removed + " removed"
			+ (unchanged > 0 ? ", " + unchanged + " unchanged" : "")
			+ ". Types from the same package and from java.lang are shown by their simple name; hover for the full name.</p>\n";
	}

	/* ------------------------------------------------------------------ classes */

	private ClassEntry classEntry(JApiClass jApiClass) {
		ClassEntry entry = new ClassEntry();
		entry.jApiClass = jApiClass;
		// the fully qualified name stays the anchor of a class, so existing links keep working
		entry.id = jApiClass.getFullyQualifiedName();
		usedIds.add(entry.id);
		entry.severity = Severity.of(jApiClass);
		String simpleName = simpleName(jApiClass.getFullyQualifiedName());
		String pkg = packageOf(jApiClass.getFullyQualifiedName());

		superclassRow(jApiClass, simpleName).ifPresent(entry.rows::add);
		for (JApiImplementedInterface implementedInterface : jApiClass.getInterfaces()) {
			if (implementedInterface.getChangeStatus() == JApiChangeStatus.UNCHANGED && Severity.of(implementedInterface) == Severity.COMPATIBLE) {
				continue;
			}
			Row row = row(0, simpleName + "-interface-" + simpleName(implementedInterface.getFullyQualifiedName()), Severity.of(implementedInterface),
				implementedInterface.getChangeStatus(), "interface", implementedInterface.getFullyQualifiedName(), implementedInterface);
			row.diff = relationDiff("implements", implementedInterface.getChangeStatus(), implementedInterface.getFullyQualifiedName(), null);
			entry.rows.add(row);
		}
		if (!jApiClass.getGenericTemplates().isEmpty() && jApiClass.getGenericTemplates().stream().anyMatch(t -> t.getChangeStatus() != JApiChangeStatus.UNCHANGED)) {
			Row row = row(0, simpleName + "-type-parameters", worst(jApiClass.getGenericTemplates()), JApiChangeStatus.MODIFIED, "type parameters", simpleName, null);
			Line oldLine = new Line();
			Line newLine = new Line();
			oldLine.add(escape(simpleName), null);
			newLine.add(escape(simpleName), null);
			genericTemplates(jApiClass.getGenericTemplates(), JApiChangeStatus.MODIFIED, pkg, oldLine, newLine);
			row.diff = diff(JApiChangeStatus.MODIFIED, oldLine, newLine);
			entry.rows.add(row);
		}
		serializationRow(jApiClass, simpleName).ifPresent(row -> {
			entry.rows.add(row);
			if (row.severity == Severity.BINARY) {
				entry.severity = Severity.BINARY;
			}
		});
		for (JApiAnnotation annotation : jApiClass.getAnnotations()) {
			if (annotation.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
				continue;
			}
			Row row = row(2, simpleName + "-annotation-" + simpleName(annotation.getFullyQualifiedName()), Severity.of(annotation),
				annotation.getChangeStatus(), "annotation", "@" + simpleName(annotation.getFullyQualifiedName()), annotation);
			Line oldLine = new Line();
			Line newLine = new Line();
			annotationLines(Collections.singletonList(annotation), annotation.getChangeStatus(), pkg, oldLine, newLine, false);
			row.diff = diff(annotation.getChangeStatus(), oldLine, newLine);
			entry.rows.add(row);
		}
		jApiClass.getFields().stream()
			.sorted(Comparator.comparing(JApiField::getName))
			.forEach(field -> entry.rows.add(fieldRow(field, simpleName, pkg)));
		for (JApiConstructor constructor : jApiClass.getConstructors()) {
			entry.rows.add(behaviorRow(constructor, 4, "constructor", simpleName, pkg));
		}
		jApiClass.getMethods().stream()
			.sorted(Comparator.comparing(JApiMethod::getName))
			.forEach(method -> entry.rows.add(behaviorRow(method, 5, "method", simpleName, pkg)));

		// within a group the most severe changes come first, otherwise the order of the model is kept
		List<Row> sorted = new ArrayList<>(entry.rows);
		sorted.sort(Comparator.comparingInt((Row r) -> r.group).thenComparingInt(r -> r.severity.ordinal()));
		entry.rows = sorted;
		return entry;
	}

	private Row row(int group, String idSeed, Severity severity, JApiChangeStatus status, String kind, String label, JApiCompatibility compatibility) {
		Row row = new Row();
		row.group = group;
		row.id = uniqueId(idSeed);
		row.severity = severity;
		row.status = status;
		row.kind = kind;
		row.label = label;
		if (compatibility != null) {
			for (JApiCompatibilityChange change : compatibility.getCompatibilityChanges()) {
				row.compatibilityChanges.add(change.getType().name());
			}
		}
		return row;
	}

	private Optional<Row> superclassRow(JApiClass jApiClass, String simpleName) {
		JApiSuperclass superclass = jApiClass.getSuperclass();
		if (!superclass.getOldSuperclassName().isPresent() && !superclass.getNewSuperclassName().isPresent()) {
			return Optional.empty();
		}
		JApiChangeStatus status = superclass.getChangeStatus();
		Severity severity = Severity.of(superclass);
		String oldName = superclass.getOldSuperclassName().orElse("java.lang.Object");
		String newName = superclass.getNewSuperclassName().orElse("java.lang.Object");
		if (status == JApiChangeStatus.UNCHANGED && severity == Severity.COMPATIBLE) {
			return Optional.empty(); // shown as context in the class header
		}
		if ((status == JApiChangeStatus.NEW || status == JApiChangeStatus.REMOVED) && "java.lang.Object".equals(status == JApiChangeStatus.NEW ? newName : oldName)) {
			return Optional.empty();
		}
		String label = status == JApiChangeStatus.REMOVED ? oldName : newName;
		Row row = row(0, simpleName + "-superclass", severity, status, "superclass", label, superclass);
		row.diff = relationDiff("extends", status, newName, oldName);
		return Optional.of(row);
	}

	private String relationDiff(String keyword, JApiChangeStatus status, String newName, String oldName) {
		switch (status) {
			case NEW:
				return "<span class=\"ln plus\"><span class=\"sig\">+</span>" + keyword + " " + escape(newName) + "</span>";
			case REMOVED:
				return "<span class=\"ln minus\"><span class=\"sig\">−</span>" + keyword + " " + escape(oldName != null ? oldName : newName) + "</span>";
			case MODIFIED:
				return "<span class=\"ln minus\"><span class=\"sig\">−</span>" + keyword + " <del>" + escape(oldName) + "</del></span>"
					+ "<span class=\"ln plus\"><span class=\"sig\">+</span>" + keyword + " <ins>" + escape(newName) + "</ins></span>";
			default:
				return "<span class=\"ln\"><span class=\"sig\"> </span>" + keyword + " " + escape(newName) + "</span>";
		}
	}

	private Optional<Row> serializationRow(JApiClass jApiClass, String simpleName) {
		JApiSerialVersionUid uid = jApiClass.getSerialVersionUid();
		JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus status = jApiClass.getJavaObjectSerializationCompatible();
		boolean serializable = uid.isSerializableOld() || uid.isSerializableNew();
		if (!serializable || status == JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.NOT_SERIALIZABLE) {
			return Optional.empty();
		}
		boolean serializableChanged = uid.isSerializableOld() != uid.isSerializableNew();
		boolean defaultChanged = !uid.getSerialVersionUidDefaultOldAsString().equals(uid.getSerialVersionUidDefaultNewAsString());
		boolean declaredChanged = !uid.getSerialVersionUidInClassOldAsString().equals(uid.getSerialVersionUidInClassNewAsString());
		boolean compatible = status == JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE;
		if (compatible && !serializableChanged && !defaultChanged && !declaredChanged) {
			return Optional.empty();
		}
		String label = compatible ? "Serializable compatible" : "Serializable incompatible: " + jApiClass.getJavaObjectSerializationCompatibleAsString();
		Row row = row(1, simpleName + "-serialization", compatible ? Severity.COMPATIBLE : Severity.BINARY, JApiChangeStatus.MODIFIED, "serialization", label, null);
		row.badgeText = compatible ? null : "Serialization incompatible";
		row.diff = serializationLine("−", "minus", "del", uid.isSerializableOld(), uid.getSerialVersionUidDefaultOldAsString(), uid.getSerialVersionUidInClassOldAsString(),
			serializableChanged, defaultChanged, declaredChanged)
			+ serializationLine("+", "plus", "ins", uid.isSerializableNew(), uid.getSerialVersionUidDefaultNewAsString(), uid.getSerialVersionUidInClassNewAsString(),
			serializableChanged, defaultChanged, declaredChanged);
		return Optional.of(row);
	}

	private String serializationLine(String sign, String css, String mark, boolean serializable, String defaultUid, String declaredUid,
									 boolean serializableChanged, boolean defaultChanged, boolean declaredChanged) {
		List<String> parts = new ArrayList<>();
		parts.add(mark(serializable ? "implements java.io.Serializable" : "not Serializable", serializableChanged ? mark : null));
		parts.add(mark("default serialVersionUID = " + (N_A.equals(defaultUid) ? "–" : defaultUid), defaultChanged ? mark : null));
		parts.add(mark(N_A.equals(declaredUid) ? "no serialVersionUID declared" : "static final long serialVersionUID = " + declaredUid + "L", declaredChanged ? mark : null));
		return "<span class=\"ln " + css + "\"><span class=\"sig\">" + sign + "</span>" + String.join("; ", parts) + "</span>";
	}

	private String mark(String text, String mark) {
		return mark == null ? escape(text) : "<" + mark + ">" + escape(text) + "</" + mark + ">";
	}

	/* ------------------------------------------------------------------ members */

	private Row fieldRow(JApiField field, String simpleName, String pkg) {
		Row row = row(3, simpleName + "-" + field.getName(), Severity.of(field), field.getChangeStatus(), "field", field.getName(), field);
		Line oldLine = new Line();
		Line newLine = new Line();
		JApiChangeStatus status = field.getChangeStatus();
		annotationLines(field.getAnnotations(), status, pkg, oldLine, newLine, true);
		modifiers(field, status, oldLine, newLine);
		JApiType type = field.getType();
		String oldType = type.getOldTypeOptional().map(t -> t + generics(field.getOldGenericTypes())).orElse("");
		String newType = type.getNewTypeOptional().map(t -> t + generics(field.getNewGenericTypes())).orElse("");
		token(status, oldType, newType, pkg, oldLine, newLine);
		oldLine.space();
		newLine.space();
		oldLine.add(escape(field.getName()) + ";", null);
		newLine.add(escape(field.getName()) + ";", null);
		row.diff = diff(status, oldLine, newLine);
		return row;
	}

	private Row behaviorRow(JApiBehavior behavior, int group, String kind, String simpleName, String pkg) {
		String params = behavior.getParameters().stream()
			.map(p -> simpleTypes(p.getType() + generics(behavior.getChangeStatus() == JApiChangeStatus.REMOVED ? p.getOldGenericTypes() : p.getNewGenericTypes())))
			.collect(Collectors.joining(", "));
		// constructors of nested classes are shown with their source name: Inner() instead of Outer$Inner()
		String name = "constructor".equals(kind) ? behavior.getName().substring(behavior.getName().lastIndexOf('$') + 1) : behavior.getName();
		String label = name + "(" + params + ")";
		String idSeed = simpleName + "-" + ("constructor".equals(kind) ? "init" : behavior.getName());
		Row row = row(group, idSeed, Severity.of(behavior), behavior.getChangeStatus(), kind, label, behavior);
		String oldNumber = behavior.getOldLineNumberAsString();
		String newNumber = behavior.getNewLineNumberAsString();
		if (!N_A.equals(oldNumber) && !N_A.equals(newNumber) && !oldNumber.equals(newNumber)) {
			row.lineNumbers = "L" + oldNumber + " → L" + newNumber;
		} else if (!N_A.equals(newNumber)) {
			row.lineNumbers = "L" + newNumber;
		} else if (!N_A.equals(oldNumber)) {
			row.lineNumbers = "L" + oldNumber;
		}

		JApiChangeStatus status = behavior.getChangeStatus();
		Line oldLine = new Line();
		Line newLine = new Line();
		annotationLines(behavior.getAnnotations(), status, pkg, oldLine, newLine, true);
		modifiers(behavior, status, oldLine, newLine);
		if (!behavior.getGenericTemplates().isEmpty()) {
			oldLine.space();
			newLine.space();
			genericTemplates(behavior.getGenericTemplates(), status, pkg, oldLine, newLine);
		}
		if (behavior instanceof JApiMethod) {
			JApiReturnType returnType = ((JApiMethod) behavior).getReturnType();
			String oldType = N_A.equals(returnType.getOldReturnType()) ? "" : returnType.getOldReturnType() + generics(returnType.getOldGenericTypes());
			String newType = N_A.equals(returnType.getNewReturnType()) ? "" : returnType.getNewReturnType() + generics(returnType.getNewGenericTypes());
			token(status, oldType, newType, pkg, oldLine, newLine);
		}
		oldLine.space();
		newLine.space();
		oldLine.add(escape(name) + "(", null);
		newLine.add(escape(name) + "(", null);
		boolean first = true;
		for (JApiParameter parameter : behavior.getParameters()) {
			if (!first) {
				oldLine.add(", ", null);
				newLine.add(", ", null);
			}
			first = false;
			String oldType = parameter.getType() + generics(parameter.getOldGenericTypes());
			String newType = parameter.getType() + generics(parameter.getNewGenericTypes());
			if (parameter.getOldGenericTypes().isEmpty() && status != JApiChangeStatus.REMOVED) {
				oldType = newType;
			}
			if (parameter.getNewGenericTypes().isEmpty() && status == JApiChangeStatus.REMOVED) {
				newType = oldType;
			}
			boolean changed = isChangeVisible(status) && !oldType.equals(newType);
			oldLine.add(types(oldType, pkg), changed ? "del" : null);
			newLine.add(types(newType, pkg), changed ? "ins" : null);
		}
		oldLine.add(")", null);
		newLine.add(")", null);
		exceptions(behavior.getExceptions(), status, pkg, oldLine, true);
		exceptions(behavior.getExceptions(), status, pkg, newLine, false);
		row.diff = diff(status, oldLine, newLine);
		return row;
	}

	private void exceptions(List<JApiException> exceptions, JApiChangeStatus memberStatus, String pkg, Line line, boolean oldSide) {
		List<JApiException> visible = exceptions.stream()
			.filter(e -> oldSide ? e.getChangeStatus() != JApiChangeStatus.NEW : e.getChangeStatus() != JApiChangeStatus.REMOVED)
			.collect(Collectors.toList());
		if (visible.isEmpty()) {
			return;
		}
		String mark = oldSide ? "del" : "ins";
		boolean allChanged = isChangeVisible(memberStatus) && visible.stream().allMatch(e -> e.getChangeStatus() != JApiChangeStatus.UNCHANGED);
		line.add(" ", null);
		line.add("throws ", allChanged ? mark : null);
		boolean first = true;
		for (JApiException exception : visible) {
			if (!first) {
				line.add(", ", allChanged ? mark : null);
			}
			first = false;
			boolean changed = isChangeVisible(memberStatus) && exception.getChangeStatus() != JApiChangeStatus.UNCHANGED;
			line.add(types(exception.getName(), pkg), changed ? mark : null);
		}
	}

	private void modifiers(JApiHasModifiers hasModifiers, JApiChangeStatus memberStatus, Line oldLine, Line newLine) {
		List<String[]> tokens = new ArrayList<>();
		for (JApiModifier<? extends Enum<? extends Enum<?>>> modifier : hasModifiers.getModifiers()) {
			String oldValue = "";
			String newValue = "";
			switch (modifier.getChangeStatus()) {
				case NEW:
					newValue = keyword(modifier.getValueNew());
					break;
				case REMOVED:
					oldValue = keyword(modifier.getValueOld());
					break;
				case UNCHANGED:
					oldValue = keyword(modifier.getValueNew());
					newValue = oldValue;
					break;
				case MODIFIED:
					oldValue = keyword(modifier.getValueOld());
					newValue = keyword(modifier.getValueNew());
					break;
			}
			if (memberStatus == JApiChangeStatus.NEW) {
				oldValue = newValue;
			} else if (memberStatus == JApiChangeStatus.REMOVED) {
				newValue = oldValue;
			}
			if (!oldValue.isEmpty() || !newValue.isEmpty()) {
				tokens.add(new String[]{oldValue, newValue});
			}
		}
		tokens.sort(Comparator.comparingInt(t -> {
			int i = MODIFIER_ORDER.indexOf(t[1].isEmpty() ? t[0] : t[1]);
			return i < 0 ? 99 : i;
		}));
		for (String[] token : tokens) {
			boolean changed = !token[0].equals(token[1]);
			if (!token[0].isEmpty()) {
				oldLine.space();
				oldLine.add(token[0], changed ? "del" : null);
			}
			if (!token[1].isEmpty()) {
				newLine.space();
				newLine.add(token[1], changed ? "ins" : null);
			}
		}
	}

	private static String keyword(String value) {
		if (value == null) {
			return "";
		}
		String lower = value.toLowerCase(Locale.ROOT);
		return MODIFIER_ORDER.contains(lower) ? lower : "";
	}

	/** Adds a type (return type, field type) that may differ between old and new. */
	private void token(JApiChangeStatus memberStatus, String oldValue, String newValue, String pkg, Line oldLine, Line newLine) {
		if (memberStatus == JApiChangeStatus.NEW) {
			oldValue = newValue;
		} else if (memberStatus == JApiChangeStatus.REMOVED) {
			newValue = oldValue;
		}
		boolean changed = isChangeVisible(memberStatus) && !oldValue.equals(newValue);
		if (!oldValue.isEmpty()) {
			oldLine.space();
			oldLine.add(types(oldValue, pkg), changed ? "del" : null);
		}
		if (!newValue.isEmpty()) {
			newLine.space();
			newLine.add(types(newValue, pkg), changed ? "ins" : null);
		}
	}

	private void genericTemplates(List<JApiGenericTemplate> templates, JApiChangeStatus memberStatus, String pkg, Line oldLine, Line newLine) {
		List<String> oldParts = new ArrayList<>();
		List<String> newParts = new ArrayList<>();
		List<Boolean> changed = new ArrayList<>();
		for (JApiGenericTemplate template : templates) {
			String oldPart = template.getOldTypeOptional().map(t -> templateText(template.getName(), t, template.getOldGenericTypes(), template.getOldInterfaceTypes())).orElse("");
			String newPart = template.getNewTypeOptional().map(t -> templateText(template.getName(), t, template.getNewGenericTypes(), template.getNewInterfaceTypes())).orElse("");
			if (memberStatus == JApiChangeStatus.NEW) {
				oldPart = newPart;
			} else if (memberStatus == JApiChangeStatus.REMOVED) {
				newPart = oldPart;
			}
			oldParts.add(oldPart);
			newParts.add(newPart);
			changed.add(isChangeVisible(memberStatus) && !oldPart.equals(newPart));
		}
		appendTemplates(oldLine, oldParts, changed, "del", pkg);
		appendTemplates(newLine, newParts, changed, "ins", pkg);
	}

	private void appendTemplates(Line line, List<String> parts, List<Boolean> changed, String mark, String pkg) {
		boolean any = parts.stream().anyMatch(p -> !p.isEmpty());
		if (!any) {
			return;
		}
		line.add("&lt;", null);
		boolean first = true;
		for (int i = 0; i < parts.size(); i++) {
			if (parts.get(i).isEmpty()) {
				continue;
			}
			if (!first) {
				line.add(", ", null);
			}
			first = false;
			line.add(types(parts.get(i), pkg), changed.get(i) ? mark : null);
		}
		line.add("&gt;", null);
	}

	private String templateText(String name, String type, List<JApiGenericType> genericTypes, List<JApiGenericType> interfaceTypes) {
		StringBuilder sb = new StringBuilder(name);
		List<String> bounds = new ArrayList<>();
		if (!"java.lang.Object".equals(type)) {
			bounds.add(type + generics(genericTypes));
		}
		for (JApiGenericType interfaceType : interfaceTypes) {
			bounds.add(interfaceType.getType() + generics(interfaceType.getGenericTypes()));
		}
		if (!bounds.isEmpty()) {
			sb.append(" extends ").append(String.join(" & ", bounds));
		}
		return sb.toString();
	}

	private void annotationLines(List<JApiAnnotation> annotations, JApiChangeStatus memberStatus, String pkg, Line oldLine, Line newLine, boolean asPrefix) {
		for (JApiAnnotation annotation : annotations) {
			String oldText = annotation.getChangeStatus() == JApiChangeStatus.NEW ? "" : annotationText(annotation, true);
			String newText = annotation.getChangeStatus() == JApiChangeStatus.REMOVED ? "" : annotationText(annotation, false);
			if (memberStatus == JApiChangeStatus.NEW) {
				oldText = newText;
			} else if (memberStatus == JApiChangeStatus.REMOVED) {
				newText = oldText;
			}
			boolean changed = isChangeVisible(memberStatus) && !oldText.equals(newText);
			if (!oldText.isEmpty()) {
				oldLine.add(types(oldText, pkg), changed ? "del" : null);
				if (asPrefix) {
					oldLine.add(" ", null);
				}
			}
			if (!newText.isEmpty()) {
				newLine.add(types(newText, pkg), changed ? "ins" : null);
				if (asPrefix) {
					newLine.add(" ", null);
				}
			}
		}
	}

	private String annotationText(JApiAnnotation annotation, boolean oldSide) {
		List<String> elements = new ArrayList<>();
		for (JApiAnnotationElement element : annotation.getElements()) {
			List<JApiAnnotationElementValue> values = oldSide ? element.getOldElementValues() : element.getNewElementValues();
			if (values.isEmpty()) {
				continue;
			}
			String value = values.stream().map(this::annotationValue).collect(Collectors.joining(", "));
			elements.add(element.getName() + "=" + (values.size() > 1 ? "{" + value + "}" : value));
		}
		return "@" + annotation.getFullyQualifiedName() + (elements.isEmpty() ? "" : "(" + String.join(", ", elements) + ")");
	}

	private String annotationValue(JApiAnnotationElementValue value) {
		switch (value.getType()) {
			case Annotation:
				return "@" + value.getFullyQualifiedName() + "(" + value.getValues().stream().map(this::annotationValue).collect(Collectors.joining(", ")) + ")";
			case Array:
				return "{" + value.getValues().stream().map(this::annotationValue).collect(Collectors.joining(", ")) + "}";
			case Enum:
				return value.getFullyQualifiedName() + "." + value.getValue();
			default:
				return value.getValueString();
		}
	}

	private Severity worst(List<? extends JApiCompatibility> elements) {
		Severity worst = Severity.COMPATIBLE;
		for (JApiCompatibility element : elements) {
			Severity severity = Severity.of(element);
			if (severity.ordinal() < worst.ordinal()) {
				worst = severity;
			}
		}
		return worst;
	}

	private static boolean isChangeVisible(JApiChangeStatus memberStatus) {
		return memberStatus == JApiChangeStatus.MODIFIED || memberStatus == JApiChangeStatus.UNCHANGED;
	}

	/* ------------------------------------------------------------------ generics and types */

	private String generics(List<JApiGenericType> genericTypes) {
		if (genericTypes == null || genericTypes.isEmpty()) {
			return "";
		}
		return "<" + genericTypes.stream().map(this::genericType).collect(Collectors.joining(", ")) + ">";
	}

	private String genericType(JApiGenericType type) {
		String inner = type.getType() + generics(type.getGenericTypes());
		switch (type.getGenericWildCard()) {
			case EXTENDS:
				return "? extends " + inner;
			case SUPER:
				return "? super " + inner;
			case UNBOUNDED:
				return "?";
			default:
				return inner;
		}
	}

	/** Escapes the text and shortens type names from the same package and java.lang. */
	private String types(String text, String pkg) {
		String escaped = escape(text);
		Matcher matcher = QUALIFIED_NAME.matcher(escaped);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String typePackage = matcher.group(1).substring(0, matcher.group(1).length() - 1);
			String replacement = matcher.group(0);
			if (typePackage.equals(pkg) || "java.lang".equals(typePackage)) {
				replacement = "<abbr title=\"" + matcher.group(0) + "\">" + matcher.group(2) + "</abbr>";
			}
			matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	private static String simpleTypes(String text) {
		return QUALIFIED_NAME.matcher(text).replaceAll("$2");
	}

	/* ------------------------------------------------------------------ rendering */

	/** One side of a signature diff, made of segments that are plain, inserted or deleted. */
	private static class Line {
		private final List<String[]> segments = new ArrayList<>();

		void add(String html, String mark) {
			if (!segments.isEmpty()) {
				String[] last = segments.get(segments.size() - 1);
				if (Objects.equals(last[1], mark)) {
					last[0] += html;
					return;
				}
			}
			segments.add(new String[]{html, mark});
		}

		void space() {
			if (!segments.isEmpty()) {
				String last = segments.get(segments.size() - 1)[0];
				if (!last.endsWith(" ") && !last.endsWith("&lt;") && !last.endsWith("(")) {
					add(" ", null);
				}
			}
		}

		String html() {
			StringBuilder sb = new StringBuilder();
			for (String[] segment : segments) {
				if (segment[1] == null) {
					sb.append(segment[0]);
				} else {
					sb.append('<').append(segment[1]).append('>').append(segment[0]).append("</").append(segment[1]).append('>');
				}
			}
			return sb.toString();
		}
	}

	private String diff(JApiChangeStatus status, Line oldLine, Line newLine) {
		String oldHtml = oldLine.html();
		String newHtml = newLine.html();
		switch (status) {
			case NEW:
				return "<span class=\"ln plus\"><span class=\"sig\">+</span>" + newHtml + "</span>";
			case REMOVED:
				return "<span class=\"ln minus\"><span class=\"sig\">−</span>" + oldHtml + "</span>";
			default:
				if (oldHtml.equals(newHtml)) {
					return "<span class=\"ln\"><span class=\"sig\"> </span>" + newHtml + "</span>";
				}
				return "<span class=\"ln minus\"><span class=\"sig\">−</span>" + oldHtml + "</span>"
					+ "<span class=\"ln plus\"><span class=\"sig\">+</span>" + newHtml + "</span>";
		}
	}

	private String classHtml(ClassEntry entry) {
		JApiClass jApiClass = entry.jApiClass;
		String fqn = jApiClass.getFullyQualifiedName();
		String pkg = packageOf(fqn);
		int binary = 0;
		int source = 0;
		Map<JApiChangeStatus, Integer> byStatus = new EnumMap<>(JApiChangeStatus.class);
		for (Row row : entry.rows) {
			if (row.severity == Severity.BINARY) {
				binary++;
			} else if (row.severity == Severity.SOURCE) {
				source++;
			}
			byStatus.merge(row.status, 1, Integer::sum);
		}
		List<String> statusParts = new ArrayList<>();
		if (byStatus.containsKey(JApiChangeStatus.MODIFIED)) {
			statusParts.add(byStatus.get(JApiChangeStatus.MODIFIED) + " changed");
		}
		if (byStatus.containsKey(JApiChangeStatus.REMOVED)) {
			statusParts.add(byStatus.get(JApiChangeStatus.REMOVED) + " removed");
		}
		if (byStatus.containsKey(JApiChangeStatus.NEW)) {
			statusParts.add(byStatus.get(JApiChangeStatus.NEW) + " new");
		}
		if (byStatus.containsKey(JApiChangeStatus.UNCHANGED)) {
			statusParts.add(byStatus.get(JApiChangeStatus.UNCHANGED) + " unchanged");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("<details class=\"cls\" id=\"").append(entry.id).append("\" data-q=\"").append(escape(fqn.toLowerCase(Locale.ROOT))).append("\">\n<summary>");
		sb.append("<svg class=\"chev\" viewBox=\"0 0 16 16\" aria-hidden=\"true\"><path d=\"M5 3l5 5-5 5\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"/></svg>");
		if (entry.severity != Severity.COMPATIBLE) {
			sb.append(badge(entry.severity, null));
		}
		sb.append("<span class=\"badge kind ").append(statusCss(jApiClass.getChangeStatus())).append("\">")
			.append(jApiClass.getChangeStatus().name().toLowerCase(Locale.ROOT)).append(" <span class=\"ctype\">").append(escape(classType(jApiClass.getClassType()))).append("</span></span>");
		sb.append("<span class=\"cmods\">").append(classModifiers(jApiClass)).append("</span>");
		sb.append("<span class=\"cname\"><span class=\"pkg\">").append(escape(pkg.isEmpty() ? "" : pkg + ".")).append("</span>").append(escape(simpleName(fqn))).append("</span>");
		sb.append("<span class=\"counts\">");
		if (binary > 0) {
			sb.append(badge(Severity.BINARY, binary + " binary"));
		}
		if (source > 0) {
			sb.append(badge(Severity.SOURCE, source + " source"));
		}
		if (!statusParts.isEmpty()) {
			sb.append("<span>").append(String.join(" · ", statusParts)).append("</span>");
		}
		sb.append("</span></summary>\n");

		List<String> meta = new ArrayList<>();
		JApiClassFileFormatVersion version = jApiClass.getClassFileFormatVersion();
		if (version.getChangeStatus() == JApiChangeStatus.MODIFIED) {
			meta.add("<span class=\"chip classfile\" data-old=\"" + version.getMajorVersionOld() + "." + version.getMinorVersionOld()
				+ "\" data-new=\"" + version.getMajorVersionNew() + "." + version.getMinorVersionNew() + "\"><span class=\"k\">class file</span> "
				+ version.getMajorVersionOld() + " → " + version.getMajorVersionNew()
				+ "</span><span>Java " + javaVersion(version.getMajorVersionOld()) + " → Java " + javaVersion(version.getMajorVersionNew()) + "</span>");
		} else if (version.getMajorVersionNew() > 0) {
			meta.add("<span class=\"chip\"><span class=\"k\">class file</span> " + version.getMajorVersionNew() + "</span>");
		}
		JApiSuperclass superclass = jApiClass.getSuperclass();
		if (superclass.getChangeStatus() == JApiChangeStatus.UNCHANGED && Severity.of(superclass) == Severity.COMPATIBLE
			&& superclass.getNewSuperclassName().isPresent() && !"java.lang.Object".equals(superclass.getNewSuperclassName().get())) {
			meta.add("<span class=\"chip\" data-kind=\"superclass\"><span class=\"k\">extends</span> " + escape(superclass.getNewSuperclassName().get()) + "</span>");
		}
		for (JApiImplementedInterface implementedInterface : jApiClass.getInterfaces()) {
			if (implementedInterface.getChangeStatus() == JApiChangeStatus.UNCHANGED && Severity.of(implementedInterface) == Severity.COMPATIBLE) {
				meta.add("<span class=\"chip\" data-kind=\"interface\"><span class=\"k\">implements</span> " + escape(implementedInterface.getFullyQualifiedName()) + "</span>");
			}
		}
		for (JApiCompatibilityChange change : jApiClass.getCompatibilityChanges()) {
			meta.add("<span class=\"tag\">" + change.getType().name() + "</span>");
		}
		if (!meta.isEmpty()) {
			sb.append("<div class=\"classmeta\">").append(String.join("", meta)).append("</div>\n");
		}

		for (int group = 0; group < GROUPS.length; group++) {
			final int g = group;
			List<Row> rows = entry.rows.stream().filter(r -> r.group == g).collect(Collectors.toList());
			if (rows.isEmpty()) {
				continue;
			}
			sb.append("<section class=\"group\"><h3 class=\"ghead\">").append(GROUPS[group]).append("<span class=\"gcount\">").append(rows.size()).append("</span></h3>\n");
			for (Row row : rows) {
				sb.append(rowHtml(row, fqn));
			}
			sb.append("</section>\n");
		}
		sb.append("</details>\n");
		return sb.toString();
	}

	private String rowHtml(Row row, String fqn) {
		String search = (fqn + " " + row.label + " " + String.join(" ", row.compatibilityChanges)).toLowerCase(Locale.ROOT);
		StringBuilder sb = new StringBuilder();
		sb.append("<article class=\"change ").append(row.severity.css).append("\" id=\"").append(row.id)
			.append("\" data-kind=\"").append(row.kind.replace(' ', '-')).append("\" data-status=\"").append(row.status.name()).append("\" data-sev=\"").append(row.severity.css)
			.append("\" data-q=\"").append(escape(search)).append("\">\n<div class=\"crow\">");
		if (row.severity != Severity.COMPATIBLE) {
			sb.append(badge(row.severity, row.badgeText));
		}
		sb.append(kindBadge(row));
		sb.append("<span class=\"member\">").append(escape(row.label)).append("</span>");
		for (String change : row.compatibilityChanges) {
			sb.append("<span class=\"tag\">").append(change).append("</span>");
		}
		sb.append("<span class=\"where\">");
		if (!row.lineNumbers.isEmpty()) {
			sb.append("<span>").append(row.lineNumbers).append("</span>");
		}
		sb.append("<a href=\"#").append(row.id).append("\" aria-label=\"Link to this change\">#</a></span></div>\n");
		sb.append("<div class=\"diff\"><pre>").append(row.diff).append("</pre></div>\n</article>\n");
		return sb.toString();
	}

	private String kindBadge(Row row) {
		if ("serialization".equals(row.kind) || "type parameters".equals(row.kind)) {
			return "<span class=\"badge kind modified\">" + row.kind + "</span>";
		}
		String verb;
		switch (row.status) {
			case NEW:
				verb = "new ";
				break;
			case REMOVED:
				verb = "removed ";
				break;
			case MODIFIED:
				verb = "changed ";
				break;
			default:
				verb = "";
		}
		return "<span class=\"badge kind " + statusCss(row.status) + "\">" + verb + row.kind + "</span>";
	}

	private static String statusCss(JApiChangeStatus status) {
		switch (status) {
			case NEW:
				return "new";
			case REMOVED:
				return "removed";
			case MODIFIED:
				return "modified";
			default:
				return "";
		}
	}

	private String badge(Severity severity, String text) {
		return "<span class=\"badge " + severity.css + "\"><span class=\"ico\">" + severity.icon + "</span>" + escape(text != null ? text : severity.label) + "</span>";
	}

	private String classModifiers(JApiClass jApiClass) {
		Line oldLine = new Line();
		Line newLine = new Line();
		modifiers(jApiClass, jApiClass.getChangeStatus(), oldLine, newLine);
		String html = jApiClass.getChangeStatus() == JApiChangeStatus.REMOVED ? oldLine.html() : newLine.html();
		return html.replaceAll("</?(ins|del)>", "");
	}

	private static String classType(JApiClassType classType) {
		switch (classType.getChangeStatus()) {
			case MODIFIED:
				return classType.getNewType().toLowerCase(Locale.ROOT) + " (was " + classType.getOldType().toLowerCase(Locale.ROOT) + ")";
			case REMOVED:
				return classType.getOldType().toLowerCase(Locale.ROOT);
			default:
				return classType.getNewType().toLowerCase(Locale.ROOT);
		}
	}

	private static String javaVersion(int major) {
		if (major <= 0) {
			return "?";
		}
		if (major <= 48) {
			return "1." + (major - 44);
		}
		return String.valueOf(major - 44);
	}

	/* ------------------------------------------------------------------ helpers */

	private String uniqueId(String seed) {
		String base = seed.replaceAll("[^A-Za-z0-9]+", "-").replaceAll("^-|-$", "");
		if (base.isEmpty()) {
			base = "c";
		}
		String id = base;
		int i = 2;
		while (!usedIds.add(id)) {
			id = base + "-" + i++;
		}
		return id;
	}

	private static String packageOf(String fqn) {
		int i = fqn.lastIndexOf('.');
		return i < 0 ? "" : fqn.substring(0, i);
	}

	private static String simpleName(String fqn) {
		return fqn.substring(fqn.lastIndexOf('.') + 1);
	}

	static String escape(String text) {
		if (text == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder(text.length());
		for (char c : text.toCharArray()) {
			switch (c) {
				case '&':
					sb.append("&amp;");
					break;
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '"':
					sb.append("&quot;");
					break;
				default:
					sb.append(c);
			}
		}
		return sb.toString();
	}

	private String getStyle() {
		String style = templateEngine.loadTemplate("/html/report.css");
		if (options.getHtmlStylesheet().isPresent()) {
			try (InputStream inputStream = new FileInputStream(options.getHtmlStylesheet().get())) {
				style += "\n/* custom stylesheet */\n" + Streams.asString(inputStream);
			} catch (IOException e) {
				throw new JApiCmpException(JApiCmpException.Reason.IoException, "Failed to load stylesheet: " + e.getMessage(), e);
			}
		}
		return style;
	}

	private String getTitle() {
		return htmlOutputGeneratorOptions.getTitle().orElse("japicmp-Report");
	}
}
