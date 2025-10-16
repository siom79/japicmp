package japicmp.maven;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import japicmp.maven.util.LocalMojoTest;
import java.io.File;
import org.apache.maven.plugin.testing.junit5.InjectMojo;
import org.junit.jupiter.api.Test;

/**
 * Collection of tests of JApiCmpReport.
 */
@LocalMojoTest
final class JApiCmpReportTest extends AbstractTest {

	final File defaultDiffFile = testDefaultDir.resolve("site/japicmp.diff").toFile();
	final File defaultHtmlFile = testDefaultDir.resolve("site/japicmp.html").toFile();
	final File defaultMdFile = testDefaultDir.resolve("site/japicmp.md").toFile();
	final File defaultXmlFile = testDefaultDir.resolve("site/japicmp.xml").toFile();

	final File configDiffFile = testConfigDir.resolve("site/japicmp.diff").toFile();
	final File configHtmlFile = testConfigDir.resolve("site/japicmp.html").toFile();
	final File configMdFile = testConfigDir.resolve("site/japicmp.md").toFile();
	final File configXmlFile = testConfigDir.resolve("site/japicmp.xml").toFile();

	@Test
	@InjectMojo(goal = "cmp-report", pom = "target/test-run/default/pom.xml")
	void testSkip(final JApiCmpReport testReport) throws Exception {
		assertNotNull(testReport);
		deleteDirectory(testDefaultDir);
		testReport.skip = true;
		testReport.execute();
		assertFileNotExists(defaultDiffFile);
		assertFileExists(defaultHtmlFile);    // HTML file always created by Report
		assertFileNotExists(defaultMdFile);
		assertFileNotExists(defaultXmlFile);
	}

	@Test
	@InjectMojo(goal = "cmp-report", pom = "target/test-run/default/pom.xml")
	void testDefaultConfiguration(final JApiCmpReport testReport) throws Exception {
		assertNotNull(testReport);
		deleteDirectory(testDefaultDir);
		testReport.execute();
		assertFileExists(defaultDiffFile);
		assertFileExists(defaultHtmlFile);
		assertFileExists(defaultMdFile);
		assertFileExists(defaultXmlFile);
	}

	@Test
	@InjectMojo(goal = "cmp-report", pom = "target/test-run/default/pom.xml")
	void testDefaultNoDiff(final JApiCmpReport testReport) throws Exception {
		assertNotNull(testReport);
		deleteDirectory(testDefaultDir);
		testReport.skipDiffReport = true;
		testReport.execute();
		assertFileNotExists(defaultDiffFile);
		assertFileExists(defaultHtmlFile);
		assertFileExists(defaultMdFile);
		assertFileExists(defaultXmlFile);
	}

	@Test
	@InjectMojo(goal = "cmp-report", pom = "target/test-run/default/pom.xml")
	void testDefaultNoMarkdown(final JApiCmpReport testReport) throws Exception {
		assertNotNull(testReport);
		deleteDirectory(testDefaultDir);
		testReport.skipMarkdownReport = true;
		testReport.execute();
		assertFileExists(defaultDiffFile);
		assertFileExists(defaultHtmlFile);
		assertFileNotExists(defaultMdFile);
		assertFileExists(defaultXmlFile);
	}

	@Test
	@InjectMojo(goal = "cmp-report", pom = "target/test-run/default/pom.xml")
	void testDefaultNoXml(final JApiCmpReport testReport) throws Exception {
		assertNotNull(testReport);
		deleteDirectory(testDefaultDir);
		testReport.skipXmlReport = true;
		testReport.execute();
		assertFileExists(defaultDiffFile);
		assertFileExists(defaultHtmlFile);
		assertFileExists(defaultMdFile);
		assertFileNotExists(defaultXmlFile);
	}

	@Test
	@InjectMojo(goal = "cmp-report", pom = "target/test-run/configured/pom.xml")
	void testMarkdownTitle(final JApiCmpReport testReport) throws Exception {
		assertNotNull(testReport);
		deleteDirectory(testConfigDir);
		testReport.execute();
		assertFileExists(configDiffFile);
		assertFileExists(configHtmlFile);
		assertFileExists(configMdFile);
		assertFileExists(configXmlFile);
		assertFileContains(configMdFile, "# New Markdown Title");
		assertFileContains(configHtmlFile, "<span class=\"title\">New HTML Title</span>");
	}


}
