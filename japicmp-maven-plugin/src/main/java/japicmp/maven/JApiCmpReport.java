package japicmp.maven;

import com.google.common.base.Optional;
import japicmp.output.xml.XmlOutput;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * @goal cmp-report
 * @phase site
 */
public class JApiCmpReport extends AbstractMavenReport {
	/**
	 * @parameter
	 * @required
	 */
	private Version oldVersion;

	/**
	 * @parameter
	 * @required
	 */
	private Version newVersion;

	/**
	 * @parameter
	 */
	private Parameter parameter;

	/**
	 * @parameter
	 */
	private List<Dependency> dependencies;

	/**
	 * @parameter
	 */
	private List<Dependency> newClassPathDependencies;

	/**
	 * @parameter
	 */
	private List<Dependency> oldClassPathDependencies;

	/**
	 * @parameter
	 */
	private String skip;

	/**
	 * Directory where reports will go.
	 *
	 * @parameter property="project.reporting.outputDirectory"
	 * @required
	 * @readonly
	 */
	private String outputDirectory;

	/**
	 * @component
	 * @required
	 */
	private ArtifactFactory artifactFactory;

	/**
	 * @component
	 * @required
	 */
	private ArtifactResolver artifactResolver;

	/**
	 * @parameter default-value="${localRepository}"
	 * @required
	 */
	private ArtifactRepository localRepository;

	/**
	 * @parameter default-value="${project.remoteArtifactRepositories}"
	 * @required
	 */
	private List<ArtifactRepository> artifactRepositories;

	/**
	 * @parameter default-value="${project}"
	 * @required
	 */
	private MavenProject mavenProject;

	@Override
	protected void executeReport(Locale locale) throws MavenReportException {
		try {
			JApiCmpMojo mojo = new JApiCmpMojo();
			MavenParameters mavenParameters = new MavenParameters(artifactRepositories, artifactFactory, localRepository, artifactResolver, mavenProject);
			PluginParameters pluginParameters = new PluginParameters(skip, newVersion, oldVersion, parameter, dependencies, oldClassPathDependencies, newClassPathDependencies, Optional.<File>absent(), Optional.of(outputDirectory), false);
			Optional<XmlOutput> xmlOutputOptional = mojo.executeWithParameters(pluginParameters, mavenParameters);
			if (xmlOutputOptional.isPresent()) {
				XmlOutput xmlOutput = xmlOutputOptional.get();
				if (xmlOutput.getHtmlOutputStream().isPresent()) {
					ByteArrayOutputStream htmlOutputStream = xmlOutput.getHtmlOutputStream().get();
					String htmlString = htmlOutputStream.toString("UTF-8");
					htmlString = htmlString.replaceAll("</?html>", "");
					htmlString = htmlString.replaceAll("</?body>", "");
					htmlString = htmlString.replaceAll("</?head>", "");
					htmlString = htmlString.replaceAll("<title>[^<]*</title>", "");
					htmlString = htmlString.replaceAll("<META[^>]*>", "");
					Sink sink = getSink();
					sink.rawText(htmlString);
					sink.close();
				}
			}
		} catch (Exception e) {
			String msg = "Failed to generate report: " + e.getMessage();
			Sink sink = getSink();
			sink.text(msg);
			sink.close();
			throw new MavenReportException(msg, e);
		}
	}

	@Override
	public String getOutputName() {
		return "japicmp-maven-plugin-report";
	}

	@Override
	public String getName(Locale locale) {
		return "japicmp-maven-plugin";
	}

	@Override
	public String getDescription(Locale locale) {
		return "japicmp is a maven plugin that computes the differences between two versions of a jar file/artifact.";
	}
}
