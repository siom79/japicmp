package japicmp.test;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ITMultipleExecutions {

	@Test
	public void testThatXmlAndHtmlFilesAreWritten() {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "multiple-versions.html")), is(true));
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "multiple-versions.xml")), is(true));
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "multiple-versions.md")), is(true));
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "multiple-versions.diff")), is(true));
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "single-version.html")), is(true));
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "single-version.xml")), is(true));
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "single-version.md")), is(true));
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "single-version.diff")), is(true));
	}
}
