package japicmp.test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;

public class ITNoReport {

	@Test
	public void testThatNoReportWasGenerated() {
		Path path = Paths.get(System.getProperty("user.dir"), "target", "japicmp");
		assertThat(Files.exists(path), is(false));
	}
}
