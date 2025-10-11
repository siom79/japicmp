package japicmp.maven;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class JApiCmpMojoTest extends AbstractTest {

  ConfigParameters configParams;
  MavenParameters mavenParams;
  PluginParameters pluginParams;

  @BeforeEach
  void setup() {
    configParams = new ConfigParameters();
    mavenParams = createMavenParameters();
    pluginParams = createPluginParameters(configParams);
  }


  @Test
  void testIgnoreMissingVersions() {
    //		JApiCmpMojo mojo = new JApiCmpMojo();
    //		Version oldVersion = createVersion("groupId", "artifactId", "0.1.0");
    //		Version newVersion = createVersion("groupId", "artifactId", "0.1.1");
    //		Parameter parameterParam = new Parameter();
    //		parameterParam.setIgnoreMissingNewVersion(true);
    //		parameterParam.setIgnoreMissingOldVersion(true);
    //		PluginParameters pluginParameters = new PluginParameters(false, newVersion, oldVersion, parameterParam, null, Optional.of(Paths.get(System.getProperty("user.dir"), "target", "simple").toFile()), Optional.<String>absent(), true, null, null, null, null);
    //		ArtifactResolutionResult artifactResolutionResult = mock(ArtifactResolutionResult.class);
    //		Set<Artifact> artifactSet = new HashSet<>();
    //		when(artifactResolutionResult.getArtifacts()).thenReturn(artifactSet);
    //		when(artifactResolver.resolve(Matchers.<ArtifactResolutionRequest>anyObject())).thenReturn(artifactResolutionResult);
    //		ArtifactFactory artifactFactory = mock(ArtifactFactory.class);
    //		when(artifactFactory.createArtifactWithClassifier(eq("groupId"), eq("artifactId"), eq("0.1.1"), anyString(), anyString())).thenReturn(mock(Artifact.class));
    //		MojoExecution mojoExecution = mock(MojoExecution.class);
    //		String executionId = "ignoreMissingVersions";
    //		when(mojoExecution.getExecutionId()).thenReturn(executionId);
    //		MavenProject mavenProject = mock(MavenProject.class);
    //		when(mavenProject.getArtifact()).thenReturn(mock(Artifact.class));
    //		MavenParameters mavenParameters = new MavenParameters(new ArrayList<ArtifactRepository>(), artifactFactory, mock(ArtifactRepository.class), artifactResolver, mavenProject, mojoExecution, "0.0.1", mock(ArtifactMetadataSource.class));
    //		mojo.executeWithParameters(pluginParameters, mavenParameters);
    //		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "simple", "japicmp", executionId + ".diff")), is(false));
    //		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "simple", "japicmp", executionId + ".xml")), is(false));
    //		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "simple", "japicmp", executionId + ".html")), is(false));
  }
}
