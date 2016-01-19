package japicmp.config;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import japicmp.cli.JApiCli;
import japicmp.exception.JApiCmpException;
import japicmp.filter.*;
import japicmp.model.AccessModifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class Options {
	private List<File> oldArchives = new ArrayList<>();
	private List<File> newArchives = new ArrayList<>();
	private boolean outputOnlyModifications = false;
	private boolean outputOnlyBinaryIncompatibleModifications = false;
	private Optional<String> xmlOutputFile = Optional.absent();
	private Optional<String> htmlOutputFile = Optional.absent();
	private Optional<AccessModifier> accessModifier = Optional.of(AccessModifier.PROTECTED);
	private List<Filter> includes = new ArrayList<>();
	private List<Filter> excludes = new ArrayList<>();
	private List<JavaDocLikeClassFilter> classesExclude = new ArrayList<>();
	private boolean includeSynthetic = false;
	private boolean ignoreMissingClasses = false;
	private Optional<String> htmlStylesheet = Optional.absent();
	private Optional<String> oldClassPath = Optional.absent();
	private Optional<String> newClassPath = Optional.absent();
	private JApiCli.ClassPathMode classPathMode = JApiCli.ClassPathMode.ONE_COMMON_CLASSPATH;
	private boolean noAnnotations = false;

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
		if (getHtmlStylesheet().isPresent()) {
			String pathname = getHtmlStylesheet().get();
			File stylesheetFile = new File(pathname);
			if (!stylesheetFile.exists()) {
				throw JApiCmpException.of(JApiCmpException.Reason.CliError, "HTML stylesheet '%s' does not exist.", pathname);
			}
		}
		if (getOldClassPath().isPresent() && getNewClassPath().isPresent()) {
			setClassPathMode(JApiCli.ClassPathMode.TWO_SEPARATE_CLASSPATHS);
		} else {
			if (getOldClassPath().isPresent() || getNewClassPath().isPresent()) {
				throw JApiCmpException.of(JApiCmpException.Reason.CliError, "Please provide both options: " + JApiCli.OLD_CLASSPATH + " and " + JApiCli.NEW_CLASSPATH);
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
			throw JApiCmpException.of(JApiCmpException.Reason.CliError, "File '%s' does not exist.", newArchive.getAbsolutePath());
		}
	}

	private static void verifyCanRead(File file) {
		if (!file.canRead()) {
			throw JApiCmpException.of(JApiCmpException.Reason.CliError, "Cannot read file '%s'.", file.getAbsolutePath());
		}
	}

	private static void verifyJarArchive(File file) {
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(file);
		} catch (IOException e) {
			throw JApiCmpException.of(JApiCmpException.Reason.CliError, "File '%s' could not be opened as a jar file: %s", file.getAbsolutePath(), e.getMessage());
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

	private List<Filter> createFilterList(Optional<String> argumentString, List<Filter> filters, String errorMessage) {
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

	public void addClassesExcludeFromArgument(Optional<String> stringOptional) {
		Iterable<String> classesAsStrings = Splitter.on(",").trimResults().omitEmptyStrings().split(stringOptional.or(""));
		for (String classAsString : classesAsStrings) {
			try {
				JavaDocLikeClassFilter classFilter = new JavaDocLikeClassFilter(classAsString);
				this.classesExclude.add(classFilter);
			} catch (Exception e) {
				throw new JApiCmpException(JApiCmpException.Reason.CliError, "Wrong syntax for class exclude option '" + classAsString + "': " + e.getMessage());
			}
		}
	}

	public List<JavaDocLikeClassFilter> getClassesExclude() {
		return classesExclude;
	}

	public boolean isIgnoreMissingClasses() {
		return ignoreMissingClasses;
	}

	public void setIgnoreMissingClasses(boolean ignoreMissingClasses) {
		this.ignoreMissingClasses = ignoreMissingClasses;
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
}
