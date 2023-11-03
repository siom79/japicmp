package japicmp.test;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ITSkip {

	@Test
	 public void testXmlReportNotGenerated() throws IOException {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "skip.html")), is(false));
	}

	@Test
	public void testHtmlReportNotGenerated() throws IOException {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "skip.xml")), is(false));
	}

	@Test
	public void testDiffReportGenerated() throws IOException {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "skip.diff")), is(false));
	}
}
