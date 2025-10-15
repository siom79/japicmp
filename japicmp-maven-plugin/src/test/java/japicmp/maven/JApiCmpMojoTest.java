package japicmp.maven;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import japicmp.maven.util.LocalMojoTest;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.junit5.InjectMojo;
import org.junit.jupiter.api.Test;

/**
 * Collection of tests of JApiCmpMojo.
 */
@LocalMojoTest
final class JApiCmpMojoTest extends AbstractTest {

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
		testMojo.execute();
		assertDirectoryEmpty(testSkipPomDir);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/noartifacts/pom.xml")
	void testNoArtifacts(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testSkipPomDir);
		assertThrows(MojoFailureException.class, testMojo::execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testMarkdownTitle(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
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
		testMojo.parameter.setBreakBuildOnModifications(true);
		assertThrows(MojoFailureException.class, testMojo::execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBreakOnBinaryIncompatible(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
		testMojo.parameter.setBreakBuildOnBinaryIncompatibleModifications(true);
		assertThrows(MojoFailureException.class, testMojo::execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBreakOnSourceIncompatible(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
		testMojo.parameter.setBreakBuildOnSourceIncompatibleModifications(true);
		assertThrows(MojoFailureException.class, testMojo::execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBreakOnMissingOldVersion(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
		testMojo.oldVersion.getDependency().setVersion("x.y");
		assertThrows(MojoFailureException.class, testMojo::execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testIgnoreMissingOldVersion(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
		testMojo.oldVersion.getDependency().setVersion("x.y");
		testMojo.parameter.setIgnoreMissingOldVersion(true);
		testMojo.execute();
		assertFileExists(configDiffFile);
		assertFileExists(configHhtmlFile);
		assertFileExists(configMdFile);
		assertFileExists(configXmlFile);
		assertFileContains(configMdFile, "with the previous version `unknown`");
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBreakOnMissingNewVersion(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
		testMojo.mavenProject.setVersion("x.y");
		assertThrows(MojoFailureException.class, testMojo::execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testIgnoreMissingNewVersion(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
		testMojo.mavenProject.setVersion("x.y");
		testMojo.parameter.setIgnoreMissingNewVersion(true);
		testMojo.execute();
		assertFileNotExists(configDiffFile);
		assertFileNotExists(configHhtmlFile);
		assertFileNotExists(configMdFile);
		assertFileNotExists(configXmlFile);
	}

}
