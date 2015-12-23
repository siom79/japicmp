package japicmp.config;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import japicmp.cli.JApiCli;
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
import java.util.ArrayList;
import java.util.List;

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

	public void setAccessModifier(AccessModifier accessModifier) {
		this.accessModifier = Optional.of(accessModifier);
	}

	public AccessModifier getAccessModifier() {
		return accessModifier.get();
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

	public void setOutputOnlyBinaryIncompatibleModifications(boolean outputOnlyBinaryIncompatibleModifications) {
		this.outputOnlyBinaryIncompatibleModifications = outputOnlyBinaryIncompatibleModifications;
	}

	public boolean isOutputOnlyBinaryIncompatibleModifications() {
		return outputOnlyBinaryIncompatibleModifications;
	}

	public Optional<String> getHtmlOutputFile() {
		return htmlOutputFile;
	}

	public void setHtmlOutputFile(Optional<String> htmlOutputFile) {
		this.htmlOutputFile = htmlOutputFile;
	}

	public void setIncludeSynthetic(boolean showSynthetic) {
		this.includeSynthetic = showSynthetic;
	}

	public boolean isIncludeSynthetic() {
		return includeSynthetic;
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

	public void setIgnoreMissingClasses(boolean ignoreMissingClasses) {
		this.ignoreMissingClasses = ignoreMissingClasses;
	}

	public boolean isIgnoreMissingClasses() {
		return ignoreMissingClasses;
	}

	public void setHtmlStylesheet(Optional<String> htmlStylesheet) {
		this.htmlStylesheet = htmlStylesheet;
	}

	public Optional<String> getHtmlStylesheet() {
		return htmlStylesheet;
	}

	public void setOldClassPath(Optional<String> oldClassPath) {
		this.oldClassPath = oldClassPath;
	}

	public Optional<String> getOldClassPath() {
		return oldClassPath;
	}

	public void setNewClassPath(Optional<String> newClassPath) {
		this.newClassPath = newClassPath;
	}

	public Optional<String> getNewClassPath() {
		return newClassPath;
	}

	public void setClassPathMode(JApiCli.ClassPathMode classPathMode) {
		this.classPathMode = classPathMode;
	}

	public JApiCli.ClassPathMode getClassPathMode() {
		return classPathMode;
	}

	public void setNoAnnotations(boolean noAnnotations) {
		this.noAnnotations = noAnnotations;
	}

	public boolean isNoAnnotations() {
		return noAnnotations;
	}
}
