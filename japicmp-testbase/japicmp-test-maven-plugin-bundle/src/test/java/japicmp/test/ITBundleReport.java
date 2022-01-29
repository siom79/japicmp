package japicmp.test;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ITBundleReport {

	@Test
	public void testThatReportWasGenerated() {
		Path path = Paths.get(System.getProperty("user.dir"), "target", "japicmp");
		assertThat(Files.exists(path), is(true));
	}
}
