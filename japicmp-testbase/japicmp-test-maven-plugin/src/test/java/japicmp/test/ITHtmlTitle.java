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

public class ITHtmlTitle {

	@Test
	public void testHtmlTitle() throws IOException {
		Path htmlPath = Paths.get(System.getProperty("user.dir"), "target", "japicmp", "single-version.html");
		assertThat(Files.exists(htmlPath), is(true));
		Document document = Jsoup.parse(htmlPath.toFile(), Charset.forName("UTF-8").toString());
		Elements title = document.select("title");
		assertThat(title.isEmpty(), is(false));
		assertThat(title.text(), is("Test-Title"));
		Elements span = document.select("span.title");
		assertThat(span.isEmpty(), is(false));
		assertThat(span.text(), is("Test-Title"));
	}
}
