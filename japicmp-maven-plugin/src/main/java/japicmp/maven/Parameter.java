package japicmp.maven;

import java.util.List;

import japicmp.model.JApiCompatibilityChange;

public class Parameter {
    private String accessModifier;
    private List<String> includes;
    private List<String> excludes;
    private String onlyBinaryIncompatible;
    private String onlyModified;
    private String breakBuildOnModifications;
    private String breakBuildOnBinaryIncompatibleModifications;
    private String breakBuildOnSourceIncompatibleModifications;
    private String breakBuildBasedOnSemanticVersioning;
    private String includeSynthetic;
    private String ignoreMissingClasses;
    @org.apache.maven.plugins.annotations.Parameter(required = false)
    private List<String> ignoreMissingClassesByRegularExpressions;
    @org.apache.maven.plugins.annotations.Parameter(required = false)
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
    @org.apache.maven.plugins.annotations.Parameter(required = false)
    private String postAnalysisScript;
    @org.apache.maven.plugins.annotations.Parameter(required = false)
    private String skipHtmlReport;
    @org.apache.maven.plugins.annotations.Parameter(required = false)
    private String skipXmlReport;
    @org.apache.maven.plugins.annotations.Parameter(required = false)
    private boolean skipDiffReport;
    @org.apache.maven.plugins.annotations.Parameter(required = false)
    private String ignoreMissingOldVersion;
    @org.apache.maven.plugins.annotations.Parameter(required = false)
    private String ignoreMissingNewVersion;
    @org.apache.maven.plugins.annotations.Parameter(required = false)
    private String oldVersionPattern;
    @org.apache.maven.plugins.annotations.Parameter(required = false)
    private boolean breakBuildIfCausedByExclusion = true;
    @org.apache.maven.plugins.annotations.Parameter(required = false)
    private boolean reportOnlyFilename;
    @org.apache.maven.plugins.annotations.Parameter(required = false)
    private List<String> includeModules;
    @org.apache.maven.plugins.annotations.Parameter(required = false)
    private List<String> excludeModules;
    @org.apache.maven.plugins.annotations.Parameter(required = false, defaultValue = "false")
    private boolean breakBuildBasedOnSemanticVersioningForMajorVersionZero;
    @org.apache.maven.plugins.annotations.Parameter(required = false, defaultValue = "false")
    private boolean includeExclusively;
    @org.apache.maven.plugins.annotations.Parameter(required = false, defaultValue = "false")
    private boolean excludeExclusively;
    @org.apache.maven.plugins.annotations.Parameter(required = false)
    private List<OverrideCompatibilityChangeParameter> overrideCompatibilityChangeParameters;

    @org.apache.maven.plugins.annotations.Parameter(required = false)
    private SemanticVersioning semanticVersioning;

    public static class SemanticVersioning {

	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private boolean allowBinaryCompatibleElementsInPatch;

	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private boolean allowNewAbstractElementsInMinor;

	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private JApiCompatibilityChange[] minorVersion;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private JApiCompatibilityChange[] microVersion;

	public boolean isAllowNewAbstractElementsInMinor() {
	    return allowNewAbstractElementsInMinor;
	}

	public void setAllowNewAbstractElementsInMinor(boolean allowNewAbstractElementsInMinor) {
	    this.allowNewAbstractElementsInMinor = allowNewAbstractElementsInMinor;
	}

	public boolean isAllowBinaryCompatibleElementsInPatch() {
	    return allowBinaryCompatibleElementsInPatch;
	}

	public void setAllowBinaryCompatibleElementsInPatch(boolean allowBinaryCompatibleElementsInPatch) {
	    this.allowBinaryCompatibleElementsInPatch = allowBinaryCompatibleElementsInPatch;
	}
    }

    public static class OverrideCompatibilityChangeParameter {
	@org.apache.maven.plugins.annotations.Parameter(required = true)
	private String compatibilityChange;
	@org.apache.maven.plugins.annotations.Parameter(required = true)
	private boolean binaryCompatible;
	@org.apache.maven.plugins.annotations.Parameter(required = true)
	private boolean sourceCompatible;

	public String getCompatibilityChange() {
	    return compatibilityChange;
	}

	public void setCompatibilityChange(final String compatibilityChange) {
	    this.compatibilityChange = compatibilityChange;
	}

	public boolean isBinaryCompatible() {
	    return binaryCompatible;
	}

