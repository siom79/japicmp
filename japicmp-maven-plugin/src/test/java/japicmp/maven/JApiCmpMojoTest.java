package japicmp.maven;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import japicmp.maven.util.LocalMojoTest;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.junit5.InjectMojo;
import org.junit.jupiter.api.Test;

/**
 * Collection of tests of JApiCmpMojo.
 */
@LocalMojoTest
final class JApiCmpMojoTest extends AbstractTest {

	final Path testDefaultDir = Paths.get("target/test-run/default/target");
	final File defaultDiffFile = testDefaultDir.resolve("japicmp/japicmp.diff").toFile();
	final File defaultHhtmlFile = testDefaultDir.resolve("japicmp/japicmp.html").toFile();
	final File defaultMdFile = testDefaultDir.resolve("japicmp/japicmp.md").toFile();
	final File defaultXmlFile = testDefaultDir.resolve("japicmp/japicmp.xml").toFile();

	final Path testConfigDir = Paths.get("target/test-run/configured/target");
	final File configDiffFile = testConfigDir.resolve("reports/japicmp.diff").toFile();
	final File configHhtmlFile = testConfigDir.resolve("reports/japicmp.html").toFile();
	final File configMdFile = testConfigDir.resolve("reports/japicmp.md").toFile();
	final File configXmlFile = testConfigDir.resolve("reports/japicmp.xml").toFile();

	final Path testSkipPomDir = Paths.get("target/test-run/skippom/target");

	/**
	 * Default constructor.
	 */
	JApiCmpMojoTest() {
		super();
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/default/pom.xml")
	void testSkip(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testDefaultDir);
//    testMojo.setLog(new SystemStreamLog());
		testMojo.skip = true;
		testMojo.execute();
		assertFileNotExists(defaultDiffFile);
		assertFileNotExists(defaultHhtmlFile);
		assertFileNotExists(defaultMdFile);
		assertFileNotExists(defaultXmlFile);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/default/pom.xml")
	void testDefaultConfiguration(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testDefaultDir);
//    testMojo.setLog(new SystemStreamLog());
		testMojo.execute();
		assertFileExists(defaultDiffFile);
		assertFileExists(defaultHhtmlFile);
		assertFileExists(defaultMdFile);
		assertFileExists(defaultXmlFile);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/default/pom.xml")
	void testDefaultNoDiff(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testDefaultDir);
//    testMojo.setLog(new SystemStreamLog());
		testMojo.skipDiffReport = true;
		testMojo.execute();
		assertFileNotExists(defaultDiffFile);
		assertFileExists(defaultHhtmlFile);
		assertFileExists(defaultMdFile);
		assertFileExists(defaultXmlFile);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/default/pom.xml")
	void testDefaultNoHtml(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testDefaultDir);
//    testMojo.setLog(new SystemStreamLog());
		testMojo.skipHtmlReport = true;
		testMojo.execute();
		assertFileExists(defaultDiffFile);
		assertFileNotExists(defaultHhtmlFile);
		assertFileExists(defaultMdFile);
		assertFileExists(defaultXmlFile);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/default/pom.xml")
	void testDefaultNoMarkdown(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testDefaultDir);
//    testMojo.setLog(new SystemStreamLog());
		testMojo.skipMarkdownReport = true;
		testMojo.execute();
		assertFileExists(defaultDiffFile);
		assertFileExists(defaultHhtmlFile);
		assertFileNotExists(defaultMdFile);
		assertFileExists(defaultXmlFile);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/default/pom.xml")
	void testDefaultNoXml(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testDefaultDir);
//    testMojo.setLog(new SystemStreamLog());
		testMojo.skipXmlReport = true;
		testMojo.execute();
		assertFileExists(defaultDiffFile);
		assertFileExists(defaultHhtmlFile);
		assertFileExists(defaultMdFile);
		assertFileNotExists(defaultXmlFile);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/skippom/pom.xml")
	void testSkipPomModule(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testSkipPomDir);
//    testMojo.setLog(new SystemStreamLog());
		testMojo.execute();
		assertDirectoryEmpty(testSkipPomDir);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/noartifacts/pom.xml")
	void testNoArtifacts(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testSkipPomDir);
//		testMojo.setLog(new SystemStreamLog());

		assertThrows(MojoFailureException.class, testMojo::execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testMarkdownTitle(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
//		testMojo.setLog(new SystemStreamLog());
		testMojo.execute();
		assertFileExists(configDiffFile);
		assertFileExists(configHhtmlFile);
		assertFileExists(configMdFile);
		assertFileExists(configXmlFile);
		assertFileContains(configMdFile, "# New Markdown Title");
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBreakOnModification(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
//		testMojo.setLog(new SystemStreamLog());
		testMojo.parameter.setBreakBuildOnModifications(true);
		assertThrows(MojoFailureException.class, testMojo::execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBreakOnBinaryIncompatible(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
//		testMojo.setLog(new SystemStreamLog());
		testMojo.parameter.setBreakBuildOnBinaryIncompatibleModifications(true);
		assertThrows(MojoFailureException.class, testMojo::execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBreakOnSourceIncompatible(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
//		testMojo.setLog(new SystemStreamLog());
		testMojo.parameter.setBreakBuildOnSourceIncompatibleModifications(true);
		assertThrows(MojoFailureException.class, testMojo::execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBreakOnMissingOldVersion(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
//		testMojo.setLog(new SystemStreamLog());
		testMojo.oldVersion.getDependency().setVersion("x.y");
		assertThrows(MojoFailureException.class, testMojo::execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testIgnoreMissingOldVersion(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
//		testMojo.setLog(new SystemStreamLog());
		testMojo.oldVersion.getDependency().setVersion("x.y");
		testMojo.parameter.setIgnoreMissingOldVersion(true);
		testMojo.execute();
		assertFileExists(configDiffFile);
		assertFileExists(configHhtmlFile);
		assertFileExists(configMdFile);
		assertFileExists(configXmlFile);
		assertFileContains(configMdFile, "with the previous version `unknown`");
	}

}
