package japicmp.output.markdown;

import static japicmp.model.JApiChangeStatus.*;
import static japicmp.output.markdown.Markdown.*;
import static japicmp.util.JApiClassFileFormatVersionHelper.*;
import static japicmp.util.MemberValueHelper.formatMemberValue;
import static japicmp.util.ModifierHelper.*;
import static japicmp.util.OptionalHelper.N_A;
import static japicmp.util.TypeNameHelper.*;
import static java.lang.String.format;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

import japicmp.cmp.JApiCmpArchive;
import japicmp.config.*;
import japicmp.filter.Filter;
import japicmp.model.*;
import japicmp.model.JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus;
import japicmp.output.*;
import japicmp.output.markdown.config.MarkdownOptions;
import japicmp.output.semver.SemverOut;
import japicmp.util.Optional;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javassist.bytecode.annotation.MemberValue;

public class MarkdownOutputGenerator extends OutputGenerator<String> {

	final MarkdownOptions md;
	final MarkdownReferences references = new MarkdownReferences();

	public MarkdownOutputGenerator(MarkdownOptions mdOptions, List<JApiClass> jApiClasses) {
		super(mdOptions.options, jApiClasses);
		md = mdOptions;
	}

	public MarkdownOutputGenerator(Options options, List<JApiClass> jApiClasses) {
		this(MarkdownOptions.newDefault(options), jApiClasses);
	}

	@Override
	public String generate() {
		return toString();
	}

	@Override
	public String toString() {
		final String semver = new SemverOut(options, jApiClasses).generate();
		return renderHeading(0, md.title.report) +
			md.message.getSemverBadge(semver) + EOL +
			renderHeading(1, md.title.summary) +
			renderReportSummary(md.message.getSummaryMessage(semver), options) + PARAGRAPH +
			renderHtmlDetails(md.message.expandOptions, renderReportOptions(options)) +
			renderReportResults(options) + EOL +
			renderMissingClassesWarning(options.getIgnoreMissingClasses()) +
			MARKDOWN_HORIZONTAL_RULE + PARAGRAPH +
			format(md.message.generatedOn, md.message.getCurrentTimestamp()) + PARAGRAPH +
			references + EOL;
	}

	private String renderHeading(int level, String text) {
		final StringBuilder tmp = new StringBuilder();
		tmp.append(EOL);
		for (int repeat = 0; repeat < md.title.topHeadingLevel + level && repeat < 6; repeat++) {
			tmp.append(HASH);
		}
		tmp.append(" ");
		tmp.append(text);
		tmp.append(PARAGRAPH);
		return tmp.toString();
	}

	private String renderReportSummary(String summary, Options options) {
		final String newVersion = renderArchivesVersion(md.targetNewVersion, options.getNewArchives(), md.message.oneNewVersion, md.message.manyNewArchives);
		final String oldVersion = renderArchivesVersion(md.targetOldVersion, options.getOldArchives(), md.message.oneOldVersion, md.message.manyOldArchives);
		return format(summary, newVersion, oldVersion);
	}

