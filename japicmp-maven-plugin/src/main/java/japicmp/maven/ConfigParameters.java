package japicmp.maven;

import java.util.List;

/** Class for storing the japicmp configuration parameters. */
public class ConfigParameters {
	// Important: All fields in this class should be considered public user-facing parameters;
	// changing their names or default values can be a breaking change.
	// Note that using Maven's `@Parameter` here is not possible, see https://github.com/apache/maven-plugin-tools/issues/631

	/** Process only modified classes. */
	private boolean onlyModified;

	/** Access modifier. */
	private String accessModifier;

	private List<String> includes;

	private List<String> excludes;

	private boolean onlyBinaryIncompatible;

	private boolean breakBuildBasedOnSemanticVersioning;

	private boolean breakBuildBasedOnSemanticVersioningForMajorVersionZero;

	private boolean breakBuildOnModifications;

	private boolean breakBuildOnBinaryIncompatibleModifications;

	private boolean breakBuildOnSourceIncompatibleModifications;

	private boolean includeSynthetic;

	private boolean ignoreMissingClasses;

	private List<String> ignoreMissingClassesByRegularExpressions;

	private boolean skipPomModules = true;

	private String htmlStylesheet;

	private String htmlTitle;

	private String markdownTitle;

	private boolean noAnnotations;

	private String ignoreNonResolvableArtifacts;

	private List<String> packagingSupporteds;

	private String postAnalysisScript;

	private boolean skipDiffReport;

	private boolean skipHtmlReport;

	private boolean skipMarkdownReport;

	private boolean skipXmlReport;

	private boolean ignoreMissingOldVersion = true;

	private boolean ignoreMissingNewVersion;

	private String oldVersionPattern;

	private boolean includeSnapshots = false;

	private boolean breakBuildIfCausedByExclusion = true;

	private boolean reportOnlyFilename;

	private boolean reportOnlySummary;

	private List<String> includeModules;

	private List<String> excludeModules;


	private boolean includeExclusively = false;

	private boolean excludeExclusively = false;

	private List<OverrideCompatibilityChangeParameter> overrideCompatibilityChangeParameters;

	private boolean ignoreMissingOptionalDependency;

	private String reportLinkName;

	public ConfigParameters() {
		// Intentionally left blank.
	}

	public boolean getNoAnnotations() {
		return noAnnotations;
	}

	void setNoAnnotations(boolean noAnnotations) {
		this.noAnnotations = noAnnotations;
	}

	public String getAccessModifier() {
		return accessModifier;
	}

	void setAccessModifier(String accessModifier) {
		this.accessModifier = accessModifier;
	}

	public boolean getOnlyModified() {
		return onlyModified;
	}

	void setOnlyModified(boolean onlyModified) {
		this.onlyModified = onlyModified;
	}

	public boolean getOnlyBinaryIncompatible() {
		return onlyBinaryIncompatible;
	}

	void setOnlyBinaryIncompatible(boolean onlyBinaryIncompatible) {
		this.onlyBinaryIncompatible = onlyBinaryIncompatible;
	}

	public boolean getBreakBuildOnModifications() {
		return breakBuildOnModifications;
	}

	void setBreakBuildOnModifications(boolean breakBuildOnModifications) {
		this.breakBuildOnModifications = breakBuildOnModifications;
	}

	public boolean getBreakBuildOnBinaryIncompatibleModifications() {
		return breakBuildOnBinaryIncompatibleModifications;
	}

	void setBreakBuildOnBinaryIncompatibleModifications(
			boolean breakBuildOnBinaryIncompatibleModifications) {
		this.breakBuildOnBinaryIncompatibleModifications = breakBuildOnBinaryIncompatibleModifications;
	}

	public boolean getIncludeSynthetic() {
		return includeSynthetic;
	}

	void setIncludeSynthetic(boolean includeSynthetic) {
		this.includeSynthetic = includeSynthetic;
	}

	public List<String> getIncludes() {
		return includes;
	}

	void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public boolean isIncludeSnapshots() {
		return includeSnapshots;
	}

