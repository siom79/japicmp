package japicmp.maven;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A collection of test of the JApiCmpProcessor class.
 */
final class JApiCmpProcessorTest extends AbstractTest {

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
  void testGetOptions() throws MojoFailureException {
    JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, mock(Log.class));

    assertThrows(MojoFailureException.class, () -> processor.getOptions());
  }
}