	private String renderReportOptions(Options options) {
		final List<Pattern> patterns = options.getIgnoreMissingClasses().getIgnoreMissingClassRegularExpression();
		return new MarkdownList(
			format(md.message.reportOnlyChanges, md.message.yesNo(options.isOutputOnlyModifications())),
			format(md.message.reportOnlyBinaryIncompatibleChanges, md.message.yesNo(options.isOutputOnlyBinaryIncompatibleModifications())),
			format(md.message.accessModifierFilter, options.getAccessModifier()),
			format(md.message.oldArchives, new MarkdownList(1, options.getOldArchives().stream().map(this::renderArchive))),
			format(md.message.newArchives, new MarkdownList(1, options.getNewArchives().stream().map(this::renderArchive))),
			format(md.message.evaluateAnnotations, md.message.yesNo(!options.isNoAnnotations())),
			format(md.message.includeSynthetic, md.message.yesNo(options.isIncludeSynthetic())),
			format(md.message.includeSpecificElements, md.message.yesNo(!options.getIncludes().isEmpty()) + new MarkdownList(1, options.getIncludes().stream().filter(Objects::nonNull).map(Filter::toString).distinct().map(this::renderCode))),
			format(md.message.excludeSpecificElements, md.message.yesNo(!options.getExcludes().isEmpty()) + new MarkdownList(1, options.getExcludes().stream().filter(Objects::nonNull).map(Filter::toString).distinct().map(this::renderCode))),
			format(md.message.ignoreAllMissingClasses, md.message.yesNo(options.getIgnoreMissingClasses().isIgnoreAllMissingClasses())),
			format(md.message.ignoreSpecificMissingClasses, md.message.yesNo(!patterns.isEmpty()) + new MarkdownList(1, patterns.stream().map(Pattern::pattern).map(this::renderCode))),
			format(md.message.treatChangesAsErrors, new MarkdownList(1,
				format(md.message.anyChanges, md.message.yesNo(options.isErrorOnModifications())),
				format(md.message.binaryIncompatibleChanges, md.message.yesNo(options.isErrorOnBinaryIncompatibility())),
				format(md.message.sourceIncompatibleChanges, md.message.yesNo(options.isErrorOnSourceIncompatibility())),
				format(md.message.incompatibleChangesCausedByExcludedClasses, md.message.yesNo(options.isErrorOnExclusionIncompatibility())),
				format(md.message.semanticallyIncompatibleChanges, md.message.yesNo(options.isErrorOnSemanticIncompatibility())),
				format(md.message.semanticallyIncompatibleChangesIncludingDevelopmentVersions, md.message.yesNo(options.isErrorOnSemanticIncompatibilityForMajorVersionZero())))
			),
			format(md.message.classpathMode, options.getClassPathMode()),
			format(md.message.oldClasspath, options.getOldClassPath().or(EMPTY)),
			format(md.message.newClasspath, options.getNewClassPath().or(EMPTY))
		) + EOL;
	}

	private String renderArchivesVersion(Optional<String> version, List<JApiCmpArchive> archives, String one, String many) {
		if (version.isPresent()) {
			return format(one, version.get());
		}
		if (archives.isEmpty()) {
			return format(one, md.message.unknownVersion);
		}
		if (archives.size() == 1) {
			final JApiCmpArchive archive = archives.get(0);
			final String archiveVersion = archive.getVersion().getStringVersion();
			if (archiveVersion != null && !archiveVersion.equals(N_A)) {
				return format(one, archiveVersion);
			}
			return format(one, renderSimpleArchiveName(archive));
		}
		return many;
	}

	private String renderReportResults(Options options) {
		new OutputFilter(options).filter(jApiClasses);
		if (jApiClasses.isEmpty()) {
			return EMPTY;
		}
		return new MarkdownSection<>(renderHeading(1, md.title.results), jApiClasses, md.sort.classes)
			.column(md.header.status, this::renderStatus)
			.column(md.header.type, this::renderClassLink)
			.column(md.header.serialization, this::renderSerializationChange)
			.column(md.header.compatibilityChanges, this::renderAllCompatibilityChanges) +
			(md.options.isReportOnlySummary() ? "" : renderHtmlDetails(md.message.expandResults, jApiClasses.stream().sorted(md.sort.classes).map(this::renderClass).collect(joining())));
	}

	private String renderMissingClassesWarning(IgnoreMissingClasses missing) {
		if (missing.isIgnoreAllMissingClasses()) {
			return md.message.warningAllMissingClassesIgnored;
		} else if (!missing.getIgnoreMissingClassRegularExpression().isEmpty()) {
			return md.message.warningSomeMissingClassesIgnored;
		}
		return EMPTY;
	}

	private String renderClass(JApiClass clazz) {
		final String name = clazz.getFullyQualifiedName();
		return MARKDOWN_HORIZONTAL_RULE + PARAGRAPH +
			renderHtmlAnchor(name) +
			renderHeading(2, renderCode(name)) +
			renderCompatibilityList(clazz) + PARAGRAPH +
			renderClassInfo(clazz) +
			renderGenericTemplates(clazz) +
			renderImplementedInterfaces(clazz) +
			renderAnnotations(clazz) +
			renderConstructors(clazz) +
			renderMethods(clazz) +
			renderFields(clazz);
	}

	private String renderHtmlAnchor(String id) {
		return angles("a" + SPACE + "id" + EQUAL + quotes(renderSlug(id))) + angles("/a");
	}