	public void setBinaryCompatible(final boolean binaryCompatible) {
	    this.binaryCompatible = binaryCompatible;
	}

	public boolean isSourceCompatible() {
	    return sourceCompatible;
	}

	public void setSourceCompatible(final boolean sourceCompatible) {
	    this.sourceCompatible = sourceCompatible;
	}
    }

    public String getNoAnnotations() {
	return noAnnotations;
    }

    public void setNoAnnotations(final String noAnnotations) {
	this.noAnnotations = noAnnotations;
    }

    public String getAccessModifier() {
	return accessModifier;
    }

    public void setAccessModifier(final String accessModifier) {
	this.accessModifier = accessModifier;
    }

    public String getOnlyModified() {
	return onlyModified;
    }

    public void setOnlyModified(final String onlyModified) {
	this.onlyModified = onlyModified;
    }

    public String getOnlyBinaryIncompatible() {
	return onlyBinaryIncompatible;
    }

    public void setOnlyBinaryIncompatible(final String onlyBinaryIncompatible) {
	this.onlyBinaryIncompatible = onlyBinaryIncompatible;
    }

    public String getBreakBuildOnModifications() {
	return breakBuildOnModifications;
    }

    public void setBreakBuildOnModifications(final String breakBuildOnModifications) {
	this.breakBuildOnModifications = breakBuildOnModifications;
    }

    public String getBreakBuildOnBinaryIncompatibleModifications() {
	return breakBuildOnBinaryIncompatibleModifications;
    }

    public void setBreakBuildOnBinaryIncompatibleModifications(
	    final String breakBuildOnBinaryIncompatibleModifications) {
	this.breakBuildOnBinaryIncompatibleModifications = breakBuildOnBinaryIncompatibleModifications;
    }

    public String getIncludeSynthetic() {
	return includeSynthetic;
    }

    public void setIncludeSynthetic(final String includeSynthetic) {
	this.includeSynthetic = includeSynthetic;
    }

    public List<String> getIncludes() {
	return includes;
    }

    public void setIncludes(final List<String> includes) {
	this.includes = includes;
    }

    public List<String> getExcludes() {
	return excludes;
    }

    public void setExcludes(final List<String> excludes) {
	this.excludes = excludes;
    }

    public String getIgnoreMissingClasses() {
	return ignoreMissingClasses;
    }

    public void setIgnoreMissingClasses(final String ignoreMissingClasses) {
	this.ignoreMissingClasses = ignoreMissingClasses;
    }

    public String getSkipPomModules() {
	return skipPomModules;
    }

    public void setSkipPomModules(final String skipPomModules) {
	this.skipPomModules = skipPomModules;
    }

    public String getHtmlStylesheet() {
	return htmlStylesheet;
    }

    public void setHtmlStylesheet(final String htmlStylesheet) {
	this.htmlStylesheet = htmlStylesheet;
    }

    public String getHtmlTitle() {
	return htmlTitle;
    }

    public void setHtmlTitle(final String htmlTitle) {
	this.htmlTitle = htmlTitle;
    }

    public String getIgnoreNonResolvableArtifacts() {
	return ignoreNonResolvableArtifacts;
    }

    public void setIgnoreNonResolvableArtifacts(final String ignoreNonResolvableArtifacts) {
	this.ignoreNonResolvableArtifacts = ignoreNonResolvableArtifacts;
    }

    public List<String> getPackagingSupporteds() {
	return packagingSupporteds;
    }

    public void setPackagingSupporteds(final List<String> packagingSupporteds) {
	this.packagingSupporteds = packagingSupporteds;
    }

    public String getBreakBuildOnSourceIncompatibleModifications() {
	return breakBuildOnSourceIncompatibleModifications;
    }

    public void setBreakBuildOnSourceIncompatibleModifications(
	    final String breakBuildOnSourceIncompatibleModifications) {
	this.breakBuildOnSourceIncompatibleModifications = breakBuildOnSourceIncompatibleModifications;
    }

    public String getBreakBuildBasedOnSemanticVersioning() {
	return breakBuildBasedOnSemanticVersioning;
    }

    public void setBreakBuildBasedOnSemanticVersioning(final String breakBuildBasedOnSemanticVersioning) {
	this.breakBuildBasedOnSemanticVersioning = breakBuildBasedOnSemanticVersioning;
    }

    public String getPostAnalysisScript() {
	return postAnalysisScript;
    }

