package japicmp.maven;

import java.io.File;
import java.util.List;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

/** Compares versions of the project using japicmp. */
@Mojo(name = "cmp", requiresDependencyResolution = ResolutionScope.COMPILE,
      defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class JApiCmpMojo extends AbstractMojo {

  /** The old version to compare. */
  @Parameter
  Version oldVersion;

  /** A {@code List} of old versions to compare. */
  @Parameter
  List<DependencyDescriptor> oldVersions;

  /** The new version to compare. */
  @Parameter
  Version newVersion;

  /** A {@code List} of new versions to compare. */
  @Parameter
  List<DependencyDescriptor> newVersions;

  /** The parameters defined for the plugin. */
  @Parameter
  ConfigParameters parameter;

  /** Additional dependencies to use. */
  @Parameter
  List<Dependency> dependencies;

  /** The classpath for the old dependencies. */
  @Parameter
  List<Dependency> oldClassPathDependencies;

  /** The classpath for the new dependencies. */
  @Parameter
  List<Dependency> newClassPathDependencies;

  /** Specifies whether the report generation should be skipped. */
  @Parameter(property = "japicmp.skip", defaultValue = "false")
  boolean skip;

  /** Specifies whether the Diff report generation should be skipped. */
  @Parameter(property = "japicmp.skipDiffReport")
  boolean skipDiffReport;

  /** Specifies whether the Markdown report generation should be skipped. */
  @Parameter(property = "japicmp.skipMarkdownReport")
  boolean skipMarkdownReport;

  /** Specifies whether the XML report generation should be skipped. */
  @Parameter(property = "japicmp.skipXmlReport")
  boolean skipXmlReport;

  /** Specifies whether the HTML report generation should be skipped. */
  @Parameter(property = "japicmp.skipHtmlReport")
  boolean skipHtmlReport;

  /** Specifies whether to break the build on modifications. */
  @Parameter(property = "japicmp.breakBuildOnModifications")
  boolean breakBuildOnModifications;

  /** Specifies whether to break the build on binary incompatible modifications. */
  @Parameter(property = "japicmp.breakBuildOnBinaryIncompatibleModifications")
  boolean breakBuildOnBinaryIncompatibleModifications;

  /** Specifies whether to break the build on source incompatible modifications. */
  @Parameter(property = "japicmp.breakBuildOnSourceIncompatibleModifications")
  boolean breakBuildOnSourceIncompatibleModifications;

  /** Specifies whether to break the build on semantic versioning. */
  @Parameter(property = "japicmp.breakBuildBasedOnSemanticVersioning")
  boolean breakBuildBasedOnSemanticVersioning;

  /** Specifies whether to break the build on semantic versioning for major version zero. */
  @Parameter(property = "japicmp.breakBuildBasedOnSemanticVersioningForMajorVersionZero")
  boolean breakBuildBasedOnSemanticVersioningForMajorVersionZero;

  /** The report(s) output directory. */
  @Parameter
  File outputDirectory;

  /** The version range to compare. */
  @Parameter(readonly = true, defaultValue = "(,${project.version})")
  String versionRangeWithProjectVersion;

  /** Specifies the current project build directory. */
  @Parameter(required = true, property = "project.build.directory")
  File projectBuildDir;

  /** A reference to the current Maven project. */
  @Parameter(defaultValue = "${project}", readonly = true)
  MavenProject mavenProject;

  /** A reference to the current Maven execution object. */
  @Parameter(defaultValue = "${mojoExecution}", readonly = true)
  MojoExecution mojoExecution;

  /** A reference to the current Maven repository system. */
  @Component
  RepositorySystem repoSystem;

  @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
  RepositorySystemSession repoSession;

  /** Specifies the {@code List} of remote repositories. */
  @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
  List<RemoteRepository> remoteRepos;

  /** Specifies the {@code List} of remote artifact repositories. */
  @Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true)
  List<ArtifactRepository> artifactRepositories;

  /*
   * Class variable to support unit tests.
   */
  JApiCmpProcessor processor;

  /**
   * Default constructor.
   */
  public JApiCmpMojo() {
    /* Intentionally left blank. */
  }

  /**
   * Executes the japicmp comparison via {@link JApiCmpProcessor}.
   *
   * @throws MojoExecutionException if an error occurs during execution
   * @throws MojoFailureException   if an error occurs during processing
   */
  public void execute() throws MojoExecutionException, MojoFailureException {
    MavenParameters mavenParameters = new MavenParameters(this.artifactRepositories,
                                                          this.mavenProject, this.mojoExecution,
                                                          this.versionRangeWithProjectVersion,
                                                          this.repoSystem, this.repoSession,
                                                          this.remoteRepos);
    PluginParameters pluginParameters = new PluginParameters(this.skip,
                                                             this.newVersion,
                                                             this.oldVersion, this.parameter,
                                                             this.dependencies,
                                                             this.projectBuildDir,
                                                             this.outputDirectory, true,
                                                             this.oldVersions, this.newVersions,
                                                             this.oldClassPathDependencies,
                                                             this.newClassPathDependencies,
                                                             new SkipReport(
                                                                     this.skipDiffReport,
                                                                     this.skipHtmlReport,
                                                                     this.skipMarkdownReport,
                                                                     this.skipXmlReport),
                                                             new BreakBuild(
                                                                     this.breakBuildBasedOnSemanticVersioning,
                                                                     this.breakBuildBasedOnSemanticVersioningForMajorVersionZero,
                                                                     this.breakBuildOnBinaryIncompatibleModifications,
                                                                     this.breakBuildOnSourceIncompatibleModifications,
                                                                     this.breakBuildOnModifications));

    processor = new JApiCmpProcessor(pluginParameters, mavenParameters, getLog());
    processor.execute();
  }
}