	private String renderHtmlDetails(String summary, String details) {
		return angles("details" + SPACE + "markdown" + EQUAL + quotes("1")) + EOL +
			angles("summary") + summary + angles("/summary") + PARAGRAPH +
			details + EOL +
			angles("/details") + PARAGRAPH;
	}

	private String renderSlug(String id) {
		return "user-content-" + id.toLowerCase();
	}

	private Markdown renderCompatibilityList(JApiClass clazz) {
		return new MarkdownList(
			format(md.message.compatibilityBinary, md.message.checkbox(clazz.isBinaryCompatible())),
			format(md.message.compatibilitySource, md.message.checkbox(clazz.isSourceCompatible())),
			format(md.message.compatibilitySerialization, md.message.checkbox(
				!clazz.getJavaObjectSerializationCompatible().isIncompatible())));
	}

	private Markdown renderClassInfo(JApiClass clazz) {
		return new MarkdownSection<>(EMPTY, clazz)
			.column(md.header.status, this::renderStatus)
			.column(md.header.modifiers, this::renderModifiers)
			.column(md.header.classType, this::renderClassType)
			.column(md.header.className, this::renderClassSimpleName)
			.column(md.header.superclass, this::renderClassSuperclass)
			.column(md.header.classJdk, this::renderClassJdk)
			.column(md.header.serialization, this::renderSerializationChange)
			.column(md.header.compatibilityChanges, this::renderClassLevelCompatibilityChanges);
	}

	private Markdown renderGenericTemplates(JApiClass clazz) {
		return new MarkdownSection<>(renderHeading(3, md.title.generics), clazz.getGenericTemplates(), md.sort.generics)
			.column(md.header.status, this::renderStatus)
			.column(md.header.genericTemplateName, this::renderGenericTemplateName)
			.column(md.header.genericTemplateType, this::renderGenericTemplateType)
			.column(md.header.compatibilityChanges, this::renderCompatibilityChanges);
	}

	private Markdown renderImplementedInterfaces(JApiClass clazz) {
		return new MarkdownSection<>(renderHeading(3, md.title.interfaces), clazz.getInterfaces(), md.sort.interfaces)
			.column(md.header.status, this::renderStatus)
			.column(md.header.interfaceName, this::renderImplementedInterfaceName)
			.column(md.header.compatibilityChanges, this::renderCompatibilityChanges);
	}

	private Markdown renderAnnotations(JApiClass clazz) {
		return new MarkdownSection<>(renderHeading(3, md.title.annotations), clazz.getAnnotations(), md.sort.annotations)
			.column(md.header.status, this::renderStatus)
			.column(md.header.annotationName, this::renderAnnotation)
			.column(md.header.compatibilityChanges, this::renderAllCompatibilityChanges);
	}

	private Markdown renderConstructors(JApiClass clazz) {
		return new MarkdownSection<>(renderHeading(3, md.title.constructors), clazz.getConstructors(), md.sort.constructors)
			.column(md.header.status, this::renderStatus)
			.column(md.header.modifiers, this::renderModifiers)
			.column(md.header.generics, this::renderGenericTemplates)
			.column(md.header.constructorNameAndParameters, this::renderNameAndParameters)
			.column(md.header.annotations, this::renderInlineAnnotations)
			.column(md.header.exceptions, this::renderExceptions)
			.column(md.header.compatibilityChanges, this::renderAllCompatibilityChanges);
	}

	private Markdown renderMethods(JApiClass clazz) {
		return new MarkdownSection<>(renderHeading(3, md.title.methods), clazz.getMethods(), md.sort.methods)
			.column(md.header.status, this::renderStatus)
			.column(md.header.modifiers, this::renderModifiers)
			.column(md.header.generics, this::renderGenericTemplates)
			.column(md.header.methodReturnType, this::renderReturnType)
			.column(md.header.methodNameAndParameters, this::renderNameAndParameters)
			.column(md.header.annotations, this::renderInlineAnnotations)
			.column(md.header.exceptions, this::renderExceptions)
			.column(md.header.compatibilityChanges, this::renderAllCompatibilityChanges);
	}