    public void setPostAnalysisScript(final String postAnalysisScript) {
	this.postAnalysisScript = postAnalysisScript;
    }

    public String getSkipHtmlReport() {
	return skipHtmlReport;
    }

    public void setSkipHtmlReport(final String skipHtmlReport) {
	this.skipHtmlReport = skipHtmlReport;
    }

    public String getSkipXmlReport() {
	return skipXmlReport;
    }

    public void setSkipXmlReport(final String skipXmlReport) {
	this.skipXmlReport = skipXmlReport;
    }

    public boolean isSkipDiffReport() {
	return skipDiffReport;
    }

    public void setSkipDiffReport(final boolean skipDiffReport) {
	this.skipDiffReport = skipDiffReport;
    }

    public String getIgnoreMissingOldVersion() {
	return ignoreMissingOldVersion;
    }

    public void setIgnoreMissingOldVersion(final String ignoreMissingOldVersion) {
	this.ignoreMissingOldVersion = ignoreMissingOldVersion;
    }

    public String getOldVersionPattern() {
	return oldVersionPattern;
    }

    public void setOldVersionPattern(final String oldVersionPattern) {
	this.oldVersionPattern = oldVersionPattern;
    }

    public boolean isBreakBuildIfCausedByExclusion() {
	return breakBuildIfCausedByExclusion;
    }

    public void setBreakBuildIfCausedByExclusion(final boolean breakBuildIfCausedByExclusion) {
	this.breakBuildIfCausedByExclusion = breakBuildIfCausedByExclusion;
    }

    public List<String> getIgnoreMissingClassesByRegularExpressions() {
	return ignoreMissingClassesByRegularExpressions;
    }

    public void setIgnoreMissingClassesByRegularExpressions(
	    final List<String> ignoreMissingClassesByRegularExpressions) {
	this.ignoreMissingClassesByRegularExpressions = ignoreMissingClassesByRegularExpressions;
    }

    public boolean isReportOnlyFilename() {
	return reportOnlyFilename;
    }

    public void setReportOnlyFilename(final boolean reportOnlyFileName) {
	this.reportOnlyFilename = reportOnlyFileName;
    }

    public String getIgnoreMissingNewVersion() {
	return ignoreMissingNewVersion;
    }

    public void setIgnoreMissingNewVersion(final String ignoreMissingNewVersion) {
	this.ignoreMissingNewVersion = ignoreMissingNewVersion;
    }

    public List<String> getIncludeModules() {
	return includeModules;
    }

    public void setIncludeModules(final List<String> includeModules) {
	this.includeModules = includeModules;
    }

    public List<String> getExcludeModules() {
	return excludeModules;
    }

    public void setExcludeModules(final List<String> excludeModules) {
	this.excludeModules = excludeModules;
    }

    public boolean isBreakBuildBasedOnSemanticVersioningForMajorVersionZero() {
	return breakBuildBasedOnSemanticVersioningForMajorVersionZero;
    }

    public void setBreakBuildBasedOnSemanticVersioningForMajorVersionZero(
	    final boolean breakBuildBasedOnSemanticVersioningForMajorVersionZero) {
	this.breakBuildBasedOnSemanticVersioningForMajorVersionZero = breakBuildBasedOnSemanticVersioningForMajorVersionZero;
    }

    public boolean isIncludeExlusively() {
	return includeExclusively;
    }

    public void setIncludeExclusively(final boolean includeExclusively) {
	this.includeExclusively = includeExclusively;
    }

    public boolean isExcludeExclusively() {
	return excludeExclusively;
    }

    public void setExcludeExclusively(final boolean excludeExclusively) {
	this.excludeExclusively = excludeExclusively;
    }

    public boolean isIncludeExclusively() {
	return includeExclusively;
    }

    public List<OverrideCompatibilityChangeParameter> getOverrideCompatibilityChangeParameters() {
	return overrideCompatibilityChangeParameters;
    }

    public void setOverrideCompatibilityChangeParameters(
	    final List<OverrideCompatibilityChangeParameter> overrideCompatibilityChangeParameters) {
	this.overrideCompatibilityChangeParameters = overrideCompatibilityChangeParameters;
    }

    public SemanticVersioning getSemanticVersioning() {
	return semanticVersioning;
    }

    public void setSemanticVersioning(final SemanticVersioning semanticVersioning) {
	this.semanticVersioning = semanticVersioning;
    }
}
