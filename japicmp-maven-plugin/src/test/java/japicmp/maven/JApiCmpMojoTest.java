package japicmp.maven;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import japicmp.maven.util.LocalMojoTest;
import java.io.File;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.junit5.InjectMojo;
import org.junit.jupiter.api.Test;

/**
 * Collection of tests of JApiCmpMojo.
 */
@LocalMojoTest
final class JApiCmpMojoTest extends AbstractTest {

	final File defaultDiffFile = testDefaultDir.resolve("japicmp/japicmp.diff").toFile();
	final File defaultHtmlFile = testDefaultDir.resolve("japicmp/japicmp.html").toFile();
	final File defaultMdFile = testDefaultDir.resolve("japicmp/japicmp.md").toFile();
	final File defaultXmlFile = testDefaultDir.resolve("japicmp/japicmp.xml").toFile();

	final File configDiffFile = testConfigDir.resolve("japicmp/japicmp.diff").toFile();
	final File configHtmlFile = testConfigDir.resolve("japicmp/japicmp.html").toFile();
	final File configMdFile = testConfigDir.resolve("japicmp/japicmp.md").toFile();
	final File configXmlFile = testConfigDir.resolve("japicmp/japicmp.xml").toFile();

	/**
	 * Default constructor.
	 */
	JApiCmpMojoTest() {
		super();
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/default/pom.xml")
	void testDefaultParameterValues(final JApiCmpMojo testMojo) {
		assertNotNull(testMojo);
		assertNotNull(testMojo.parameter);

		// Verify all default values are set correctly
		assertThat(testMojo.parameter.getSkipPomModules(), is(true));
		assertThat(testMojo.parameter.getIgnoreMissingOldVersion(), is(true));
		assertThat(testMojo.parameter.isIncludeSnapshots(), is(false));
		assertThat(testMojo.parameter.isBreakBuildIfCausedByExclusion(), is(true));
		assertThat(testMojo.parameter.isIncludeExclusively(), is(false));
		assertThat(testMojo.parameter.isExcludeExclusively(), is(false));
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/default/pom.xml")
	void testSkip(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testDefaultDir);
		testMojo.skip = true;
		testMojo.execute();
		assertFileNotExists(defaultDiffFile);
		assertFileNotExists(defaultHtmlFile);
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
		assertFileExists(defaultHtmlFile);
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
		assertFileExists(defaultHtmlFile);
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
		assertFileNotExists(defaultHtmlFile);
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
		assertFileExists(defaultHtmlFile);
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
		assertFileExists(defaultHtmlFile);
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
		assertThrows(MojoFailureException.class, testMojo :: execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testMarkdownTitle(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
		testMojo.execute();
		assertFileExists(configDiffFile);
		assertFileExists(configHtmlFile);
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
		assertThrows(MojoFailureException.class, testMojo :: execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBreakOnBinaryIncompatible(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
		testMojo.parameter.setBreakBuildOnBinaryIncompatibleModifications(true);
		assertThrows(MojoFailureException.class, testMojo :: execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBreakOnSourceIncompatible(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
		testMojo.parameter.setBreakBuildOnSourceIncompatibleModifications(true);
		assertThrows(MojoFailureException.class, testMojo :: execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBreakOnMissingOldVersion(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
		testMojo.oldVersion.getDependency().setVersion("x.y");
		assertThrows(MojoFailureException.class, testMojo :: execute);
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
		assertFileExists(configHtmlFile);
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
		assertThrows(MojoFailureException.class, testMojo :: execute);
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
		assertFileNotExists(configHtmlFile);
		assertFileNotExists(configMdFile);
		assertFileNotExists(configXmlFile);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testPostAnalysisFromFile(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
		testMojo.parameter.setPostAnalysisScript("target/test-classes/groovy/SamplePostAnalysis.groovy");
		testMojo.execute();
		assertFileExists(configDiffFile);
		assertFileExists(configHtmlFile);
		assertFileExists(configMdFile);
		assertFileExists(configXmlFile);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testMissingPostAnalysisScript(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
		testMojo.parameter.setPostAnalysisScript("target/test-classes/groovy/Unknown.groovy");
		assertThrows(MojoExecutionException.class, testMojo :: execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBadPostAnalysisScript(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
		testMojo.parameter.setPostAnalysisScript("target/test-classes/groovy/BadScript.groovy");
		assertThrows(MojoExecutionException.class, testMojo :: execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testNoReturnPostAnalysisScript(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		deleteDirectory(testConfigDir);
		testMojo.parameter.setPostAnalysisScript("target/test-classes/groovy/NoReturn.groovy");
		assertThrows(MojoExecutionException.class, testMojo :: execute);
	}

}
