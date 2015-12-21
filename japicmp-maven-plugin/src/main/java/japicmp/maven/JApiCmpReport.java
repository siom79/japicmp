package japicmp.maven;

import com.google.common.base.Optional;
import japicmp.output.xml.XmlOutput;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

@Mojo(name = "cmp-report", defaultPhase = LifecyclePhase.SITE)
public class JApiCmpReport extends AbstractMavenReport {
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private Version oldVersion;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private List<DependencyDescriptor> oldVersions;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private Version newVersion;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private List<DependencyDescriptor> newVersions;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private Parameter parameter;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private List<Dependency> dependencies;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private List<Dependency> oldClassPathDependencies;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private List<Dependency> newClassPathDependencies;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private String skip;
	@org.apache.maven.plugins.annotations.Parameter(required = true, readonly = true, property = "project.reporting.outputDirectory")
	private String outputDirectory;
	@Component
	private ArtifactFactory artifactFactory;
	@Component
	private ArtifactResolver artifactResolver;
	@org.apache.maven.plugins.annotations.Parameter(required = true, defaultValue = "${localRepository}")
	private ArtifactRepository localRepository;
	@org.apache.maven.plugins.annotations.Parameter(required = true, defaultValue = "${project.remoteArtifactRepositories}")
	private List<ArtifactRepository> artifactRepositories;
	@org.apache.maven.plugins.annotations.Parameter(required = true, defaultValue = "${project}")
	private MavenProject mavenProject;
	@org.apache.maven.plugins.annotations.Parameter( defaultValue = "${mojoExecution}", readonly = true )
	private MojoExecution mojoExecution;

	@Override
	protected void executeReport(Locale locale) throws MavenReportException {
		try {
			JApiCmpMojo mojo = new JApiCmpMojo();
			MavenParameters mavenParameters = new MavenParameters(artifactRepositories, artifactFactory, localRepository, artifactResolver, mavenProject, mojoExecution);
			PluginParameters pluginParameters = new PluginParameters(skip, newVersion, oldVersion, parameter, dependencies, outputDirectory, false, oldVersions, newVersions, oldClassPathDependencies, newClassPathDependencies);
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
