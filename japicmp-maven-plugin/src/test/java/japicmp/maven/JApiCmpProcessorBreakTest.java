package japicmp.maven;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Collection of tests of the {@link JApiCmpProcessor} breaking logic.
 */
final class JApiCmpProcessorBreakTest extends BaseTest {

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
  void testBreakOnSemanticVersioning() {
    JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, mock(Log.class));
    assertFalse(processor.breakBuildBasedOnSemanticVersioning(configParams));

    processor.pluginParameters.breakBuild().setOnSemanticVersioning(true);
    assertTrue(processor.breakBuildBasedOnSemanticVersioning(configParams));

    processor.pluginParameters.breakBuild().setOnSemanticVersioning(false);
    configParams.setBreakBuildBasedOnSemanticVersioning(true);
    assertTrue(processor.breakBuildBasedOnSemanticVersioning(configParams));
  }

  @Test
  void testBreakOnSemanticVersioningForMajorVersionZero() {
    JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, mock(Log.class));
    assertFalse(processor.breakBuildBasedOnSemanticVersioningForMajorVersionZero(configParams));

    processor.pluginParameters.breakBuild().setOnSemanticVersioningForMajorVersionZero(true);
    assertTrue(processor.breakBuildBasedOnSemanticVersioningForMajorVersionZero(configParams));

    processor.pluginParameters.breakBuild().setOnSemanticVersioningForMajorVersionZero(false);
    configParams.setBreakBuildBasedOnSemanticVersioningForMajorVersionZero(true);
    assertTrue(processor.breakBuildBasedOnSemanticVersioningForMajorVersionZero(configParams));
  }

  @Test
  void testBreakOnModifications() {
    JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, mock(Log.class));
    assertFalse(processor.breakBuildOnModifications(configParams));

    processor.pluginParameters.breakBuild().setOnModifications(true);
    assertTrue(processor.breakBuildOnModifications(configParams));

    processor.pluginParameters.breakBuild().setOnModifications(false);
    configParams.setBreakBuildOnModifications(true);
    assertTrue(processor.breakBuildOnModifications(configParams));
  }

  @Test
  void testBreakOnBinaryIncompatibleModifications() {
    JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, mock(Log.class));
    assertFalse(processor.breakBuildOnBinaryIncompatibleModifications(configParams));

    processor.pluginParameters.breakBuild().setOnBinaryIncompatibleModifications(true);
    assertTrue(processor.breakBuildOnBinaryIncompatibleModifications(configParams));

    processor.pluginParameters.breakBuild().setOnBinaryIncompatibleModifications(false);
    configParams.setBreakBuildOnBinaryIncompatibleModifications(true);
    assertTrue(processor.breakBuildOnBinaryIncompatibleModifications(configParams));
  }

  @Test
  void testBreakOnSourceIncompatibleModifications() {
    JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, mock(Log.class));
    assertFalse(processor.breakBuildOnSourceIncompatibleModifications(configParams));

    processor.pluginParameters.breakBuild().setOnSourceIncompatibleModifications(true);
    assertTrue(processor.breakBuildOnSourceIncompatibleModifications(configParams));

    processor.pluginParameters.breakBuild().setOnSourceIncompatibleModifications(false);
    configParams.setBreakBuildOnSourceIncompatibleModifications(true);
    assertTrue(processor.breakBuildOnSourceIncompatibleModifications(configParams));
  }
}