	private Markdown renderFields(JApiClass clazz) {
		return new MarkdownSection<>(renderHeading(3, md.title.fields), clazz.getFields(), md.sort.fields)
			.column(md.header.status, this::renderStatus)
			.column(md.header.modifiers, this::renderModifiers)
			.column(md.header.fieldType, this::renderFieldType)
			.column(md.header.fieldName, this::renderFieldName)
			.column(md.header.annotations, this::renderInlineAnnotations)
			.column(md.header.compatibilityChanges, this::renderCompatibilityChanges);
	}

	private String renderStatus(JApiHasChangeStatus element) {
		final boolean isBinaryCompatible = ((JApiCompatibility) element).isBinaryCompatible();
		final boolean isSourceCompatible = ((JApiCompatibility) element).isSourceCompatible();
		final boolean isSerializationCompatible = !(element instanceof JApiJavaObjectSerializationCompatibility)
			|| !((JApiJavaObjectSerializationCompatibility) element).getJavaObjectSerializationCompatible().isIncompatible();
		final boolean isFullyCompatible = isBinaryCompatible && isSourceCompatible && isSerializationCompatible;
		if (element.getChangeStatus() != UNCHANGED || isFullyCompatible) {
			return renderLiteralStatus(element);
		} else if (!isBinaryCompatible && !isSourceCompatible) {
			return md.message.statusIncompatible;
		} else if (!isBinaryCompatible) {
			return md.message.statusBinaryIncompatible;
		} else if (!isSourceCompatible) {
			return md.message.statusSourceIncompatible;
		}
		return md.message.statusSerializationIncompatible;
	}

	private String renderLiteralStatus(JApiHasChangeStatus element) {
		switch (element.getChangeStatus()) {
			case NEW:
				return md.message.statusNew;
			case REMOVED:
				return md.message.statusRemoved;
			case UNCHANGED:
				return md.message.statusUnchanged;
			default:
			case MODIFIED:
				return md.message.statusModified;
		}
	}

	private String renderClassLink(JApiClass clazz) {
		final String name = clazz.getFullyQualifiedName();
		return md.options.isReportOnlySummary() ? name : references.link(HASH + renderSlug(name), name, null).toString();
	}

	private String renderClassSimpleName(JApiClass clazz) {
		final String simpleName = formatTypeName(clazz.getFullyQualifiedName(), emptyList(), true);
		return renderChange(clazz, renderCode(simpleName));
	}

	private String renderClassType(JApiClass clazz) {
		final JApiClassType classType = clazz.getClassType();
		return renderChange(classType, md.message.getClassType(classType.getOldTypeOptional()), md.message.getClassType(classType.getNewTypeOptional()));
	}

	private String renderClassSuperclass(JApiClass clazz) {
		final JApiSuperclass superclass = clazz.getSuperclass();
		final JApiClass correspondingClass = superclass.getCorrespondingJApiClass().or((JApiClass) null);
		return renderChange(superclass,
			renderTypeWithGenericTemplates(superclass.getOldSuperclassName().or((String) null), superclass, correspondingClass),
			renderTypeWithGenericTemplates(superclass.getNewSuperclassName().or((String) null), superclass, correspondingClass));
	}

	private String renderClassJdk(JApiClass clazz) {
		final JApiClassFileFormatVersion version = clazz.getClassFileFormatVersion();
		return renderChange(version, getOldJdkVersion(version), getNewJdkVersion(version));
	}

	private String renderGenericTemplateName(JApiGenericTemplate genericTemplate) {
		return renderChange(genericTemplate, renderCode(genericTemplate.getName()));
	}

	private String renderGenericTemplateType(JApiGenericTemplate genericTemplate) {
		return renderChange(genericTemplate,
			renderTypeWithGenericTypes(genericTemplate.getOldType(), genericTemplate.getOldGenericTypes()),
			renderTypeWithGenericTypes(genericTemplate.getNewType(), genericTemplate.getNewGenericTypes()));
	}

	private String renderImplementedInterfaceName(JApiImplementedInterface implInterface) {
		return renderChange(implInterface, renderTypeWithGenericTemplates(
			implInterface.getFullyQualifiedName(), implInterface, implInterface.getCorrespondingJApiClass().or((JApiClass) null)));
	}

