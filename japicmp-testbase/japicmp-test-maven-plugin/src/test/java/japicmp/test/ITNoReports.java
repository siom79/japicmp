package japicmp.test;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ITNoReports {

	@Test
	 public void testXmlReportNotGenerated() {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "no-reports.html")), is(false));
	}

	@Test
	public void testHtmlReportNotGenerated() {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "no-reports.xml")), is(false));
	}

	@Test
	public void testDiffReportGenerated() {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "no-reports.diff")), is(true));
	}
}
