package japicmp.test;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ITModuleExcluded {

	@Test
	public void testXmlReportNotGenerated() {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "ignore-module.html")), is(false));
	}

	@Test
	public void testHtmlReportNotGenerated() {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "ignore-module.xml")), is(false));
	}

	@Test
	public void testDiffReportGenerated() {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "ignore-module.diff")), is(false));
	}
}