	private String renderNameAndParameters(JApiBehavior behavior) {
		final String name = behavior.getName();
		final int pos = name != null ? name.lastIndexOf('$') : -1;
		final String simpleName = pos > 0 ? name.substring(pos + 1) : name;
		return renderChange(behavior, renderCode(simpleName)) + renderParameters(behavior);
	}

	private String renderParameters(JApiBehavior behavior) {
		return parenthesis(behavior.getParameters().stream().map(x -> renderParameter(behavior, x)).collect(CSV));
	}

	private String renderParameter(JApiBehavior method, JApiParameter parameter) {
		if (parameter.getTemplateNameOptional().isPresent()) {
			return renderChange(parameter, renderCode(parameter.getTemplateName()));
		}
		final JApiChangeStatus status = method.getChangeStatus();
		final JApiModifier<VarargsModifier> varargs = method.getVarargsModifier();
		final String oldType = (status == NEW) ? null : renderParameterType(method, parameter, varargs.getOldModifier(), parameter.getOldGenericTypes());
		final String newType = (status == REMOVED) ? null : renderParameterType(method, parameter, varargs.getNewModifier(), parameter.getNewGenericTypes());
		return renderChange(parameter, oldType, newType);
	}

	private String renderGenericTemplates(JApiBehavior behavior) {
		final List<JApiGenericTemplate> genericTemplates = behavior.getGenericTemplates();
		if (genericTemplates.isEmpty()) {
			return EMPTY;
		}
		return BACKSLASH + ANGLE_OPEN +
			genericTemplates.stream().map(this::renderGenericTemplate).collect(CSV) +
			BACKSLASH + ANGLE_CLOSE;
	}

	private String renderGenericTemplate(JApiGenericTemplate genericTemplate) {
		final String name = genericTemplate.getName();
		final String oldTemplate = renderGenericTemplate(name, genericTemplate.getOldTypeOptional().or((String) null), genericTemplate.getOldGenericTypes());
		final String newTemplate = renderGenericTemplate(name, genericTemplate.getNewTypeOptional().or((String) null), genericTemplate.getNewGenericTypes());
		return renderChange(genericTemplate, oldTemplate, newTemplate);
	}

	private String renderGenericTemplate(String name, String type, List<JApiGenericType> genericTypes) {
		final String fullType = formatGenericTemplate(name, type, genericTypes, false);
		final String simpleType = formatGenericTemplate(name, type, genericTypes, true);
		return renderCodeWithTooltip(fullType, simpleType);
	}

	private String renderCompatibilityChanges(List<JApiCompatibilityChange> changes) {
		if (changes.isEmpty()) {
			return renderNoChangesBadge();
		}
		return changes.stream().map(this::renderChangeBadge).collect(SPACES);
	}

	private String renderCompatibilityChanges(JApiCompatibility hasCompatibilityChanges) {
		return renderCompatibilityChanges(hasCompatibilityChanges.getCompatibilityChanges());
	}

	@SafeVarargs
	private final String renderCompatibilityChanges(List<? extends JApiCompatibility>... haveCompatibilityChanges) {
		return renderCompatibilityChanges(Stream.of(haveCompatibilityChanges).flatMap(List::stream)
			.map(JApiCompatibility::getCompatibilityChanges).flatMap(List::stream).distinct().collect(toList()));
	}

	private String renderAllCompatibilityChanges(JApiClass clazz) {
		return renderCompatibilityChanges(Stream.of(
				singletonList(clazz),
				singletonList(clazz.getClassFileFormatVersion()),
				singletonList(clazz.getSuperclass()),
				clazz.getGenericTemplates(),
				clazz.getInterfaces(),
				clazz.getAnnotations(),
				clazz.getAnnotations().stream().map(JApiAnnotation::getElements).flatMap(List::stream).collect(toList()),
				clazz.getConstructors(),
				clazz.getConstructors().stream().map(JApiConstructor::getParameters).flatMap(List::stream).collect(toList()),
				clazz.getMethods(),
				clazz.getMethods().stream().map(JApiMethod::getReturnType).collect(toList()),
				clazz.getMethods().stream().map(JApiMethod::getParameters).flatMap(List::stream).collect(toList()),
				clazz.getFields())
			.flatMap(List::stream)
			.map(JApiCompatibility::getCompatibilityChanges)
			.flatMap(List::stream)
			.distinct().sorted(Comparator.comparing(JApiCompatibilityChange::getType))
			.collect(toList()));
	}

