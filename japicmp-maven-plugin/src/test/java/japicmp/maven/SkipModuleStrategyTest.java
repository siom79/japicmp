package japicmp.maven;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

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
		RemoteRepository remoteRepository = new RemoteRepository.Builder("id", "type", "http://example.org").build();
		return new MavenParameters(new ArrayList<ArtifactRepository>(), 
			new MavenProject(), mock(MojoExecution.class), "", mock(RepositorySystem.class), mock(
				RepositorySystemSession.class), Collections.singletonList(remoteRepository));
	}

	private PluginParameters createPluginParameters() {
		Version oldVersion = JApiCmpMojoTest.createVersion("groupId", "artifactId", "0.1.0");
		Version newVersion = JApiCmpMojoTest.createVersion("groupId", "artifactId", "0.1.1");
		Parameter parameter = new Parameter();
		return new PluginParameters(false, newVersion, oldVersion, parameter, new ArrayList<Dependency>(),
			Optional.<File>empty(), Optional.<String>empty(), false, new ArrayList<DependencyDescriptor>(),
			new ArrayList<DependencyDescriptor>(), new ArrayList<Dependency>(), new ArrayList<Dependency>());
	}
}
