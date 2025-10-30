package japicmp.maven;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.File;
import java.util.List;

@Mojo(name = "cmp", requiresDependencyResolution = ResolutionScope.COMPILE,
	  defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class JApiCmpMojo extends AbstractMojo {

	@Parameter
	Version oldVersion;
	@Parameter
	List<DependencyDescriptor> oldVersions;
	@Parameter
	Version newVersion;
	@Parameter
	List<DependencyDescriptor> newVersions;
	@Parameter
	ConfigParameters parameter = new ConfigParameters();
	@Parameter
	List<Dependency> dependencies;
	@Parameter
	List<Dependency> oldClassPathDependencies;
	@Parameter
	List<Dependency> newClassPathDependencies;
	@Parameter(property = "japicmp.skip", defaultValue = "false")
	boolean skip;
	@Parameter(property = "japicmp.skipDiffReport")
	boolean skipDiffReport;
	@Parameter(property = "japicmp.skipMarkdownReport")
	boolean skipMarkdownReport;
	@Parameter(property = "japicmp.skipXmlReport")
	boolean skipXmlReport;
	@Parameter(property = "japicmp.skipHtmlReport")
	boolean skipHtmlReport;
	@Parameter(property = "japicmp.breakBuildOnModifications")
	boolean breakBuildOnModifications;
	@Parameter(property = "japicmp.breakBuildOnBinaryIncompatibleModifications")
	boolean breakBuildOnBinaryIncompatibleModifications;
	@Parameter(property = "japicmp.breakBuildOnSourceIncompatibleModifications")
	boolean breakBuildOnSourceIncompatibleModifications;
	@Parameter(property = "japicmp.breakBuildBasedOnSemanticVersioning")
	boolean breakBuildBasedOnSemanticVersioning;
	@Parameter(property = "japicmp.breakBuildBasedOnSemanticVersioningForMajorVersionZero")
	boolean breakBuildBasedOnSemanticVersioningForMajorVersionZero;
	@Parameter
	File outputDirectory;
	@Parameter(readonly = true, defaultValue = "(,${project.version})")
	String versionRangeWithProjectVersion;
	@Parameter(required = true, property = "project.build.directory")
	File projectBuildDir;
	@Parameter(defaultValue = "${project}", readonly = true)
	MavenProject mavenProject;
	@Parameter(defaultValue = "${mojoExecution}", readonly = true)
	MojoExecution mojoExecution;
	@Component
	RepositorySystem repoSystem;
	@Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
	RepositorySystemSession repoSession;
	@Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
	List<RemoteRepository> remoteRepos;
	@Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true)
	List<ArtifactRepository> artifactRepositories;
	JApiCmpProcessor processor;

	public JApiCmpMojo() {
		/* Intentionally left blank. */
	}

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
