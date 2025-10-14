package japicmp.maven;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;

final class SkipModuleStrategyTest extends AbstractTest {

	@Test
	void testModuleIsExcluded() {
		final PluginParameters pluginParameters = createPluginParameters(new ConfigParameters());
		final MavenParameters mavenParameters = createMavenParameters();
		mavenParameters.mavenProject().setArtifactId("name-to-exclude");
		pluginParameters.parameter().setExcludeModules(Collections.singletonList(".*excl.*"));
		final SkipModuleStrategy skipModuleStrategy = new SkipModuleStrategy(pluginParameters,
				mavenParameters,
				mock(Log.class));
		assertThat(skipModuleStrategy.skip(), is(true));
	}

	@Test
	void testModuleIsIncluded() {
		final PluginParameters pluginParameters = createPluginParameters(new ConfigParameters());
		final MavenParameters mavenParameters = createMavenParameters();
		mavenParameters.mavenProject().setArtifactId("name-to-include");
		pluginParameters.parameter().setIncludeModules(Collections.singletonList(".*incl.*"));
		final SkipModuleStrategy skipModuleStrategy = new SkipModuleStrategy(pluginParameters,
				mavenParameters,
				mock(Log.class));
		assertFalse(skipModuleStrategy.skip());
	}

	@Test
	void testModuleIsIncludedAndExcludeDoesNotMatch() {
		final PluginParameters pluginParameters = createPluginParameters(new ConfigParameters());
		final MavenParameters mavenParameters = createMavenParameters();
		mavenParameters.mavenProject().setArtifactId("name-to-include");
		pluginParameters.parameter().setExcludeModules(Collections.singletonList(".*excl.*"));
		final SkipModuleStrategy skipModuleStrategy = new SkipModuleStrategy(pluginParameters,
				mavenParameters,
				mock(Log.class));
		assertFalse(skipModuleStrategy.skip());
	}

	@Test
	void testExcludeBeforeInclude() {
		final PluginParameters pluginParameters = createPluginParameters(new ConfigParameters());
		final MavenParameters mavenParameters = createMavenParameters();
		mavenParameters.mavenProject().setArtifactId("name-to-include");
		pluginParameters.parameter().setExcludeModules(Collections.singletonList(".*incl.*"));
		pluginParameters.parameter().setIncludeModules(Collections.singletonList(".*incl.*"));
		final SkipModuleStrategy skipModuleStrategy = new SkipModuleStrategy(pluginParameters,
				mavenParameters,
				mock(Log.class));
		assertTrue(skipModuleStrategy.skip());
	}

	@Test
	void testModuleIsIncludedAndNoIncludesAndExcludesDefined() {
		final PluginParameters pluginParameters = createPluginParameters(new ConfigParameters());
		final MavenParameters mavenParameters = createMavenParameters();
		mavenParameters.mavenProject().setArtifactId("name-to-include");
		final SkipModuleStrategy skipModuleStrategy = new SkipModuleStrategy(pluginParameters,
				mavenParameters,
				mock(Log.class));
		assertFalse(skipModuleStrategy.skip());
	}

	@Test
	void testModuleIsNotIncludedAndNoIncludesDefined() {
		final PluginParameters pluginParameters = createPluginParameters(new ConfigParameters());
		final MavenParameters mavenParameters = createMavenParameters();
		mavenParameters.mavenProject().setArtifactId("name-to-include");
		pluginParameters.parameter().setIncludeModules(Collections.singletonList(".*test.*"));
		final SkipModuleStrategy skipModuleStrategy = new SkipModuleStrategy(pluginParameters,
				mavenParameters,
				mock(Log.class));
		assertTrue(skipModuleStrategy.skip());
	}

	@Test
	void testMavenProjectNameIsNotAvailable() {
		final PluginParameters pluginParameters = createPluginParameters(new ConfigParameters());
		final MavenParameters mavenParameters = createMavenParameters();
		mavenParameters.mavenProject().setArtifactId(null);
		final SkipModuleStrategy skipModuleStrategy = new SkipModuleStrategy(pluginParameters,
				mavenParameters,
				mock(Log.class));
		assertFalse(skipModuleStrategy.skip());
	}

}