	private String renderClassLevelCompatibilityChanges(JApiClass clazz) {
		return renderCompatibilityChanges(Arrays.asList(clazz, clazz.getClassFileFormatVersion(), clazz.getSuperclass()));
	}

	private String renderAllCompatibilityChanges(JApiAnnotation annotation) {
		return renderCompatibilityChanges(singletonList(annotation), annotation.getElements());
	}

	private String renderAllCompatibilityChanges(JApiConstructor constructor) {
		return renderCompatibilityChanges(singletonList(constructor), constructor.getParameters());
	}

	private String renderAllCompatibilityChanges(JApiMethod method) {
		return renderCompatibilityChanges(singletonList(method), singletonList(method.getReturnType()), method.getParameters());
	}

	private String renderNoChangesBadge() {
		return new MarkdownBadge(null, md.message.noCompatibilityChanges, md.message.colorNoChanges)
			.toRefImage(references).toString();
	}

	private String renderChangeBadge(JApiCompatibilityChange change) {
		final JApiCompatibilityChangeType type = change.getType();
		return new MarkdownBadge(null, md.message.compatibilityChangeType.getOrDefault(type, type.name()), md.message.getSemanticColor(change.getSemanticVersionLevel()))
			.toRefImage(references).toString();
	}

	private String renderSerializationChange(JApiClass clazz) {
		final JApiJavaObjectSerializationChangeStatus change = clazz.getJavaObjectSerializationCompatible();
		final String text = md.message.serializationCompatibility.getOrDefault(change, change.getDescription());
		final String color = change.isIncompatible() ? md.message.colorMajorChanges : md.message.colorNoChanges;
		final String msg = change.isIncompatible() ? md.message.statusIncompatible : text;
		return new MarkdownBadge(null, msg, color).toRefImage(references, text).toString();
	}

	private String renderChange(JApiHasChangeStatus hasStatus, String oldValue, String newValue) {
		if (oldValue == null && newValue == null) {
			return null;
		}
		switch (hasStatus.getChangeStatus()) {
			case NEW:
				return newValue == null ? null : format(md.message.added, newValue);
			case REMOVED:
				return oldValue == null ? null : format(md.message.removed, oldValue);
			case UNCHANGED:
				if (newValue == null) {
					return format(md.message.unchanged, oldValue);
				} else if (oldValue == null || newValue.equals(oldValue)) {
					return format(md.message.unchanged, newValue);
				}
				return format(md.message.modified, oldValue, newValue);
			default:
			case MODIFIED:
				if (oldValue == null) {
					return format(md.message.added, newValue);
				} else if (newValue == null) {
					return format(md.message.removed, oldValue);
				} else if (newValue.equals(oldValue)) {
					return format(md.message.unchanged, newValue);
				}
				return format(md.message.modified, oldValue, newValue);
		}
	}

	private String renderChange(JApiHasChangeStatus status, String value) {
		return renderChange(status, value, value);
	}

	private String renderModifiers(JApiHasModifiers clazz) {
		return clazz.getModifiers().stream().map(this::renderModifier)
			.filter(Objects::nonNull).collect(SPACES);
	}

	private String renderModifier(JApiModifier<? extends Enum<? extends Enum<?>>> modifier) {
		final String oldName = getOldModifierName(modifier).or((String) null);
		final String newName = getNewModifierName(modifier).or((String) null);
		return renderChange(modifier, renderCode(oldName), renderCode(newName));
	}

	private String renderTypeWithGenericTypes(String type, List<JApiGenericType> genericTypes) {
		if (type == null) {
			return null;
		}
		final String fullType = formatTypeName(type, genericTypes, false);
		final String simpleType = formatTypeName(type, genericTypes, true);
		return renderCodeWithTooltip(fullType, simpleType);
	}

	private String renderTypeWithGenericTemplates(String type, JApiHasChangeStatus hasChangeStatus, JApiHasGenericTemplates hasGenericTemplates) {
		if (type == null) {
			return null;
		}
		final String fullType = formatTypeName(type, hasChangeStatus, hasGenericTemplates, false);
		final String simpleType = formatTypeName(type, hasChangeStatus, hasGenericTemplates, true);
		return renderCodeWithTooltip(fullType, simpleType);
	}

