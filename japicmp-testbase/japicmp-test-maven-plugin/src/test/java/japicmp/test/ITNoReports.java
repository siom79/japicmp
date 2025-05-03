package japicmp.test;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ITNoReports {

	@Test
	 public void testXmlReportNotGenerated() throws IOException {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "no-reports.html")), is(false));
	}

	@Test
	public void testHtmlReportNotGenerated() throws IOException {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "no-reports.xml")), is(false));
	}

	@Test
	public void testMarkdownReportNotGenerated() throws IOException {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "no-reports.md")), is(false));
	}

	@Test
	public void testDiffReportGenerated() throws IOException {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "no-reports.diff")), is(true));
	}
}
