package japicmp.maven;

import java.util.List;
import org.apache.maven.plugins.annotations.Parameter;

/** Class for storing the japicmp configuration parameters. */
public class ConfigParameters {

  /** Process only modified classes. */
  @Parameter
  private boolean onlyModified;

  /** Access modifier. */
  @Parameter
  private String accessModifier;

  @Parameter
  private List<String> includes;

  @Parameter
  private List<String> excludes;

  @Parameter
  private boolean onlyBinaryIncompatible;

  @Parameter
  private boolean breakBuildBasedOnSemanticVersioning;

  @Parameter
  private boolean breakBuildBasedOnSemanticVersioningForMajorVersionZero;

  @Parameter
  private boolean breakBuildOnModifications;

  @Parameter
  private boolean breakBuildOnBinaryIncompatibleModifications;

  @Parameter
  private boolean breakBuildOnSourceIncompatibleModifications;

  @Parameter
  private boolean includeSynthetic;

  @Parameter
  private boolean ignoreMissingClasses;

  @Parameter
  private List<String> ignoreMissingClassesByRegularExpressions;

  @Parameter(defaultValue = "true")
  private boolean skipPomModules;

  @Parameter
  private String htmlStylesheet;

  @Parameter
  private String htmlTitle;

  @Parameter
  private String markdownTitle;

  @Parameter
  private boolean noAnnotations;

  @Parameter
  private String ignoreNonResolvableArtifacts;

  @Parameter
  private List<String> packagingSupporteds;

  @Parameter
  private String postAnalysisScript;

  @Parameter
  private boolean skipDiffReport;

  @Parameter
  private boolean skipHtmlReport;

  @Parameter
  private boolean skipMarkdownReport;

  @Parameter
  private boolean skipXmlReport;

  @Parameter(defaultValue = "true")
  private boolean ignoreMissingOldVersion;

  @Parameter
  private boolean ignoreMissingNewVersion;

  @Parameter
  private String oldVersionPattern;

  @Parameter(defaultValue = "false")
  private boolean includeSnapshots;

  @Parameter(defaultValue = "true")
  private boolean breakBuildIfCausedByExclusion;

  @Parameter
  private boolean reportOnlyFilename;

  @Parameter
  private boolean reportOnlySummary;

  @Parameter
  private List<String> includeModules;

  @Parameter
  private List<String> excludeModules;


  @Parameter(defaultValue = "false")
  private boolean includeExclusively;

  @Parameter(defaultValue = "false")
  private boolean excludeExclusively;

  @Parameter
  private List<OverrideCompatibilityChangeParameter> overrideCompatibilityChangeParameters;

  @Parameter
  private boolean ignoreMissingOptionalDependency;

  @Parameter
  private String reportLinkName;

  /**
   * Default Contructor.
   */
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
    @Parameter(required = true)
    private String compatibilityChange;

    @Parameter(required = true)
    private boolean binaryCompatible;

    @Parameter(required = true)
    private boolean sourceCompatible;

    @Parameter(required = true)
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
