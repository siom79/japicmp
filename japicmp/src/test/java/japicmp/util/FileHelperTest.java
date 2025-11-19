package japicmp.util;

import static org.hamcrest.CoreMatchers.is;

import java.io.File;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import japicmp.cmp.JApiCmpArchive;
import japicmp.versioning.SemanticVersion;

class FileHelperTest {

	@Test
	void testGuessVersionFromJarFile() {
		String guessVersion = FileHelper.guessVersion(new File("project-11.22.33.jar"));
		MatcherAssert.assertThat(guessVersion, is("11.22.33"));
	}

	@Test
	void testGuessVersionFromSnapshotJarFile() {
		String guessVersion = FileHelper.guessVersion(new File("project-11.22.33-SNAPSHOT.jar"));
		MatcherAssert.assertThat(guessVersion, is("11.22.33-SNAPSHOT"));
	}

	@Test
	void testCreateFileList() {
		List<JApiCmpArchive> archives = FileHelper
				.createFileList("projecta-11.22.33-SNAPSHOT.jar;projectb-44.55.66.jar;projectd.jar");
		MatcherAssert.assertThat(archives.size(), is(3));

		MatcherAssert.assertThat(archives.get(0).getFile(), is(new File("projecta-11.22.33-SNAPSHOT.jar")));
		MatcherAssert.assertThat(archives.get(0).getVersion().getSemanticVersion().get(),
				is(new SemanticVersion(11, 22, 33)));
		MatcherAssert.assertThat(archives.get(0).getVersion().getStringVersion(), is("11.22.33-SNAPSHOT"));

		MatcherAssert.assertThat(archives.get(1).getFile(), is(new File("projectb-44.55.66.jar")));
		MatcherAssert.assertThat(archives.get(1).getVersion().getSemanticVersion().get(),
				is(new SemanticVersion(44, 55, 66)));
		MatcherAssert.assertThat(archives.get(1).getVersion().getStringVersion(), is("44.55.66"));

		MatcherAssert.assertThat(archives.get(2).getFile(), is(new File("projectd.jar")));
		MatcherAssert.assertThat(archives.get(2).getVersion().getSemanticVersion().isPresent(), is(false));
		MatcherAssert.assertThat(archives.get(2).getVersion().getStringVersion(), is("n.a."));
	}
}
