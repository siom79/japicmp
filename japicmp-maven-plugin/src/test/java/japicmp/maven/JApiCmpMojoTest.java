package japicmp.maven;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import japicmp.maven.util.LocalMojoTest;
import org.apache.maven.plugin.testing.junit5.InjectMojo;
import org.junit.jupiter.api.Test;

/**
 * Collection of tests of JApiCmpMojo.
 */
@LocalMojoTest
final class JApiCmpMojoTest {

  JApiCmpMojoTest() {
    super();
  }

  @Test
  @InjectMojo(goal = "cmp", pom = "target/test-run/default/pom.xml")
  void testDefaultConfiguration(final JApiCmpMojo testMojo) throws Exception {
    assertNotNull(testMojo);
//    testMojo.execute();
  }

}
