package japicmp.maven;

import japicmp.util.Optional;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class SkipModuleStrategyTest {

	@Test
	public void testModuleIsExcluded() {
		PluginParameters pluginParameters = createPluginParameters();
		MavenParameters mavenParameters = createMavenParameters();
		mavenParameters.getMavenProject().setArtifactId("name-to-exclude");
		pluginParameters.getParameterParam().setExcludeModules(Collections.singletonList(".*excl.*"));
		SkipModuleStrategy skipModuleStrategy = new SkipModuleStrategy(pluginParameters, mavenParameters, mock(Log.class));
		assertThat(skipModuleStrategy.skip(), is(true));
	}

	@Test
	public void testModuleIsIncluded() {
		PluginParameters pluginParameters = createPluginParameters();
		MavenParameters mavenParameters = createMavenParameters();
		mavenParameters.getMavenProject().setArtifactId("name-to-include");
		pluginParameters.getParameterParam().setIncludeModules(Collections.singletonList(".*incl.*"));
		SkipModuleStrategy skipModuleStrategy = new SkipModuleStrategy(pluginParameters, mavenParameters, mock(Log.class));
		assertThat(skipModuleStrategy.skip(), is(false));
	}

	@Test
	public void testModuleIsIncludedAndExcludeDoesNotMatch() {
		PluginParameters pluginParameters = createPluginParameters();
		MavenParameters mavenParameters = createMavenParameters();
		mavenParameters.getMavenProject().setArtifactId("name-to-include");
		pluginParameters.getParameterParam().setExcludeModules(Collections.singletonList(".*excl.*"));
		SkipModuleStrategy skipModuleStrategy = new SkipModuleStrategy(pluginParameters, mavenParameters, mock(Log.class));
		assertThat(skipModuleStrategy.skip(), is(false));
	}

	@Test
	public void testExcludeBeforeInclude() {
		PluginParameters pluginParameters = createPluginParameters();
		MavenParameters mavenParameters = createMavenParameters();
		mavenParameters.getMavenProject().setArtifactId("name-to-include");
		pluginParameters.getParameterParam().setExcludeModules(Collections.singletonList(".*incl.*"));
		pluginParameters.getParameterParam().setIncludeModules(Collections.singletonList(".*incl.*"));
		SkipModuleStrategy skipModuleStrategy = new SkipModuleStrategy(pluginParameters, mavenParameters, mock(Log.class));
		assertThat(skipModuleStrategy.skip(), is(true));
	}

	@Test
	public void testModuleIsIncludedAndNoIncludesAndExcludesDefined() {
		PluginParameters pluginParameters = createPluginParameters();
		MavenParameters mavenParameters = createMavenParameters();
		mavenParameters.getMavenProject().setArtifactId("name-to-include");
		SkipModuleStrategy skipModuleStrategy = new SkipModuleStrategy(pluginParameters, mavenParameters, mock(Log.class));
		assertThat(skipModuleStrategy.skip(), is(false));
	}

	@Test
	public void testModuleIsNotIncludedAndNoIncludesDefined() {
		PluginParameters pluginParameters = createPluginParameters();
		MavenParameters mavenParameters = createMavenParameters();
		mavenParameters.getMavenProject().setArtifactId("name-to-include");
		pluginParameters.getParameterParam().setIncludeModules(Collections.singletonList(".*test.*"));
		SkipModuleStrategy skipModuleStrategy = new SkipModuleStrategy(pluginParameters, mavenParameters, mock(Log.class));
		assertThat(skipModuleStrategy.skip(), is(true));
	}

	@Test
	public void testMavenProjectNameIsNotAvailable() {
		PluginParameters pluginParameters = createPluginParameters();
		MavenParameters mavenParameters = createMavenParameters();
		mavenParameters.getMavenProject().setArtifactId(null);
		SkipModuleStrategy skipModuleStrategy = new SkipModuleStrategy(pluginParameters, mavenParameters, mock(Log.class));
		assertThat(skipModuleStrategy.skip(), is(false));
	}

	private MavenParameters createMavenParameters() {
		return new MavenParameters(new ArrayList<ArtifactRepository>(), mock(ArtifactFactory.class), mock(ArtifactRepository.class),
			mock(ArtifactResolver.class), new MavenProject(), mock(MojoExecution.class), "", mock(ArtifactMetadataSource.class));
	}

	private PluginParameters createPluginParameters() {
		Version oldVersion = JApiCmpMojoTest.createVersion("groupId", "artifactId", "0.1.0");
		Version newVersion = JApiCmpMojoTest.createVersion("groupId", "artifactId", "0.1.1");
		Parameter parameter = new Parameter();
		return new PluginParameters("false", newVersion, oldVersion, parameter, new ArrayList<Dependency>(),
			Optional.<File>absent(), Optional.<String>absent(), false, new ArrayList<DependencyDescriptor>(),
			new ArrayList<DependencyDescriptor>(), new ArrayList<Dependency>(), new ArrayList<Dependency>());
	}
}
