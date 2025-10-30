package japicmp.maven;

import japicmp.config.Options;
import japicmp.output.html.HtmlOutput;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Mojo(name = "cmp-report", defaultPhase = LifecyclePhase.SITE)
public class JApiCmpReport extends AbstractMavenReport {
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
	@Parameter(property = "japicmp.skipHtmlReport")
	boolean skipHtmlReport;
	@Parameter(property = "japicmp.skipMarkdownReport")
	boolean skipMarkdownReport;
	@Parameter(property = "japicmp.skipXmlReport")
	boolean skipXmlReport;
	@Parameter(required = true, property = "project.build.directory")
	File projectBuildDir;
	@Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
	List<RemoteRepository> remoteProjectRepositories;
	@Parameter(defaultValue = "(,${project.version})", readonly = true)
	String versionRangeWithProjectVersion;
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
	MavenParameters mavenParameters;
	PluginParameters pluginParameters;
	JApiCmpProcessor processor;

	public JApiCmpReport() {
		/* Intentionally left blank. */
	}

	@Override
	protected void executeReport(Locale locale) throws MavenReportException {
		mavenParameters = new MavenParameters(this.artifactRepositories, this.project,
				this.mojoExecution, this.versionRangeWithProjectVersion,
				this.repoSystem, this.repoSession,
				this.remoteProjectRepositories);
		pluginParameters = new PluginParameters(this.skip,
				this.newVersion, this.oldVersion, this.parameter,
				this.dependencies, this.projectBuildDir,
				this.outputDirectory, true, this.oldVersions,
				this.newVersions, this.oldClassPathDependencies,
				this.newClassPathDependencies,
				new SkipReport(
						this.skipDiffReport,
						this.skipHtmlReport,
						this.skipMarkdownReport,
						this.skipXmlReport),
				new BreakBuild());
		try {
			processor = new JApiCmpProcessor(pluginParameters, mavenParameters, getLog());
			final Optional<HtmlOutput> htmlOutputOptional = processor.execute();
			if (htmlOutputOptional.isPresent()) {
				final HtmlOutput htmlOutput = htmlOutputOptional.get();
				String htmlString = htmlOutput.getHtml();
				htmlString = replaceHtmlTags(htmlString);
				writeToSink(htmlString);
			}
		} catch (Exception e) {
			final String msg = "Failed to generate report: " + e.getMessage();
			final Sink sink = getSink();
			sink.text(msg);
			sink.close();
			throw new MavenReportException(msg, e);
		}
	}

	private void writeToSink(final String htmlString) {
		final Sink sink = getSink();
		try {
			final String htmlTitle = getHtmlTitle();
			if (htmlTitle != null) {
				sink.head();
				sink.title();
				sink.text(pluginParameters.parameter().getHtmlTitle());
				sink.title_();
				sink.head_();
			}
			sink.rawText(htmlString);
		} finally {
			sink.close();
		}
	}

	private static String replaceHtmlTags(final String html) {
		String newHtml = html.replaceAll("</?html>", "");
		newHtml = newHtml.replaceAll("</?body>", "");
		newHtml = newHtml.replaceAll("</?head>", "");
		newHtml = newHtml.replaceAll("<title>[^<]*</title>", "");
		newHtml = newHtml.replaceAll("<META[^>]*>", "");
		return newHtml;
	}

	private String getHtmlTitle() {
		String ret = null;
		if (this.parameter.getHtmlTitle() != null) {
			ret = pluginParameters.parameter().getHtmlTitle();
		}
		return ret;
	}

	@Override
	public String getOutputName() {
		String ret = "japicmp";
		if (this.parameter.getReportLinkName() != null) {
			ret = this.parameter.getReportLinkName().replace(' ', '_');
		}
		return ret;
	}

	@Override
	public String getName(Locale locale) {
		String ret = "japicmp";
		if (this.parameter.getReportLinkName() != null) {
			ret = this.parameter.getReportLinkName();
		}
		return ret;
	}

	@Override
	public String getDescription(Locale locale) {
		if (this.skip || isPomModuleNeedingSkip()) {
			return "Skipping report";
		}
		Options options;
		try {
			options = processor.getOptions();
		} catch (MojoFailureException e) {
			return "failed report";
		}
		if (options == null) {
			return "failed report";
		}
		return options.getDifferenceDescription();
	}

	private boolean isPomModuleNeedingSkip() {
		return this.parameter.getSkipPomModules() && "pom".equalsIgnoreCase(
				this.project.getArtifact().getType());
	}
}
