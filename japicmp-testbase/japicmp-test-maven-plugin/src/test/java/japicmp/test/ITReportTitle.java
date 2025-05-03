package japicmp.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ITReportTitle {

	@Test
	public void testReportTitle() throws IOException {
		Path htmlPath = Paths.get(System.getProperty("user.dir"), "target", "site", "project-reports.html");
		assertThat(Files.exists(htmlPath), is(true));
		Document document = Jsoup.parse(htmlPath.toFile(), "UTF-8");

		String projectVersion = System.getProperty("project.version");

		Elements overviewRow = document.select("#bodyColumn tr:has([href=\"japicmp-test-v1_japicmp-test-v2.html\"])");
		Elements link = overviewRow.select("[href=\"japicmp-test-v1_japicmp-test-v2.html\"]");
		assertThat(link.text(), is("japicmp-test-v1_japicmp-test-v2"));

		Elements description = overviewRow.select("td:eq(1)");
		assertThat(description.text(), is("Comparing source compatibility of japicmp-test-v2.jar against japicmp-test-v1-"+ projectVersion + ".jar"));
	}
}
