package japicmp.maven;

import java.util.List;

public class Parameter {
	private String accessModifier;
	private List<String> includes;
	private List<String> excludes;
	private String onlyBinaryIncompatible;
	private String onlyModified;
	private String breakBuildOnModifications;
	private String breakBuildOnBinaryIncompatibleModifications;
	private String includeSynthetic;
	private String ignoreMissingClasses;
	private String skipPomModules;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private String htmlStylesheet;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private String htmlTitle;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private String noAnnotations;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private String ignoreNonResolvableArtifacts;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private List<String> packagingSupporteds;

	public String getNoAnnotations() {
		return noAnnotations;
	}

	public void setNoAnnotations(String noAnnotations) {
		this.noAnnotations = noAnnotations;
	}

	public String getAccessModifier() {
		return accessModifier;
	}

	public void setAccessModifier(String accessModifier) {
		this.accessModifier = accessModifier;
	}

	public String getOnlyModified() {
		return onlyModified;
	}

	public void setOnlyModified(String onlyModified) {
		this.onlyModified = onlyModified;
	}

	public String getOnlyBinaryIncompatible() {
		return onlyBinaryIncompatible;
	}

	public void setOnlyBinaryIncompatible(String onlyBinaryIncompatible) {
		this.onlyBinaryIncompatible = onlyBinaryIncompatible;
	}

	public String getBreakBuildOnModifications() {
		return breakBuildOnModifications;
	}

	public void setBreakBuildOnModifications(String breakBuildOnModifications) {
		this.breakBuildOnModifications = breakBuildOnModifications;
	}

	public String getBreakBuildOnBinaryIncompatibleModifications() {
		return breakBuildOnBinaryIncompatibleModifications;
	}

	public void setBreakBuildOnBinaryIncompatibleModifications(String breakBuildOnBinaryIncompatibleModifications) {
		this.breakBuildOnBinaryIncompatibleModifications = breakBuildOnBinaryIncompatibleModifications;
	}

	public String getIncludeSynthetic() {
		return includeSynthetic;
	}

	public void setIncludeSynthetic(String includeSynthetic) {
		this.includeSynthetic = includeSynthetic;
	}

	public List<String> getIncludes() {
		return includes;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public List<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public String getIgnoreMissingClasses() {
		return ignoreMissingClasses;
	}

	public void setIgnoreMissingClasses(String ignoreMissingClasses) {
		this.ignoreMissingClasses = ignoreMissingClasses;
	}

	public String getSkipPomModules() {
		return skipPomModules;
	}

	public void setSkipPomModules(String skipPomModules) {
		this.skipPomModules = skipPomModules;
	}

	public String getHtmlStylesheet() {
		return htmlStylesheet;
	}

	public void setHtmlStylesheet(String htmlStylesheet) {
		this.htmlStylesheet = htmlStylesheet;
	}

	public String getHtmlTitle() {
		return htmlTitle;
	}

	public void setHtmlTitle(String htmlTitle) {
		this.htmlTitle = htmlTitle;
	}

	public String getIgnoreNonResolvableArtifacts() {
		return ignoreNonResolvableArtifacts;
	}

	public void setIgnoreNonResolvableArtifacts(String ignoreNonResolvableArtifacts) {
		this.ignoreNonResolvableArtifacts = ignoreNonResolvableArtifacts;
	}

	public List<String> getPackagingSupporteds() {
		return packagingSupporteds;
	}

	public void setPackagingSupporteds(List<String> packagingSupporteds) {
		this.packagingSupporteds = packagingSupporteds;
	}
}
