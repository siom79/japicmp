package japicmp.maven;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Collection of tests of the {@link JApiCmpProcessor} skip logic.
 */
final class JApiCmpProcessorSkipTest extends AbstractTest {

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
  void testSkipDiffReport() {
    JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, mock(Log.class));
    assertFalse(processor.skipDiffReport());

    processor.pluginParameters.skipReport().setSkipDiffReport(true);
    assertTrue(processor.skipDiffReport());

    processor.pluginParameters.skipReport().setSkipDiffReport(false);
    configParams.setSkipDiffReport(true);
    assertTrue(processor.skipDiffReport());
  }

  @Test
  void testSkipHtmlReport() {
    JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, mock(Log.class));
    assertFalse(processor.skipHtmlReport());

    processor.pluginParameters.skipReport().setSkipHtmlReport(true);
    assertTrue(processor.skipHtmlReport());

    processor.pluginParameters.skipReport().setSkipHtmlReport(false);
    configParams.setSkipHtmlReport(true);
    assertTrue(processor.skipHtmlReport());
  }

  @Test
  void testSkipMarkdownReport() {
    JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, mock(Log.class));
    assertFalse(processor.skipMarkdownReport());

    processor.pluginParameters.skipReport().setSkipMarkdownReport(true);
    assertTrue(processor.skipMarkdownReport());

    processor.pluginParameters.skipReport().setSkipMarkdownReport(false);
    configParams.setSkipMarkdownReport(true);
    assertTrue(processor.skipMarkdownReport());
  }

  @Test
  void testSkipXmlReport() {
    JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, mock(Log.class));
    assertFalse(processor.skipMarkdownReport());

    processor.pluginParameters.skipReport().setSkipXmlReport(true);
    assertTrue(processor.skipXmlReport());

    processor.pluginParameters.skipReport().setSkipXmlReport(false);
    configParams.setSkipXmlReport(true);
    assertTrue(processor.skipXmlReport());
  }
}