	void setIncludeSnapshots(boolean includeSnapshots) {
		this.includeSnapshots = includeSnapshots;
	}

	public List<String> getExcludes() {
		return excludes;
	}

	void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public boolean getIgnoreMissingClasses() {
		return ignoreMissingClasses;
	}

	void setIgnoreMissingClasses(boolean ignoreMissingClasses) {
		this.ignoreMissingClasses = ignoreMissingClasses;
	}

	public boolean getSkipPomModules() {
		return skipPomModules;
	}

	void setSkipPomModules(boolean skipPomModules) {
		this.skipPomModules = skipPomModules;
	}

	public String getHtmlStylesheet() {
		return htmlStylesheet;
	}

	void setHtmlStylesheet(String htmlStylesheet) {
		this.htmlStylesheet = htmlStylesheet;
	}

	public String getHtmlTitle() {
		return htmlTitle;
	}

	void setHtmlTitle(String htmlTitle) {
		this.htmlTitle = htmlTitle;
	}

	public String getMarkdownTitle() {
		return markdownTitle;
	}

	void setMarkdownTitle(String markdownTitle) {
		this.markdownTitle = markdownTitle;
	}

	public String getIgnoreNonResolvableArtifacts() {
		return ignoreNonResolvableArtifacts;
	}

	void setIgnoreNonResolvableArtifacts(String ignoreNonResolvableArtifacts) {
		this.ignoreNonResolvableArtifacts = ignoreNonResolvableArtifacts;
	}

	public List<String> getPackagingSupporteds() {
		return packagingSupporteds;
	}

	void setPackagingSupporteds(List<String> packagingSupporteds) {
		this.packagingSupporteds = packagingSupporteds;
	}

	public boolean getBreakBuildOnSourceIncompatibleModifications() {
		return breakBuildOnSourceIncompatibleModifications;
	}

	void setBreakBuildOnSourceIncompatibleModifications(
			boolean breakBuildOnSourceIncompatibleModifications) {
		this.breakBuildOnSourceIncompatibleModifications = breakBuildOnSourceIncompatibleModifications;
	}

	public boolean getBreakBuildBasedOnSemanticVersioning() {
		return breakBuildBasedOnSemanticVersioning;
	}

	void setBreakBuildBasedOnSemanticVersioning(boolean breakBuildBasedOnSemanticVersioning) {
		this.breakBuildBasedOnSemanticVersioning = breakBuildBasedOnSemanticVersioning;
	}

	public String getPostAnalysisScript() {
		return postAnalysisScript;
	}

	void setPostAnalysisScript(String postAnalysisScript) {
		this.postAnalysisScript = postAnalysisScript;
	}

	public boolean skipDiffReport() {
		return skipDiffReport;
	}

	void setSkipDiffReport(final boolean skipDiffReport) {
		this.skipDiffReport = skipDiffReport;
	}

	public boolean skipHtmlReport() {
		return skipHtmlReport;
	}

	void setSkipHtmlReport(final boolean skipHtmlReport) {
		this.skipHtmlReport = skipHtmlReport;
	}

	public boolean skipMarkdownReport() {
		return skipMarkdownReport;
	}

	void setSkipMarkdownReport(final boolean skipMarkdownReport) {
		this.skipMarkdownReport = skipMarkdownReport;
	}

	public boolean skipXmlReport() {
		return skipXmlReport;
	}

	void setSkipXmlReport(final boolean skipXmlReport) {
		this.skipXmlReport = skipXmlReport;
	}

	public boolean getIgnoreMissingOldVersion() {
		return ignoreMissingOldVersion;
	}

	void setIgnoreMissingOldVersion(boolean ignoreMissingOldVersion) {
		this.ignoreMissingOldVersion = ignoreMissingOldVersion;
	}

	public String getOldVersionPattern() {
		return oldVersionPattern;
	}

	void setOldVersionPattern(String oldVersionPattern) {
		this.oldVersionPattern = oldVersionPattern;
	}

	public boolean isBreakBuildIfCausedByExclusion() {
		return breakBuildIfCausedByExclusion;
	}

