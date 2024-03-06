package japicmp.test;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNot.not;

public class ITStylesheet {

	@Test
	public void testStylesheetIsUsed() throws IOException {
		Path htmlPath = Paths.get(System.getProperty("user.dir"), "target", "japicmp", "single-version.html");
		assertThat(Files.exists(htmlPath), is(true));
		List<String> htmlLines = Files.readAllLines(htmlPath, StandardCharsets.UTF_8);
		List<String> cssLines = Files.readAllLines(Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "css", "stylesheet.css"), StandardCharsets.UTF_8);
		assertThat(htmlLines.size(), not(is(0)));
		assertThat(cssLines.size(), not(is(0)));
		for (String cssLine : cssLines) {
			assertThat(htmlLines, hasItem(cssLine));
		}
	}
}
