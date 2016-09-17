package japicmp.config;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import japicmp.cli.JApiCli;
import japicmp.exception.JApiCmpException;
import japicmp.filter.*;
import japicmp.model.AccessModifier;
import japicmp.util.ListJoiner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class Options {
	static final String N_A = "n.a.";
	private List<File> oldArchives = new ArrayList<>();
	private List<File> newArchives = new ArrayList<>();
	private boolean outputOnlyModifications = false;
	private boolean outputOnlyBinaryIncompatibleModifications = false;
	private Optional<String> xmlOutputFile = Optional.absent();
	private Optional<String> htmlOutputFile = Optional.absent();
	private Optional<AccessModifier> accessModifier = Optional.of(AccessModifier.PROTECTED);
	private List<Filter> includes = new ArrayList<>();
	private List<Filter> excludes = new ArrayList<>();
	private boolean includeSynthetic = false;
	private IgnoreMissingClasses ignoreMissingClasses = new IgnoreMissingClasses();
	private Optional<String> htmlStylesheet = Optional.absent();
	private Optional<String> oldClassPath = Optional.absent();
	private Optional<String> newClassPath = Optional.absent();
	private JApiCli.ClassPathMode classPathMode = JApiCli.ClassPathMode.ONE_COMMON_CLASSPATH;
	private boolean noAnnotations = false;
	private ListJoiner<File> joiner = new ListJoiner<File>()
			.on(";")
			.sort(new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					return o1.getName().compareTo(o2.getName());
				}
			})
			.toStringBuilder(FULL_PATH);

	private static final ListJoiner.ListJoinerToString<File> FULL_PATH = new ListJoiner.ListJoinerToString<File>() {
		@Override
		public String toString(File file) {
			return file.getAbsolutePath();
		}
	};

	private static final ListJoiner.ListJoinerToString<File> FILE_NAME = new ListJoiner.ListJoinerToString<File>() {
		@Override
		public String toString(File file) {
			return file.getName();
		}
	};

	Options() {
		// intentionally left empty
	}

	public static Options newDefault() {
		return new Options();
	}

	public void verify() {
		for (File file : getOldArchives()) {
			verifyExistsCanReadAndJar(file);
		}
		for (File file : getNewArchives()) {
			verifyExistsCanReadAndJar(file);
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
				throw JApiCmpException.cliError("Please provide both options: " + JApiCli.OLD_CLASSPATH + " and " + JApiCli.NEW_CLASSPATH);
			} else {
				setClassPathMode(JApiCli.ClassPathMode.ONE_COMMON_CLASSPATH);
			}
		}
	}

	private static void verifyExistsCanReadAndJar(File file) {
		verifyExisting(file);
		verifyCanRead(file);
		verifyJarArchive(file);
	}

	private static void verifyExisting(File newArchive) {
		if (!newArchive.exists()) {
			throw JApiCmpException.cliError("File '%s' does not exist.", newArchive.getAbsolutePath());
		}
	}

	private static void verifyCanRead(File file) {
		if (!file.canRead()) {
			throw JApiCmpException.cliError("Cannot read file '%s'.", file.getAbsolutePath());
		}
	}

	private static void verifyJarArchive(File file) {
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(file);
		} catch (IOException e) {
			throw JApiCmpException.cliError("File '%s' could not be opened as a jar file: %s", file.getAbsolutePath(), e.getMessage());
		} finally {
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	public List<File> getNewArchives() {
		return newArchives;
	}

	public void setNewArchives(List<File> newArchives) {
		this.newArchives = newArchives;
	}

	public List<File> getOldArchives() {
		return oldArchives;
	}

	public void setOldArchives(List<File> oldArchives) {
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

	public void addExcludeFromArgument(Optional<String> packagesExcludeArg) {
		excludes = createFilterList(packagesExcludeArg, excludes, "Wrong syntax for exclude option '%s': %s");
	}

	public void addIncludeFromArgument(Optional<String> packagesIncludeArg) {
		includes = createFilterList(packagesIncludeArg, includes, "Wrong syntax for include option '%s': %s");
	}

	public List<Filter> createFilterList(Optional<String> argumentString, List<Filter> filters, String errorMessage) {
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
					JavadocLikePackageFilter packageFilter = new JavadocLikePackageFilter(filterString);
					filters.add(packageFilter);
				}
			} catch (Exception e) {
				throw new JApiCmpException(JApiCmpException.Reason.CliError, String.format(errorMessage, filterString, e.getMessage()));
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

	public void setReportOnlyFilename(boolean justFile) {
		joiner.toStringBuilder(justFile ?FILE_NAME :FULL_PATH);
	}

	public String getDifferenceDescription() {
		StringBuilder sb = new StringBuilder()
				.append("Comparing ")
				.append(isOutputOnlyBinaryIncompatibleModifications() ?"binary" :"source")
				.append(" compatibility of ");
		joiner.join(sb, newArchives);
		sb.append(" against ");
		joiner.join(sb, oldArchives);
		return sb.toString();
	}

	public String joinOldArchives() {
		String join = joiner.join(oldArchives);
		if (join != null && join.trim().length() == 0) {
			return N_A;
		}
		return join;
	}

	public String joinNewArchives() {
		String join = joiner.join(newArchives);
		if (join != null && join.trim().length() == 0) {
			return N_A;
		}
		return join;
	}}
