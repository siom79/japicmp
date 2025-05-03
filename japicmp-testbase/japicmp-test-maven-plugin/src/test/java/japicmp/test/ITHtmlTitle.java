package japicmp.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ITHtmlTitle {

	@Test
	public void testHtmlTitle() throws IOException {
		Path htmlPath = Paths.get(System.getProperty("user.dir"), "target", "japicmp", "single-version.html");
		assertThat(Files.exists(htmlPath), is(true));
		Document document = Jsoup.parse(htmlPath.toFile(), StandardCharsets.UTF_8.toString());
		Elements title = document.select("title");
		assertThat(title.isEmpty(), is(false));
		assertThat(title.text(), is("Test-Title"));
		Elements span = document.select("span.title");
		assertThat(span.isEmpty(), is(false));
		assertThat(span.text(), is("Test-Title"));
	}

	@Test
	public void testHtmlTitleInSiteReport() throws IOException {
		Path htmlPath = Paths.get(System.getProperty("user.dir"), "target", "site", "japicmp-test-v1_japicmp-test-v2.html");
		assertThat(Files.exists(htmlPath), is(true));
		Document document = Jsoup.parse(htmlPath.toFile(), StandardCharsets.UTF_8.toString());
		Elements title = document.select("title");
		assertThat(title.isEmpty(), is(false));
		assertThat(title.text(), is("japicmp-test-maven-plugin – Test-Title for Site-Report"));
	}
}
