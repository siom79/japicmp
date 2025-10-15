package japicmp.maven;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

abstract class AbstractTest {

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
	 * Creates MavenParameters for tests.
	 *
	 * @return the mocked MavenParameters
	 */
	MavenParameters createMavenParameters() {
		final RemoteRepository remoteRepository = new RemoteRepository.Builder("id", "type",
				"http://example.org").build();
		return new MavenParameters(new ArrayList<>(), new MavenProject(),
				mock(MojoExecution.class), "", mock(RepositorySystem.class),
				mock(RepositorySystemSession.class),
				Collections.singletonList(remoteRepository));
	}

	/**
	 * Creates the PluginParameters for tests.
	 *
	 * @return the mocked PluginParameters
	 */
	PluginParameters createPluginParameters(final ConfigParameters configParameters) {
		final Version oldVersion = createVersion("groupId", "artifactId", "0.1.0");
		final Version newVersion = createVersion("groupId", "artifactId", "0.1.1");
		return new PluginParameters(false, newVersion, oldVersion, configParameters, new ArrayList<>(),
				null,
				null, false, new ArrayList<>(), new ArrayList<>(),
				new ArrayList<>(), new ArrayList<>(), new SkipReport(),
				new BreakBuild());
	}

	/**
	 * Creates a Version instance for testing.
	 *
	 * @param groupId    the group ID of the Version
	 * @param artifactId the artifact ID of the Version
	 * @param version    the version of the Version
	 *
	 * @return a new Version instance
	 */
	Version createVersion(String groupId, String artifactId, String version) {
		return new Version(createDependency(groupId, artifactId, version), null);
	}

	/**
	 * Creates a Dependency instance with the give values.
	 *
	 * @param groupId    the group ID of the Dependency
	 * @param artifactId the artifact ID of the Dependency
	 * @param version    the version of the Dependency
	 *
	 * @return a new Dependency instance
	 */
	Dependency createDependency(String groupId, String artifactId, String version) {
		final Dependency dependency = new Dependency();
		dependency.setGroupId(groupId);
		dependency.setArtifactId(artifactId);
		dependency.setVersion(version);
		return dependency;
	}


	void deleteDirectory(final Path dir) throws IOException {
		if (Files.exists(dir)) {
			if (!Files.isDirectory(dir)) {
				throw new IOException(dir + " is not a directory");
			}

			// noinspection ResultOfMethodCallIgnored
			Files.walk(dir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		}
	}

	void assertFileExists(final File file) {
		assertThat(file, anExistingFile());
	}

	void assertFileNotExists(final File file) {
		assertThat(file, not(anExistingFile()));
	}

	void assertFileContains(final File file, final String contents) throws IOException {
		final String fileContents = FileUtils.readFileToString(file, Charset.defaultCharset());
		assertThat(fileContents, containsString(contents));
	}

	void assertDirectoryEmpty(final Path dir) throws IOException {
		// A non-existent directory is considered empty
		if (Files.exists(dir)) {
			final List<Path> contents = Files.list(dir).collect(Collectors.toList());
			assertThat(contents, is(empty()));
		}
	}
}
