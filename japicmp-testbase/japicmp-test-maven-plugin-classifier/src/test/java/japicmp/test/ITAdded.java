package japicmp.test;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class ITAdded {

	/**
	 * The japicmp plugin compares in this module the two artifacts japicmp-test-v2 with and without classifier added.
	 * The artifact with classifier added contains only the class added, hence it should be detected as unchanged. All
	 * other classes are removed from this artifact.
	 *
	 * @throws IOException if HTML file cannot be read
	 */
	@Test
	public void testStylesheetIsUsed() throws IOException {
		Path htmlPath = Paths.get(System.getProperty("user.dir"), "target", "japicmp", "japicmp.diff");
		assertThat(Files.exists(htmlPath), is(true));
		List<String> allLines = Files.readAllLines(htmlPath, Charset.forName("UTF-8"));
		assertThat(allLines.size(), not(is(0)));
		boolean foundUnchangedAdded = false;
		int unchangedCounter = 0;
		for (String line : allLines) {
			if (line.startsWith("===  UNCHANGED CLASS: PUBLIC japicmp.test.Added ")) {
				foundUnchangedAdded = true;
			}
			if (line.startsWith("===  UNCHANGED CLASS")) {
				unchangedCounter++;
			}
		}
		assertThat(foundUnchangedAdded, is(true));
		assertThat(unchangedCounter, is(1));
	}
}
