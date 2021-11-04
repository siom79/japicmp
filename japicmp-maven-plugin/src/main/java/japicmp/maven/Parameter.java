package japicmp.maven;

import java.util.List;

public class Parameter {
	private String accessModifier;
	private List<String> includes;
	private List<String> excludes;
	private boolean onlyBinaryIncompatible;
	private boolean onlyModified;
	private boolean breakBuildOnModifications;
	private boolean breakBuildOnBinaryIncompatibleModifications;
	private boolean breakBuildOnSourceIncompatibleModifications;
	private boolean breakBuildBasedOnSemanticVersioning;
	private boolean includeSynthetic;
	private boolean ignoreMissingClasses;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private List<String> ignoreMissingClassesByRegularExpressions;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private boolean skipPomModules = true;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private String htmlStylesheet;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private String htmlTitle;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private boolean noAnnotations;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private String ignoreNonResolvableArtifacts;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private List<String> packagingSupporteds;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private String postAnalysisScript;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private boolean skipHtmlReport;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private boolean skipXmlReport;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private boolean skipDiffReport;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private boolean ignoreMissingOldVersion;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private boolean ignoreMissingNewVersion;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private String oldVersionPattern;
	@org.apache.maven.plugins.annotations.Parameter(required = false, defaultValue = "false")
	private boolean includeSnapshots;
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
	private boolean ignoreMissingOptionalDependency;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private String reportLinkName;

	public static class OverrideCompatibilityChangeParameter {
		@org.apache.maven.plugins.annotations.Parameter(required = true)
		private String compatibilityChange;
		@org.apache.maven.plugins.annotations.Parameter(required = true)
		private boolean binaryCompatible;
		@org.apache.maven.plugins.annotations.Parameter(required = true)
		private boolean sourceCompatible;
		@org.apache.maven.plugins.annotations.Parameter(required = true)
		private String semanticVersionLevel;

		public String getCompatibilityChange() {
			return compatibilityChange;
		}

		public void setCompatibilityChange(String compatibilityChange) {
			this.compatibilityChange = compatibilityChange;
		}

		public boolean isBinaryCompatible() {
			return binaryCompatible;
		}

		public void setBinaryCompatible(boolean binaryCompatible) {
			this.binaryCompatible = binaryCompatible;
		}

		public boolean isSourceCompatible() {
			return sourceCompatible;
		}

		public void setSourceCompatible(boolean sourceCompatible) {
			this.sourceCompatible = sourceCompatible;
		}

		public String getSemanticVersionLevel() {
			return semanticVersionLevel;
		}

		public void setSemanticVersionLevel(String semanticVersionLevel) {
			this.semanticVersionLevel = semanticVersionLevel;
		}
	}

	public boolean getNoAnnotations() {
		return noAnnotations;
	}

	public void setNoAnnotations(boolean noAnnotations) {
		this.noAnnotations = noAnnotations;
	}

	public String getAccessModifier() {
		return accessModifier;
	}

	public void setAccessModifier(String accessModifier) {
		this.accessModifier = accessModifier;
	}

	public boolean getOnlyModified() {
		return onlyModified;
	}

	public void setOnlyModified(boolean onlyModified) {
		this.onlyModified = onlyModified;
	}

	public boolean getOnlyBinaryIncompatible() {
		return onlyBinaryIncompatible;
	}

	public void setOnlyBinaryIncompatible(boolean onlyBinaryIncompatible) {
		this.onlyBinaryIncompatible = onlyBinaryIncompatible;
	}

	public boolean getBreakBuildOnModifications() {
		return breakBuildOnModifications;
	}

	public void setBreakBuildOnModifications(boolean breakBuildOnModifications) {
		this.breakBuildOnModifications = breakBuildOnModifications;
	}

	public boolean getBreakBuildOnBinaryIncompatibleModifications() {
		return breakBuildOnBinaryIncompatibleModifications;
	}

	public void setBreakBuildOnBinaryIncompatibleModifications(boolean breakBuildOnBinaryIncompatibleModifications) {
		this.breakBuildOnBinaryIncompatibleModifications = breakBuildOnBinaryIncompatibleModifications;
	}

	public boolean getIncludeSynthetic() {
		return includeSynthetic;
	}

	public void setIncludeSynthetic(boolean includeSynthetic) {
		this.includeSynthetic = includeSynthetic;
	}

