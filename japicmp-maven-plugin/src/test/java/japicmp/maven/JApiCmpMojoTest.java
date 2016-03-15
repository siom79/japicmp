package japicmp.maven;

import com.google.common.base.Optional;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.mockito.Matchers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JApiCmpMojoTest {

	@Test
	public void testSimple() throws MojoFailureException {
		JApiCmpMojo mojo = new JApiCmpMojo();
		Version oldVersion = createVersion("groupId", "artifactId", "0.1.0");
		Version newVersion = createVersion("groupId", "artifactId", "0.1.1");
		PluginParameters pluginParameters = new PluginParameters(null, newVersion, oldVersion, null, null, Optional.of(Paths.get(System.getProperty("user.dir"), "target", "simple").toFile()), Optional.<String>absent(), true, null, null, null, null);
		ArtifactResolver artifactResolver = mock(ArtifactResolver.class);
		ArtifactResolutionResult artifactResolutionResult = mock(ArtifactResolutionResult.class);
		Set<Artifact> artifactSet = new HashSet<>();
		Artifact resolvedArtifact = mock(Artifact.class);
		artifactSet.add(resolvedArtifact);
		when(resolvedArtifact.getFile()).thenReturn(Paths.get(System.getProperty("user.dir"), "target", "guava-18.0.jar").toFile());
		when(artifactResolutionResult.getArtifacts()).thenReturn(artifactSet);
		when(artifactResolver.resolve(Matchers.<ArtifactResolutionRequest>anyObject())).thenReturn(artifactResolutionResult);
		ArtifactFactory artifactFactory = mock(ArtifactFactory.class);
		when(artifactFactory.createArtifactWithClassifier(eq("groupId"), eq("artifactId"), eq("0.1.1"), anyString(), anyString())).thenReturn(mock(Artifact.class));
		MavenParameters mavenParameters = new MavenParameters(new ArrayList<ArtifactRepository>(), artifactFactory, mock(ArtifactRepository.class), artifactResolver, mock(MavenProject.class), mock(MojoExecution.class));
		mojo.executeWithParameters(pluginParameters, mavenParameters);
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "simple", "japicmp", "japicmp.diff")), is(true));
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "simple", "japicmp", "japicmp.xml")), is(true));
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "simple", "japicmp", "japicmp.html")), is(true));
	}

	@Test
	public void testNoXmlAndNoHtmlReport() throws MojoFailureException {
		JApiCmpMojo mojo = new JApiCmpMojo();
		Version oldVersion = createVersion("groupId", "artifactId", "0.1.0");
		Version newVersion = createVersion("groupId", "artifactId", "0.1.1");
		Parameter parameter = new Parameter();
		parameter.setSkipHtmlReport("true");
		parameter.setSkipXmlReport("true");
		PluginParameters pluginParameters = new PluginParameters(null, newVersion, oldVersion, parameter, null, Optional.of(Paths.get(System.getProperty("user.dir"), "target", "noXmlAndNoHtmlReport").toFile()), Optional.<String>absent(), true, null, null, null, null);
		ArtifactResolver artifactResolver = mock(ArtifactResolver.class);
		ArtifactResolutionResult artifactResolutionResult = mock(ArtifactResolutionResult.class);
		Set<Artifact> artifactSet = new HashSet<>();
		Artifact resolvedArtifact = mock(Artifact.class);
		artifactSet.add(resolvedArtifact);
		when(resolvedArtifact.getFile()).thenReturn(Paths.get(System.getProperty("user.dir"), "target", "guava-18.0.jar").toFile());
		when(artifactResolutionResult.getArtifacts()).thenReturn(artifactSet);
		when(artifactResolver.resolve(Matchers.<ArtifactResolutionRequest>anyObject())).thenReturn(artifactResolutionResult);
		ArtifactFactory artifactFactory = mock(ArtifactFactory.class);
		when(artifactFactory.createArtifactWithClassifier(eq("groupId"), eq("artifactId"), eq("0.1.1"), anyString(), anyString())).thenReturn(mock(Artifact.class));
		MavenParameters mavenParameters = new MavenParameters(new ArrayList<ArtifactRepository>(), artifactFactory, mock(ArtifactRepository.class), artifactResolver, mock(MavenProject.class), mock(MojoExecution.class));
		mojo.executeWithParameters(pluginParameters, mavenParameters);
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "noXmlAndNoHtmlReport", "japicmp", "japicmp.diff")), is(true));
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "noXmlAndNoHtmlReport", "japicmp", "japicmp.xml")), is(false));
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "noXmlAndNoHtmlReport", "japicmp", "japicmp.html")), is(false));
	}

	private Version createVersion(String groupId, String artifactId, String version) {
		Version versionInstance = new Version();
		Dependency dependency = new Dependency();
		dependency.setGroupId(groupId);
		dependency.setArtifactId(artifactId);
		dependency.setVersion(version);
		versionInstance.setDependency(dependency);
		return versionInstance;
	}
}
