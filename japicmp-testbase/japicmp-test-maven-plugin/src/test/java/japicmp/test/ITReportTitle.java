package japicmp.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

public class ITReportTitle {

	@Test
	public void testReportTitle() throws IOException {
		Path htmlPath = Paths.get(System.getProperty("user.dir"), "target", "site", "project-reports.html");
		assertThat(Files.exists(htmlPath), is(true));
		Document document = Jsoup.parse(htmlPath.toFile(), "UTF-8");

		Elements overviewRow = document.select("#bodyColumn tr:has([href=\"com.github.siom79.japicmp_japicmp-test-v1_0.13.2-SNAPSHOTcom.github.siom79.japicmp_japicmp-test-v2_0.13.2-SNAPSHOT_output.html\"])");
		Elements link = overviewRow.select("[href=\"com.github.siom79.japicmp_japicmp-test-v1_0.13.2-SNAPSHOTcom.github.siom79.japicmp_japicmp-test-v2_0.13.2-SNAPSHOT_output.html\"]");
		assertThat(link.text(), is("com.github.siom79.japicmp_japicmp-test-v1_0.13.2-SNAPSHOTcom.github.siom79.japicmp_japicmp-test-v2_0.13.2-SNAPSHOT"));

		Elements description = overviewRow.select("td:eq(1)");
		String projectVersion = System.getProperty("project.version");
		assertThat(description.text(), is("Comparing source compatibility of japicmp-test-v2-"+projectVersion
		    + ".jar against japicmp-test-v1-"+ projectVersion + ".jar"));
	}
}
