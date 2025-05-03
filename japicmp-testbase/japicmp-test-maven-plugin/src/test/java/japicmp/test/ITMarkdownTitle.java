package japicmp.test;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ITMarkdownTitle {

	@Test
	public void testMarkdownTitle() throws IOException {
		Path markdownPath = Paths.get(System.getProperty("user.dir"), "target", "japicmp", "single-version.md");
		assertThat(Files.exists(markdownPath), is(true));
		List<String> lines = Files.readAllLines(markdownPath, StandardCharsets.UTF_8);
		boolean found = false;
		for (String line : lines) {
			if (line.equals("# Test-Markdown-Title")) {
				found = true;
				break;
			}
		}
		assertThat(found, is(true));
	}
}