	public List<String> getIncludes() {
		return includes;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public boolean isIncludeSnapshots() {
		return includeSnapshots;
	}

	public void setIncludeSnapshots(boolean includeSnapshots) {
		this.includeSnapshots = includeSnapshots;
	}

	public List<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public boolean getIgnoreMissingClasses() {
		return ignoreMissingClasses;
	}

	public void setIgnoreMissingClasses(boolean ignoreMissingClasses) {
		this.ignoreMissingClasses = ignoreMissingClasses;
	}

	public boolean getSkipPomModules() {
		return skipPomModules;
	}

	public void setSkipPomModules(boolean skipPomModules) {
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

	public boolean getBreakBuildOnSourceIncompatibleModifications() {
		return breakBuildOnSourceIncompatibleModifications;
	}

	public void setBreakBuildOnSourceIncompatibleModifications(boolean breakBuildOnSourceIncompatibleModifications) {
		this.breakBuildOnSourceIncompatibleModifications = breakBuildOnSourceIncompatibleModifications;
	}

	public boolean getBreakBuildBasedOnSemanticVersioning() {
		return breakBuildBasedOnSemanticVersioning;
	}

	public void setBreakBuildBasedOnSemanticVersioning(boolean breakBuildBasedOnSemanticVersioning) {
		this.breakBuildBasedOnSemanticVersioning = breakBuildBasedOnSemanticVersioning;
	}

	public String getPostAnalysisScript() {
		return postAnalysisScript;
	}

	public void setPostAnalysisScript(String postAnalysisScript) {
		this.postAnalysisScript = postAnalysisScript;
	}

	public boolean getSkipHtmlReport() {
		return skipHtmlReport;
	}

	public void setSkipHtmlReport(boolean skipHtmlReport) {
		this.skipHtmlReport = skipHtmlReport;
	}

	public boolean getSkipXmlReport() {
		return skipXmlReport;
	}

	public void setSkipXmlReport(boolean skipXmlReport) {
		this.skipXmlReport = skipXmlReport;
	}

	public boolean isSkipDiffReport() {
		return skipDiffReport;
	}

	public void setSkipDiffReport(boolean skipDiffReport) {
		this.skipDiffReport = skipDiffReport;
	}

	public boolean getIgnoreMissingOldVersion() {
		return ignoreMissingOldVersion;
	}

	public void setIgnoreMissingOldVersion(boolean ignoreMissingOldVersion) {
		this.ignoreMissingOldVersion = ignoreMissingOldVersion;
	}

	public String getOldVersionPattern() {
		return oldVersionPattern;
	}

	public void setOldVersionPattern(String oldVersionPattern) {
		this.oldVersionPattern = oldVersionPattern;
	}

	public boolean isBreakBuildIfCausedByExclusion() {
		return breakBuildIfCausedByExclusion;
	}

	public void setBreakBuildIfCausedByExclusion(boolean breakBuildIfCausedByExclusion) {
		this.breakBuildIfCausedByExclusion = breakBuildIfCausedByExclusion;
	}

	public List<String> getIgnoreMissingClassesByRegularExpressions() {
		return ignoreMissingClassesByRegularExpressions;
	}

	public void setIgnoreMissingClassesByRegularExpressions(List<String> ignoreMissingClassesByRegularExpressions) {
		this.ignoreMissingClassesByRegularExpressions = ignoreMissingClassesByRegularExpressions;
	}

	public boolean isReportOnlyFilename() {
		return reportOnlyFilename;
	}

	public void setReportOnlyFilename(boolean reportOnlyFileName) {
		this.reportOnlyFilename = reportOnlyFileName;
	}

	public boolean getIgnoreMissingNewVersion() {
		return ignoreMissingNewVersion;
	}

	public void setIgnoreMissingNewVersion(boolean ignoreMissingNewVersion) {
		this.ignoreMissingNewVersion = ignoreMissingNewVersion;
	}

	public List<String> getIncludeModules() {
		return includeModules;
	}

	public void setIncludeModules(List<String> includeModules) {
		this.includeModules = includeModules;
	}

	public List<String> getExcludeModules() {
		return excludeModules;
	}

	public void setExcludeModules(List<String> excludeModules) {
		this.excludeModules = excludeModules;
	}

	public boolean isBreakBuildBasedOnSemanticVersioningForMajorVersionZero() {
		return breakBuildBasedOnSemanticVersioningForMajorVersionZero;
	}

	public void setBreakBuildBasedOnSemanticVersioningForMajorVersionZero(boolean breakBuildBasedOnSemanticVersioningForMajorVersionZero) {
		this.breakBuildBasedOnSemanticVersioningForMajorVersionZero = breakBuildBasedOnSemanticVersioningForMajorVersionZero;
	}

	public boolean isIncludeExlusively() {
		return includeExclusively;
	}

	public void setIncludeExclusively(boolean includeExclusively) {
		this.includeExclusively = includeExclusively;
	}

	public boolean isExcludeExclusively() {
		return excludeExclusively;
	}

	public void setExcludeExclusively(boolean excludeExclusively) {
		this.excludeExclusively = excludeExclusively;
	}

	public boolean isIncludeExclusively() {
		return includeExclusively;
	}

	public List<OverrideCompatibilityChangeParameter> getOverrideCompatibilityChangeParameters() {
		return overrideCompatibilityChangeParameters;
	}

	public void setOverrideCompatibilityChangeParameters(List<OverrideCompatibilityChangeParameter> overrideCompatibilityChangeParameters) {
		this.overrideCompatibilityChangeParameters = overrideCompatibilityChangeParameters;
	}

	public boolean isIgnoreMissingOptionalDependency() {
		return ignoreMissingOptionalDependency;
	}

	public void setIgnoreMissingOptionalDependency(boolean ignoreMissingOptionalDependency) {
		this.ignoreMissingOptionalDependency = ignoreMissingOptionalDependency;
	}

	public String getReportLinkName() {
		return reportLinkName;
	}

	public void setReportLinkName(String reportLinkName) {
		this.reportLinkName = reportLinkName;
	}
}