	private String renderParameterType(JApiBehavior method, JApiParameter parameter, Optional<VarargsModifier> varargs, List<JApiGenericType> genericTypes) {
		if (parameter.getType() == null) {
			return null;
		}
		final String fullType = formatParameterTypeName(method, parameter, varargs, genericTypes, false);
		final String simpleType = formatParameterTypeName(method, parameter, varargs, genericTypes, true);
		return renderCodeWithTooltip(fullType, simpleType);
	}

	private String renderMemberValue(Optional<MemberValue> optionalValue) {
		if (!optionalValue.isPresent()) {
			return EMPTY;
		}
		final String fullValue = formatMemberValue(optionalValue.get(), false);
		final String simpleValue = formatMemberValue(optionalValue.get(), true);
		return renderCodeWithTooltip(fullValue, simpleValue);
	}

	private String renderReturnType(JApiMethod method) {
		final JApiReturnType returnType = method.getReturnType();
		return renderChange(returnType,
			renderTypeWithGenericTypes(returnType.getOldReturnType(), returnType.getOldGenericTypes()),
			renderTypeWithGenericTypes(returnType.getNewReturnType(), returnType.getNewGenericTypes()));
	}

	private String renderFieldType(JApiField field) {
		final JApiType fieldType = field.getType();
		return renderChange(fieldType,
			renderTypeWithGenericTypes(fieldType.getOldValue(), field.getOldGenericTypes()),
			renderTypeWithGenericTypes(fieldType.getNewValue(), field.getNewGenericTypes()));
	}

	private String renderFieldName(JApiField field) {
		return renderCode(field.getName());
	}

	private String renderExceptions(JApiBehavior behavior) {
		return behavior.getExceptions().stream().map(this::renderException).collect(CSV);
	}

	private String renderException(JApiException exception) {
		return renderChange(exception, renderTypeWithGenericTypes(exception.getName(), emptyList()));
	}

	private String renderInlineAnnotations(JApiHasAnnotations hasAnnotations) {
		return hasAnnotations.getAnnotations().stream().map(this::renderAnnotation).collect(BR);
	}

	private String renderAnnotation(JApiAnnotation annotation) {
		final String typeName = renderTypeWithGenericTemplates(annotation.getFullyQualifiedName(), annotation, annotation.getCorrespondingJApiClass().or((JApiClass) null));
		return renderChange(annotation, typeName) +
			(annotation.getElements().isEmpty() ? EMPTY : COLON + SPACE) +
			annotation.getElements().stream().map(this::renderAnnotationElement).collect(CSV);
	}

	private String renderAnnotationElement(JApiAnnotationElement element) {
		return renderChange(element, renderCode(element.getName())) + EQUAL +
			renderChange(element, renderMemberValue(element.getOldValue()), renderMemberValue(element.getNewValue()));
	}

	private String renderCode(String text) {
		return text == null || text.isEmpty() ? text : backticks(text);
	}

	private String renderCodeWithTooltip(String fullType, String simpleType) {
		if (fullType.equals(simpleType)) {
			return renderCode(simpleType);
		}
		return references.link(null, renderCode(simpleType), fullType).toString();
	}

	String renderArchive(JApiCmpArchive archive) {
		final String label = renderSimpleArchiveName(archive);
		final String version = archive.getVersion().getStringVersion();
		final String message = version != null && !version.equals(N_A) ? version : md.message.unknownVersion;
		return new MarkdownBadge(label, message, md.message.colorVersionNumber).toString();
	}

	String renderSimpleArchiveName(JApiCmpArchive archive) {
		final String version = archive.getVersion().getStringVersion();
		String name = archive.getFile().getName();
		// Drop version from archive name
		if (version != null && name.contains(version)) {
			name = name.replace(version, EMPTY);
		}
		// Drop extension from archive name
		final int dot = name.lastIndexOf(DOT);
		if (dot > 0) {
			name = name.substring(0, dot);
		}
		// Drop trailing dashes or underscores
		if (name.endsWith(DASH) || name.endsWith(UNDERSCORE)) {
			name = name.substring(0, name.length() - 1);
		}
		return name;
	}
}
