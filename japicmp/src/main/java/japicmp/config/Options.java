package japicmp.config;

import com.google.common.base.Joiner;
import japicmp.util.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import japicmp.cli.CliParser;
import japicmp.cli.JApiCli;
import japicmp.cmp.JApiCmpArchive;
import japicmp.exception.JApiCmpException;
import japicmp.filter.AnnotationBehaviorFilter;
import japicmp.filter.AnnotationClassFilter;
import japicmp.filter.AnnotationFieldFilter;
import japicmp.filter.Filter;
import japicmp.filter.JavaDocLikeClassFilter;
import japicmp.filter.JavadocLikeBehaviorFilter;
import japicmp.filter.JavadocLikeFieldFilter;
import japicmp.filter.JavadocLikePackageFilter;
import japicmp.model.AccessModifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Options {
	private static final Logger LOGGER = Logger.getLogger(Options.class.getName());
	static final String N_A = "n.a.";
	private List<JApiCmpArchive> oldArchives = new ArrayList<>();
	private List<JApiCmpArchive> newArchives = new ArrayList<>();
	private boolean outputOnlyModifications = false;
	private boolean outputOnlyBinaryIncompatibleModifications = false;
	private Optional<String> xmlOutputFile = Optional.absent();
	private Optional<String> htmlOutputFile = Optional.absent();
	private Optional<AccessModifier> accessModifier = Optional.of(AccessModifier.PROTECTED);
	private List<Filter> includes = new ArrayList<>();
	private List<Filter> excludes = new ArrayList<>();
	private boolean includeSynthetic = false;
	private final IgnoreMissingClasses ignoreMissingClasses = new IgnoreMissingClasses();
	private Optional<String> htmlStylesheet = Optional.absent();
	private Optional<String> oldClassPath = Optional.absent();
	private Optional<String> newClassPath = Optional.absent();
	private JApiCli.ClassPathMode classPathMode = JApiCli.ClassPathMode.ONE_COMMON_CLASSPATH;
	private boolean noAnnotations = false;
	private boolean reportOnlyFilename;
	private boolean semanticVersioning;
	private boolean errorOnBinaryIncompatibility;
	private boolean errorOnSourceIncompatibility;
	private boolean errorOnExclusionIncompatibility = true;
	private boolean errorOnModifications;
	private boolean errorOnSemanticIncompatibility;
	private boolean ignoreMissingOldVersion;
	private boolean ignoreMissingNewVersion;
	private boolean helpRequested;

	Options() {
		// intentionally left empty
	}

	public static Options newDefault() {
		return new Options();
	}

	public void verify() {
		if (oldArchives.isEmpty()) {
			throw new JApiCmpException(JApiCmpException.Reason.CliError, "Required option -o, --old is missing.");
		}
		if (newArchives.isEmpty()) {
			throw new JApiCmpException(JApiCmpException.Reason.CliError, "Required option -n, --new is missing.");
		}
		for (JApiCmpArchive archive : getOldArchives()) {
			verifyExistsCanReadAndJar(archive);
		}
		for (JApiCmpArchive archive : getNewArchives()) {
			verifyExistsCanReadAndJar(archive);
		}
		if (getHtmlOutputFile().isPresent()) {
			if (getHtmlStylesheet().isPresent()) {
				String pathname = getHtmlStylesheet().get();
				File stylesheetFile = new File(pathname);
				if (!stylesheetFile.exists()) {
					throw JApiCmpException.cliError("HTML stylesheet '%s' does not exist.", pathname);
				}
			}
		} else {
			if (getHtmlStylesheet().isPresent()) {
				throw JApiCmpException.cliError("Define a HTML output file, if you want to apply a stylesheet.");
			}
		}
		if (getOldClassPath().isPresent() && getNewClassPath().isPresent()) {
			setClassPathMode(JApiCli.ClassPathMode.TWO_SEPARATE_CLASSPATHS);
		} else {
			if (getOldClassPath().isPresent() || getNewClassPath().isPresent()) {
				throw JApiCmpException.cliError("Please provide both options: " + CliParser.OLD_CLASSPATH + " and " + CliParser.NEW_CLASSPATH);
			} else {
				setClassPathMode(JApiCli.ClassPathMode.ONE_COMMON_CLASSPATH);
			}
		}
	}

	private static void verifyExistsCanReadAndJar(JApiCmpArchive jApiCmpArchive) {
		verifyExisting(jApiCmpArchive);
		verifyCanRead(jApiCmpArchive);
		verifyJarArchive(jApiCmpArchive);
	}

	private static void verifyExisting(JApiCmpArchive jApiCmpArchive) {
		if (!jApiCmpArchive.getFile().exists()) {
			throw JApiCmpException.cliError("File '%s' does not exist.", jApiCmpArchive.getFile().getAbsolutePath());
		}
	}

	private static void verifyCanRead(JApiCmpArchive jApiCmpArchive) {
		if (!jApiCmpArchive.getFile().canRead()) {
			throw JApiCmpException.cliError("Cannot read file '%s'.", jApiCmpArchive.getFile().getAbsolutePath());
		}
	}

	private static void verifyJarArchive(JApiCmpArchive jApiCmpArchive) {
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(jApiCmpArchive.getFile());
		} catch (IOException e) {
			throw JApiCmpException.cliError("File '%s' could not be opened as a jar file: %s", jApiCmpArchive.getFile().getAbsolutePath(), e.getMessage(), e);
		} finally {
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException e) {
					LOGGER.log(Level.FINE, "Failed to close file: " + e.getLocalizedMessage(), e);
				}
			}
		}
	}

	public List<JApiCmpArchive> getNewArchives() {
		return newArchives;
	}

	public void setNewArchives(List<JApiCmpArchive> newArchives) {
		this.newArchives = newArchives;
	}

	public List<JApiCmpArchive> getOldArchives() {
		return oldArchives;
	}

	public void setOldArchives(List<JApiCmpArchive> oldArchives) {
		this.oldArchives = oldArchives;
	}

	public boolean isOutputOnlyModifications() {
		return outputOnlyModifications;
	}

	public void setOutputOnlyModifications(boolean outputOnlyModifications) {
		this.outputOnlyModifications = outputOnlyModifications;
	}

	public Optional<String> getXmlOutputFile() {
		return xmlOutputFile;
	}

	public void setXmlOutputFile(Optional<String> xmlOutputFile) {
		this.xmlOutputFile = xmlOutputFile;
	}

	public void setAccessModifier(Optional<AccessModifier> accessModifier) {
		this.accessModifier = accessModifier;
	}

	public AccessModifier getAccessModifier() {
		return accessModifier.get();
	}

	public void setAccessModifier(AccessModifier accessModifier) {
		this.accessModifier = Optional.of(accessModifier);
	}

	public List<Filter> getIncludes() {
		return ImmutableList.copyOf(includes);
	}

	public List<Filter> getExcludes() {
		return ImmutableList.copyOf(excludes);
	}

	public void addExcludeFromArgument(Optional<String> packagesExcludeArg, boolean excludeExclusively) {
		excludes = createFilterList(packagesExcludeArg, excludes, "Wrong syntax for exclude option '%s': %s", excludeExclusively);
	}

	public void addIncludeFromArgument(Optional<String> packagesIncludeArg, boolean includeExclusively) {
		includes = createFilterList(packagesIncludeArg, includes, "Wrong syntax for include option '%s': %s", includeExclusively);
	}

	public List<Filter> createFilterList(Optional<String> argumentString, List<Filter> filters, String errorMessage, boolean exclusive) {
		for (String filterString : Splitter.on(";").trimResults().omitEmptyStrings().split(argumentString.or(""))) {
			try {
				// filter based on annotations
				if (filterString.startsWith("@")) {
					filters.add(new AnnotationClassFilter(filterString));
					filters.add(new AnnotationFieldFilter(filterString));
					filters.add(new AnnotationBehaviorFilter(filterString));
				}
				if (filterString.contains("#")) {
					if (filterString.contains("(")) {
						JavadocLikeBehaviorFilter behaviorFilter = new JavadocLikeBehaviorFilter(filterString);
						filters.add(behaviorFilter);
					} else {
						JavadocLikeFieldFilter fieldFilter = new JavadocLikeFieldFilter(filterString);
						filters.add(fieldFilter);
					}
				} else {
					JavaDocLikeClassFilter classFilter = new JavaDocLikeClassFilter(filterString);
					filters.add(classFilter);
					JavadocLikePackageFilter packageFilter = new JavadocLikePackageFilter(filterString, exclusive);
					filters.add(packageFilter);
				}
			} catch (Exception e) {
				throw new JApiCmpException(JApiCmpException.Reason.CliError, String.format(errorMessage, filterString, e.getMessage()), e);
			}
		}
		return filters;
	}

	public boolean isOutputOnlyBinaryIncompatibleModifications() {
		return outputOnlyBinaryIncompatibleModifications;
	}

	public void setOutputOnlyBinaryIncompatibleModifications(boolean outputOnlyBinaryIncompatibleModifications) {
		this.outputOnlyBinaryIncompatibleModifications = outputOnlyBinaryIncompatibleModifications;
	}

	public Optional<String> getHtmlOutputFile() {
		return htmlOutputFile;
	}

	public void setHtmlOutputFile(Optional<String> htmlOutputFile) {
		this.htmlOutputFile = htmlOutputFile;
	}

	public boolean isIncludeSynthetic() {
		return includeSynthetic;
	}

	public void setIncludeSynthetic(boolean showSynthetic) {
		this.includeSynthetic = showSynthetic;
	}

	public void setIgnoreMissingClasses(boolean ignoreMissingClasses) {
		this.ignoreMissingClasses.setIgnoreAllMissingClasses(ignoreMissingClasses);
	}

	public Optional<String> getHtmlStylesheet() {
		return htmlStylesheet;
	}

	public void setHtmlStylesheet(Optional<String> htmlStylesheet) {
		this.htmlStylesheet = htmlStylesheet;
	}

	public Optional<String> getOldClassPath() {
		return oldClassPath;
	}

	public void setOldClassPath(Optional<String> oldClassPath) {
		this.oldClassPath = oldClassPath;
	}

	public Optional<String> getNewClassPath() {
		return newClassPath;
	}

	public void setNewClassPath(Optional<String> newClassPath) {
		this.newClassPath = newClassPath;
	}

	public JApiCli.ClassPathMode getClassPathMode() {
		return classPathMode;
	}

	public void setClassPathMode(JApiCli.ClassPathMode classPathMode) {
		this.classPathMode = classPathMode;
	}

	public boolean isNoAnnotations() {
		return noAnnotations;
	}

	public void setNoAnnotations(boolean noAnnotations) {
		this.noAnnotations = noAnnotations;
	}

	public void addIgnoreMissingClassRegularExpression(String missingClassRegEx) {
		try {
			Pattern pattern = Pattern.compile(missingClassRegEx);
			this.ignoreMissingClasses.getIgnoreMissingClassRegularExpression().add(pattern);
		} catch (Exception e) {
			throw new JApiCmpException(JApiCmpException.Reason.IllegalArgument, "Could not compile provided regular expression: " + e.getMessage(), e);
		}
	}

	public IgnoreMissingClasses getIgnoreMissingClasses() {
		return ignoreMissingClasses;
	}

	public void setReportOnlyFilename(boolean reportOnlyFilename) {
		this.reportOnlyFilename = reportOnlyFilename;
	}

	public String getDifferenceDescription() {
		Joiner joiner = Joiner.on(";");
		StringBuilder sb = new StringBuilder()
				.append("Comparing ")
				.append(isOutputOnlyBinaryIncompatibleModifications() ? "binary" :"source")
				.append(" compatibility of ");
		sb.append(joiner.join(toPathList(newArchives)));
		sb.append(" against ");
		sb.append(joiner.join(toPathList(oldArchives)));
		return sb.toString();
	}

	private List<String> toPathList(List<JApiCmpArchive> archives) {
		List<String> paths = new ArrayList<>(archives.size());
		for (JApiCmpArchive archive : archives) {
			if (this.reportOnlyFilename) {
				paths.add(archive.getFile().getName());
			} else {
				paths.add(archive.getFile().getAbsolutePath());
			}
		}
		return paths;
	}

	private List<String> toVersionList(List<JApiCmpArchive> archives) {
		List<String> versions = new ArrayList<>(archives.size());
		for (JApiCmpArchive archive : archives) {
			String stringVersion = archive.getVersion().getStringVersion();
			if (stringVersion != null) {
				versions.add(stringVersion);
			}
		}
		return versions;
	}

	public String joinOldArchives() {
		Joiner joiner = Joiner.on(";");
		String join = joiner.join(toPathList(oldArchives));
		if (join.trim().length() == 0) {
			return N_A;
		}
		return join;
	}

	public String joinNewArchives() {
		Joiner joiner = Joiner.on(";");
		String join = joiner.join(toPathList(newArchives));
		if (join.trim().length() == 0) {
			return N_A;
		}
		return join;
	}

	public String joinOldVersions() {
		Joiner joiner = Joiner.on(";");
		String join = joiner.join(toVersionList(oldArchives));
		if (join.trim().length() == 0) {
			return N_A;
		}
		return join;
	}

	public String joinNewVersions() {
		Joiner joiner = Joiner.on(";");
		String join = joiner.join(toVersionList(newArchives));
		if (join.trim().length() == 0) {
			return N_A;
		}
		return join;
	}

	public void setSemanticVersioning(boolean semanticVersioning) {
		this.semanticVersioning = semanticVersioning;
	}

	public boolean isSemanticVersioning() {
		return semanticVersioning;
	}

	public boolean isErrorOnBinaryIncompatibility() {
		return errorOnBinaryIncompatibility;
	}

	public void setErrorOnBinaryIncompatibility(boolean errorOnBinaryIncompatibility) {
		this.errorOnBinaryIncompatibility = errorOnBinaryIncompatibility;
	}

	public boolean isErrorOnSourceIncompatibility() {
		return errorOnSourceIncompatibility;
	}

	public void setErrorOnSourceIncompatibility(boolean errorOnSourceIncompatibility) {
		this.errorOnSourceIncompatibility = errorOnSourceIncompatibility;
	}

	public boolean isErrorOnExclusionIncompatibility() {
		return errorOnExclusionIncompatibility;
	}

	public void setErrorOnExclusionIncompatibility(boolean errorOnExclusionIncompatibility) {
		this.errorOnExclusionIncompatibility = errorOnExclusionIncompatibility;
	}

	public boolean isErrorOnModifications() {
		return errorOnModifications;
	}

	public void setErrorOnModifications(boolean errorOnModifications) {
		this.errorOnModifications = errorOnModifications;
	}

	public boolean isErrorOnSemanticIncompatibility() {
		return errorOnSemanticIncompatibility;
	}

	public void setErrorOnSemanticIncompatibility(boolean errorOnSemanticIncompatibility) {
		this.errorOnSemanticIncompatibility = errorOnSemanticIncompatibility;
	}

	public boolean isIgnoreMissingOldVersion() {
		return ignoreMissingOldVersion;
	}

	public void setIgnoreMissingOldVersion(boolean ignoreMissingOldVersion) {
		this.ignoreMissingOldVersion = ignoreMissingOldVersion;
	}

	public boolean isIgnoreMissingNewVersion() {
		return ignoreMissingNewVersion;
	}

	public void setIgnoreMissingNewVersion(boolean ignoreMissingNewVersion) {
		this.ignoreMissingNewVersion = ignoreMissingNewVersion;
	}

	public boolean isHelpRequested() {
		return helpRequested;
	}

	public void setHelpRequested(boolean helpRequested) {
		this.helpRequested = helpRequested;
	}
}
