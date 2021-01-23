package japicmp.test;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ITTestPostAnalysisScript {

	@Test
	public void testSkipViaUserProperty() throws IOException {
		Path path = Paths.get(System.getProperty("user.dir"), "target", "japicmp", "japicmp.diff");
		Assert.assertTrue(Files.exists(path));
		List<String> lines = Files.readAllLines(path);
		Assert.assertTrue(lines.size() > 0);
		for (String line : lines) {
			if (line.contains("japicmp.test.AbstractModifier")) {
				Assert.fail("Diff file contains class that should have been filtered by groovy script from classpath");
			}
		}
	}
}
