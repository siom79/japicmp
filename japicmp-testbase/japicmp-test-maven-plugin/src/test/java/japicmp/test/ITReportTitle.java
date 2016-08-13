package japicmp.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ITReportTitle {

	@Test
	public void testReportTitle() throws IOException {
		Path htmlPath = Paths.get(System.getProperty("user.dir"), "target", "site", "project-reports.html");
		assertThat(Files.exists(htmlPath), is(true));
		Document document = Jsoup.parse(htmlPath.toFile(), Charset.forName("UTF-8").toString());

		Elements leftNav = document.select("#leftColumn [href=\"japicmp.html\"]");
		assertThat(leftNav.attr("title"), is("japicmp"));
		assertThat(leftNav.text(), is("japicmp"));

		Elements overviewRow = document.select("#bodyColumn tr:has([href=\"japicmp.html\"])");
		Elements link = overviewRow.select("[href=\"japicmp.html\"]");
		assertThat(link.text(), is("japicmp"));

		Elements description = overviewRow.select("td:eq(1)");
		assertThat(description.text(), is("Source compatibility of japicmp-test-v2-0.8.2-SNAPSHOT.jar against japicmp-test-v2-0.8.2-SNAPSHOT.jar"));
	}
}
