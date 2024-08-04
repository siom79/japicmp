package japicmp.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ITClassFileFormatVersion {

	@Test
	public void testClassFileFormatVersionIsPresent() throws IOException {
		Path htmlPath = Paths.get(System.getProperty("user.dir"), "target", "japicmp", "class-file-format-version.html");
		if (!Files.exists(htmlPath)) {
			return; //in JDK 1.7 case
		}
		Document document = Jsoup.parse(htmlPath.toFile(), StandardCharsets.UTF_8.toString());
		Elements classFileFormatElements = document.select(".class_fileFormatVersion");
		assertThat(classFileFormatElements.isEmpty(), is(false));
		Elements tdCells = classFileFormatElements.select("table > tbody > tr > td");
		assertThat(tdCells.isEmpty(), is(false));
		for (Element element : tdCells) {
			String text = element.text();
			if (!"MODIFIED (!)".equals(text) && !"50.0".equals(text) && !"52.0".equals(text)) {
				Assert.fail("text of HTML element does not equal 'MODIFIED' or 50.0 or 52.0: " + text);
			}
		}
	}

	@Test
	public void testMarkdownReportDiffClassFileFormatVersionIsPresent() throws IOException {
		Path path = Paths.get(System.getProperty("user.dir"), "target", "japicmp", "class-file-format-version.md");
		if (!Files.exists(path)) {
			return; //in JDK 1.7 case
		}
		assertThat(Files.exists(path), is(true));
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		boolean found = false;
		for (String line : lines) {
			if (line.contains("~~JDK 6~~ &rarr; **JDK 8**")) {
				found = true;
				break;
			}
		}
		assertThat(found, is(true));
	}

	@Test
	public void testStoutDiffClassFileFormatVersionIsPresent() throws IOException {
		Path path = Paths.get(System.getProperty("user.dir"), "target", "japicmp", "class-file-format-version.diff");
		if (!Files.exists(path)) {
			return; //in JDK 1.7 case
		}
		assertThat(Files.exists(path), is(true));
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		boolean found = false;
		for (String line : lines) {
			if (line.contains("***! CLASS FILE FORMAT VERSION: 52.0 <- 50.0")) {
				found = true;
				break;
			}
		}
		assertThat(found, is(true));
	}
}
