package japicmp.maven;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import japicmp.exception.JApiCmpException;
import japicmp.maven.util.LocalMojoTest;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.junit5.InjectMojo;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

	final File multipleDiffFile = testMultipleDir.resolve("japicmp/japicmp.diff").toFile();
	final File multipleHtmlFile = testMultipleDir.resolve("japicmp/japicmp.html").toFile();
	final File multipleMdFile = testMultipleDir.resolve("japicmp/japicmp.md").toFile();
	final File multipleXmlFile = testMultipleDir.resolve("japicmp/japicmp.xml").toFile();

	final File overrideDiffFile = testOverrideDir.resolve("japicmp/japicmp.diff").toFile();
	final File overrideHtmlFile = testOverrideDir.resolve("japicmp/japicmp.html").toFile();
	final File overrideMdFile = testOverrideDir.resolve("japicmp/japicmp.md").toFile();
	final File overrideXmlFile = testOverrideDir.resolve("japicmp/japicmp.xml").toFile();

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
	void testBadOutputDirectory(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		testMojo.outputDirectory = new File("pom.xml");
		deleteDirectory(testDefaultDir);
		assertThrows(MojoFailureException.class, testMojo::execute);
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
		testMojo.parameter.setIncludeSnapshots(true);
		testMojo.skipDiffReport = true;
		deleteDirectory(testDefaultDir);
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
	@InjectMojo(goal = "cmp", pom = "target/test-run/default/pom.xml")
	void testOldVersionPattern(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		testMojo.parameter.setOldVersionPattern("^\\d+[.]\\d+[.]0$");
		deleteDirectory(testDefaultDir);
		testMojo.execute();
		assertFileExists(defaultDiffFile);
		assertFileExists(defaultHtmlFile);
		assertFileExists(defaultMdFile);
		assertFileExists(defaultXmlFile);
		assertFileContainsPattern(defaultMdFile, "^.+with the previous version.+\\d+[.]\\d+[.]0.+$");
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/default/pom.xml")
	void testBadVersionPattern(final JApiCmpMojo testMojo) {
		assertNotNull(testMojo);
		testMojo.parameter.setOldVersionPattern("^\\d+[.][\\d+[.]0$");
		assertThrows(MojoFailureException.class, testMojo::execute);
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
	void testNoArtifacts(final JApiCmpMojo testMojo) {
		assertNotNull(testMojo);
		assertThrows(MojoFailureException.class, testMojo::execute);
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
	void testSetUpClassPath(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);

		// The plugin testing harness doesn't populate dependencies, so we have to spoof it.
		final DefaultArtifactHandler handler = new DefaultArtifactHandler("test");
		handler.setAddedToClasspath(true);
		Artifact slfApi =
			new DefaultArtifact("org.slf4j", "slf4j-api", "2.0.17", "system", "jar", "", handler);
		Artifact slfSimple =
			new DefaultArtifact("org.slf4j", "slf4j-simple", "2.0.17", "provided", "jar", "", handler);
		Artifact logback =
			new DefaultArtifact("ch.qos.logback", "logback-classic", "1.5.10", "compile", "jar", "", handler);
		testMojo.mavenProject.getArtifacts().add(slfApi);
		testMojo.mavenProject.getArtifacts().add(slfSimple);
		testMojo.mavenProject.getArtifacts().add(logback);

		deleteDirectory(testConfigDir);
		testMojo.execute();
		// Just confirm a report was generated
		assertFileExists(configDiffFile);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testIgnoreMissingDependency(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);

		// The plugin testing harness doesn't populate dependencies, so we have to spoof it.
		final DefaultArtifactHandler handler = new DefaultArtifactHandler("test");
		handler.setAddedToClasspath(true);
		Artifact slfApi =
			new DefaultArtifact("org.slf4j", "slf4j-api", "2.0.17", "system", "jar", "", handler);
		testMojo.mavenProject.getArtifacts().add(slfApi);
		testMojo.parameter.setIgnoreMissingOptionalDependency(true);
		deleteDirectory(testConfigDir);
		testMojo.execute();
		// Just confirm a report was generated
		assertFileExists(configDiffFile);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBreakOnModification(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		testMojo.parameter.setBreakBuildOnModifications(true);
		deleteDirectory(testConfigDir);
		assertThrows(MojoFailureException.class, testMojo::execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBreakOnBinaryIncompatible(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		testMojo.parameter.setBreakBuildOnBinaryIncompatibleModifications(true);
		deleteDirectory(testConfigDir);
		assertThrows(MojoFailureException.class, testMojo::execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBreakOnSourceIncompatible(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		testMojo.parameter.setBreakBuildOnSourceIncompatibleModifications(true);
		deleteDirectory(testConfigDir);
		assertThrows(MojoFailureException.class, testMojo::execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/default/pom.xml")
	void testMissingOldVersion(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		testMojo.parameter.setOldVersionPattern("^\\d+[.]\\d+[.]9$");
		testMojo.parameter.setIgnoreMissingOldVersion(false);
		deleteDirectory(testDefaultDir);
		assertThrows(MojoFailureException.class, testMojo::execute);
		assertDirectoryEmpty(testSkipPomDir);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testIgnoreMissingOldVersion(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		testMojo.oldVersion.getDependency().setVersion("x.y");
		testMojo.parameter.setIgnoreMissingOldVersion(true);
		deleteDirectory(testConfigDir);
		testMojo.execute();
		assertFileExists(configDiffFile);
		assertFileExists(configHtmlFile);
		assertFileExists(configMdFile);
		assertFileExists(configXmlFile);
		assertFileContains(configMdFile, "with the previous version `unknown`");
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBreakOnMissingOldVersion(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		testMojo.oldVersion.getDependency().setVersion("x.y");
		deleteDirectory(testConfigDir);
		assertThrows(MojoFailureException.class, testMojo::execute);
		assertDirectoryEmpty(testConfigDir);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBreakOnMissingNewVersion(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		testMojo.mavenProject.setVersion("x.y");
		deleteDirectory(testConfigDir);
		assertThrows(MojoFailureException.class, testMojo::execute);
		assertDirectoryEmpty(testConfigDir);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testIgnoreMissingNewVersion(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		testMojo.mavenProject.setVersion("x.y");
		testMojo.parameter.setIgnoreMissingNewVersion(true);
		deleteDirectory(testConfigDir);
		testMojo.execute();
		assertDirectoryEmpty(testConfigDir);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testPostAnalysisFromFile(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		testMojo.parameter.setPostAnalysisScript("target/test-classes/groovy/SamplePostAnalysis.groovy");
		deleteDirectory(testConfigDir);
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
		testMojo.parameter.setPostAnalysisScript("target/test-classes/groovy/Unknown.groovy");
		deleteDirectory(testConfigDir);
		assertThrows(MojoExecutionException.class, testMojo::execute);
		assertDirectoryEmpty(testConfigDir);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBadPostAnalysisScript(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		testMojo.parameter.setPostAnalysisScript("target/test-classes/groovy/BadScript.groovy");
		deleteDirectory(testConfigDir);
		assertThrows(MojoExecutionException.class, testMojo::execute);
		assertDirectoryEmpty(testConfigDir);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testNoReturnPostAnalysisScript(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		testMojo.parameter.setPostAnalysisScript("target/test-classes/groovy/NoReturn.groovy");
		deleteDirectory(testConfigDir);
		assertThrows(MojoExecutionException.class, testMojo::execute);
		assertDirectoryEmpty(testConfigDir);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testExclusion(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		testMojo.parameter.setExcludes(Collections.singletonList("japicmp.cli.CliParser"));
		deleteDirectory(testConfigDir);
		testMojo.execute();
		assertFileExists(configXmlFile);
		assertFileContainsPattern(configXmlFile, "^.*packagesExclude=\"japicmp[.]cli[.]CliParser.*$");
		assertFileNotContainsPattern(configXmlFile, "^.*fullyQualifiedName=\"japicmp[.]cli[.]CliParser.*$");
		assertFileContainsPattern(configXmlFile, "^.*japicmp[.]cli.*$");
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testInclusion(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		testMojo.parameter.setIncludes(Collections.singletonList("japicmp.model"));
		deleteDirectory(testConfigDir);
		testMojo.execute();
		assertFileExists(configXmlFile);
		assertFileContainsPattern(configXmlFile, "^.*packagesInclude=\"japicmp[.]model.*$");
		assertFileNotContainsPattern(configXmlFile, "^.*japicmp[.]cli.*$");
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testAccessModifier(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		testMojo.parameter.setAccessModifier("protected");
		deleteDirectory(testConfigDir);
		testMojo.execute();
		assertFileExists(configXmlFile);
		assertFileContainsPattern(configXmlFile, "^.*<modifier.*oldValue=\"PROTECTED.*$");
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/configured/pom.xml")
	void testBadAccessModifier(final JApiCmpMojo testMojo) throws Exception {
		assertNotNull(testMojo);
		testMojo.parameter.setAccessModifier("BAD_ACCESS");
		deleteDirectory(testConfigDir);
		assertThrows(MojoFailureException.class, testMojo::execute);
		assertDirectoryEmpty(testConfigDir);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/override/pom.xml")
	void testOverrideCompatibilityChangeParameter(final JApiCmpMojo testMojo)
		throws IOException, MojoExecutionException, MojoFailureException {
		assertNotNull(testMojo);
		deleteDirectory(testOverrideDir);
		testMojo.execute();
		assertFileNotExists(overrideDiffFile);
		assertFileNotExists(overrideHtmlFile);
		assertFileNotExists(overrideMdFile);
		assertFileExists(overrideXmlFile);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/override/pom.xml")
	void testOverrideCompatibilityChangeBadParameter(final JApiCmpMojo testMojo)
		throws IOException {
		assertNotNull(testMojo);
		testMojo.parameter.getOverrideCompatibilityChangeParameters().get(0).setCompatibilityChange("BAD_VALUE");
		deleteDirectory(testOverrideDir);
		assertThrows(MojoFailureException.class, testMojo::execute);
		assertDirectoryEmpty(testOverrideDir);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/override/pom.xml")
	void testSystemPathWithPropertyInDependency(final JApiCmpMojo testMojo)
		throws IOException, MojoExecutionException, MojoFailureException {
		assertNotNull(testMojo);
		Dependency newDependency = new Dependency();
		newDependency.setGroupId("com.github.siom79.japicmp");
		newDependency.setArtifactId("japicmp");
		newDependency.setVersion("0.24.0");
		newDependency.setType("jar");
		newDependency.setScope("system");
		newDependency.setSystemPath("${projDir}/target/${testDir}/japicmp-0.24.0.jar");
		testMojo.newVersion = new Version(newDependency, null);
		deleteDirectory(testOverrideDir);
		testMojo.execute();
		assertFileExists(overrideXmlFile);
		assertFileContainsPattern(overrideXmlFile,
			"^.*newJar=\".+japicmp-maven-plugin[\\\\/]target[\\\\/]test-run[\\\\/]override[\\\\/]japicmp-0.24.0.jar\".+$");
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/override/pom.xml")
	void testOverrideCompatibilityChangeBadSemanticVersionLevel(final JApiCmpMojo testMojo)
		throws IOException, MojoExecutionException, MojoFailureException {
		assertNotNull(testMojo);
		testMojo.parameter.getOverrideCompatibilityChangeParameters().get(0).setSemanticVersionLevel(null);
		// Bad semanticVersionLevel doesn't matter; defaults to sematicVersionLevel defined by the change type
		deleteDirectory(testOverrideDir);
		testMojo.execute();
		assertFileExists(overrideXmlFile);
		assertFileContainsPattern(overrideXmlFile,
			"^.*newJar=\".+japicmp-maven-plugin[\\\\/]target[\\\\/]test-run[\\\\/]override[\\\\/]japicmp-0.24.0.jar\".+$");
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/override/pom.xml")
	void testConfiguredFileMissing(final JApiCmpMojo testMojo)
		throws IOException, MojoExecutionException, MojoFailureException {
		assertNotNull(testMojo);
		final ConfigurationFile newConfig = new ConfigurationFile();
		newConfig.setPath("target/test-run/override/japicmp-0.0.0.jar");
		testMojo.oldVersion = new Version(null, newConfig);
		deleteDirectory(testOverrideDir);
		assertThrows(JApiCmpException.class, testMojo::execute);
		testMojo.parameter.setIgnoreMissingOldVersion(false);
		assertThrows(MojoFailureException.class, testMojo::execute);
	}

	@Test
	@InjectMojo(goal = "cmp", pom = "target/test-run/multiple/pom.xml")
	void testMultipleArtifacts(final JApiCmpMojo testMojo)
		throws IOException, MojoExecutionException, MojoFailureException {
		assertNotNull(testMojo);
		deleteDirectory(testMultipleDir);
		testMojo.execute();
		assertFileExists(multipleXmlFile);
		assertFileExists(multipleHtmlFile);

		assertFileContainsPattern(multipleXmlFile,
			"^.*newJar=\".*japicmp[\\/\\\\]japicmp-ant-task[\\/\\\\]0[.]24[.]0[\\/\\\\]japicmp-ant-task-0[.]24[.]0[.]jar;"
				+ ".*japicmp[\\/\\\\]japicmp-maven-plugin[\\/\\\\]0[.]24[.]0[\\/\\\\]japicmp-maven-plugin-0[.]24[.]0[.]jar\".*$");
		assertFileContainsPattern(multipleXmlFile,
			"^.*oldJar=\".*japicmp[\\/\\\\]japicmp-ant-task[\\/\\\\]0[.]20[.]0[\\/\\\\]japicmp-ant-task-0[.]20[.]0[.]jar;"
				+ ".*japicmp[\\/\\\\]japicmp-maven-plugin[\\/\\\\]0[.]20[.]0[\\/\\\\]japicmp-maven-plugin-0[.]20[.]0[.]jar\".*$");
	}
}