	void setBreakBuildIfCausedByExclusion(boolean breakBuildIfCausedByExclusion) {
		this.breakBuildIfCausedByExclusion = breakBuildIfCausedByExclusion;
	}

	public List<String> getIgnoreMissingClassesByRegularExpressions() {
		return ignoreMissingClassesByRegularExpressions;
	}

	void setIgnoreMissingClassesByRegularExpressions(
			List<String> ignoreMissingClassesByRegularExpressions) {
		this.ignoreMissingClassesByRegularExpressions = ignoreMissingClassesByRegularExpressions;
	}

	public boolean isReportOnlyFilename() {
		return reportOnlyFilename;
	}

	void setReportOnlyFilename(boolean reportOnlyFileName) {
		this.reportOnlyFilename = reportOnlyFileName;
	}

	public boolean isReportOnlySummary() {
		return reportOnlySummary;
	}

	void setReportOnlySummary(boolean reportOnlySummary) {
		this.reportOnlySummary = reportOnlySummary;
	}

	public boolean getIgnoreMissingNewVersion() {
		return ignoreMissingNewVersion;
	}

	void setIgnoreMissingNewVersion(boolean ignoreMissingNewVersion) {
		this.ignoreMissingNewVersion = ignoreMissingNewVersion;
	}

	public List<String> getIncludeModules() {
		return includeModules;
	}

	void setIncludeModules(List<String> includeModules) {
		this.includeModules = includeModules;
	}

	public List<String> getExcludeModules() {
		return excludeModules;
	}

	void setExcludeModules(List<String> excludeModules) {
		this.excludeModules = excludeModules;
	}

	public boolean isBreakBuildBasedOnSemanticVersioningForMajorVersionZero() {
		return breakBuildBasedOnSemanticVersioningForMajorVersionZero;
	}

	void setBreakBuildBasedOnSemanticVersioningForMajorVersionZero(
			boolean breakBuildBasedOnSemanticVersioningForMajorVersionZero) {
		this.breakBuildBasedOnSemanticVersioningForMajorVersionZero =
				breakBuildBasedOnSemanticVersioningForMajorVersionZero;
	}

	public boolean isIncludeExlusively() {
		return includeExclusively;
	}

	void setIncludeExclusively(boolean includeExclusively) {
		this.includeExclusively = includeExclusively;
	}

	public boolean isExcludeExclusively() {
		return excludeExclusively;
	}

	void setExcludeExclusively(boolean excludeExclusively) {
		this.excludeExclusively = excludeExclusively;
	}

	public boolean isIncludeExclusively() {
		return includeExclusively;
	}

	public List<OverrideCompatibilityChangeParameter> getOverrideCompatibilityChangeParameters() {
		return overrideCompatibilityChangeParameters;
	}

	void setOverrideCompatibilityChangeParameters(
			List<OverrideCompatibilityChangeParameter> overrideCompatibilityChangeParameters) {
		this.overrideCompatibilityChangeParameters = overrideCompatibilityChangeParameters;
	}

	public boolean isIgnoreMissingOptionalDependency() {
		return ignoreMissingOptionalDependency;
	}

	void setIgnoreMissingOptionalDependency(boolean ignoreMissingOptionalDependency) {
		this.ignoreMissingOptionalDependency = ignoreMissingOptionalDependency;
	}

	public String getReportLinkName() {
		return reportLinkName;
	}

	void setReportLinkName(String reportLinkName) {
		this.reportLinkName = reportLinkName;
	}

	/** Local class for storing Override Compatibility Change parameters. */
	public static class OverrideCompatibilityChangeParameter {
		// required parameter
		private String compatibilityChange;

		// required parameter
		private boolean binaryCompatible;

		// required parameter
		private boolean sourceCompatible;

		// required parameter
		private String semanticVersionLevel;

		public String getCompatibilityChange() {
			return compatibilityChange;
		}

		public boolean isBinaryCompatible() {
			return binaryCompatible;
		}

		public boolean isSourceCompatible() {
			return sourceCompatible;
		}

		public String getSemanticVersionLevel() {
			return semanticVersionLevel;
		}
	}
}
