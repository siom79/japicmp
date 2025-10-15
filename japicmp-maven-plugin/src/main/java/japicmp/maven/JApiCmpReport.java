package japicmp.maven;

import japicmp.config.Options;
import japicmp.output.html.HtmlOutput;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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

/** Generates the japicmp reports for the project. */
@Mojo(name = "cmp-report", defaultPhase = LifecyclePhase.SITE)
public class JApiCmpReport extends AbstractMavenReport {
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

	/** Specifies whether the HTML report generation should be skipped. */
	@Parameter(property = "japicmp.skipHtmlReport")
	boolean skipHtmlReport;
	/** Specifies whether the Markdown report generation should be skipped. */
	@Parameter(property = "japicmp.skipMarkdownReport")
	boolean skipMarkdownReport;

	/** Specifies whether the XML report generation should be skipped. */
	@Parameter(property = "japicmp.skipXmlReport")
	boolean skipXmlReport;

	/** Specifies the current project build directory. */
	@Parameter(required = true, property = "project.build.directory")
	File projectBuildDir;

	/** Remote project repositories used for the project. */
	@Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
	List<RemoteRepository> remoteProjectRepositories;

	@Parameter(defaultValue = "(,${project.version})", readonly = true)
	String versionRangeWithProjectVersion;

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


	MavenParameters mavenParameters;
	PluginParameters pluginParameters;
	JApiCmpProcessor processor;

	/**
	 * Generates the japicmp reports.
	 *
	 * @param locale the wanted locale to return the report's description, could be
	 *               <code>null</code>.
	 *
	 * @throws MavenReportException if an error occurs
	 */
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
			Optional<HtmlOutput> htmlOutputOptional = processor.execute();
			if (htmlOutputOptional.isPresent()) {
				HtmlOutput htmlOutput = htmlOutputOptional.get();
				String htmlString = htmlOutput.getHtml();
				htmlString = replaceHtmlTags(htmlString);
				writeToSink(htmlString);
			}
		} catch (Exception e) {
			String msg = "Failed to generate report: " + e.getMessage();
			Sink sink = getSink();
			sink.text(msg);
			sink.close();
			throw new MavenReportException(msg, e);
		}
	}

	private void writeToSink(final String htmlString) {
		Sink sink = getSink();
		try {
			String htmlTitle = getHtmlTitle();
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

	private static String replaceHtmlTags(String html) {
		html = html.replaceAll("</?html>", "");
		html = html.replaceAll("</?body>", "");
		html = html.replaceAll("</?head>", "");
		html = html.replaceAll("<title>[^<]*</title>", "");
		html = html.replaceAll("<META[^>]*>", "");
		return html;
	}

	private String getHtmlTitle() {
		if (pluginParameters.parameter() != null
				&& pluginParameters.parameter().getHtmlTitle() != null) {
			return pluginParameters.parameter().getHtmlTitle();
		}
		return null;
	}

	@Override
	public String getOutputName() {
		if (this.parameter != null && this.parameter.getReportLinkName() != null) {
			return this.parameter.getReportLinkName();
		}
		return "japicmp";
	}

	@Override
	public String getName(Locale locale) {
		if (this.parameter != null && this.parameter.getReportLinkName() != null) {
			return this.parameter.getReportLinkName();
		}
		return "japicmp";
	}

	@Override
	public String getDescription(Locale locale) {
		if (this.skip || isPomModuleNeedingSkip()) {
			return "skipping report";
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
		return this.pluginParameters.parameter().getSkipPomModules() && "pom".equalsIgnoreCase(
				this.project.getArtifact().getType());
	}
}
