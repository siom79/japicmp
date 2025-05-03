package japicmp.test;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ITTestPostAnalysisScript {

	@Test
	public void testSkipViaUserProperty() throws IOException {
		Path path = Paths.get(System.getProperty("user.dir"), "target", "japicmp", "japicmp.diff");
		assertTrue(Files.exists(path));
		List<String> lines = Files.readAllLines(path);
		assertTrue(lines.size() > 0);
		for (String line : lines) {
			if (line.contains("japicmp.test.AbstractModifier")) {
				fail("Diff file contains class that should have been filtered by groovy script from classpath");
			}
		}
	}
}
