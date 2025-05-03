package japicmp.test;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ITModuleExcluded {

	@Test
	public void testXmlReportNotGenerated() throws IOException {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "ignore-module.html")), is(false));
	}

	@Test
	public void testHtmlReportNotGenerated() throws IOException {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "ignore-module.xml")), is(false));
	}

	@Test
	public void testMarkdownReportGenerated() throws IOException {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "ignore-module.md")), is(false));
	}

	@Test
	public void testDiffReportGenerated() throws IOException {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp", "ignore-module.diff")), is(false));
	}
}
