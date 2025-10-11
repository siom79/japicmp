package japicmp.maven;

import java.io.File;
import java.util.List;

/** Plugin parameters defined for the JApiCmp plugin. */
public class PluginParameters {
  private final boolean skip;
  private final SkipReport skipReport;
  private final Version oldVersion;
  private final List<DependencyDescriptor> oldVersions;
  private final Version newVersion;
  private final List<DependencyDescriptor> newVersions;
  private final ConfigParameters parameter;
  private final List<Dependency> dependencies;
  private final List<Dependency> oldClassPathDependencies;
  private final List<Dependency> newClassPathDependencies;
  private final File projectBuildDir;
  private final File outputDirectory;
  private final boolean writeToFiles;
  private final BreakBuild breakBuild;

  /**
   * Creates a new {@code PluginParameters} instance with the given values.
   *
   * @param skip
   * @param newVersion
   * @param oldVersion
   * @param parameter
   * @param dependencies
   * @param projectBuildDir
   * @param outputDirectory
   * @param writeToFiles
   * @param oldVersions
   * @param newVersions
   * @param oldClassPathDependencies
   * @param newClassPathDependencies
   * @param skipReport
   * @param breakBuild
   */
  public PluginParameters(final boolean skip,
                          final Version newVersion,
                          final Version oldVersion,
                          final ConfigParameters parameter, final List<Dependency> dependencies,
                          final File projectBuildDir, final File outputDirectory,
                          final boolean writeToFiles,
                          final List<DependencyDescriptor> oldVersions,
                          final List<DependencyDescriptor> newVersions,
                          final List<Dependency> oldClassPathDependencies,
                          final List<Dependency> newClassPathDependencies,
                          final SkipReport skipReport, final BreakBuild breakBuild) {
    this.skip = skip;
    this.newVersion = newVersion;
    this.oldVersion = oldVersion;
    this.parameter = parameter == null ? new ConfigParameters() : parameter;
    this.dependencies = dependencies;
    this.oldClassPathDependencies = oldClassPathDependencies;
    this.newClassPathDependencies = newClassPathDependencies;
    this.projectBuildDir = projectBuildDir;
    this.outputDirectory = outputDirectory;
    this.writeToFiles = writeToFiles;
    this.oldVersions = oldVersions;
    this.newVersions = newVersions;
    this.skipReport = skipReport;
    this.breakBuild = breakBuild;
  }

  public boolean skip() {
    return skip;
  }

  public Version newVersion() {
    return newVersion;
  }

  public Version oldVersion() {
    return oldVersion;
  }

  public ConfigParameters parameter() {
    return parameter;
  }

  public List<Dependency> dependencies() {
    return dependencies;
  }

  public File projectBuildDir() {
    return projectBuildDir;
  }

  public File outputDirectory() {
    return outputDirectory;
  }

  public boolean isWriteToFiles() {
    return writeToFiles;
  }

  public List<DependencyDescriptor> oldVersions() {
    return oldVersions;
  }

  public List<DependencyDescriptor> newVersions() {
    return newVersions;
  }

  public List<Dependency> oldClassPathDependencies() {
    return oldClassPathDependencies;
  }

  public List<Dependency> newClassPathDependencies() {
    return newClassPathDependencies;
  }

  public BreakBuild breakBuild() {
    return breakBuild;
  }

  public SkipReport skipReport() {
    return skipReport;
  }
}
