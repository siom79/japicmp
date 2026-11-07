package japicmp.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.fail;

public class ITClassFileFormatVersion {

	@Test
	public void testClassFileFormatVersionIsPresent() throws IOException {
		Path htmlPath = Paths.get(System.getProperty("user.dir"), "target", "japicmp", "class-file-format-version.html");
		if (!Files.exists(htmlPath)) {
			return; //in JDK 1.7 case
		}
		Document document = Jsoup.parse(htmlPath.toFile(), StandardCharsets.UTF_8.toString());
		Elements classFileFormatElements = document.select(".classfile");
		assertThat(classFileFormatElements.isEmpty(), is(false));
		for (Element element : classFileFormatElements) {
			if (!"50.0".equals(element.attr("data-old")) || !"52.0".equals(element.attr("data-new"))) {
				fail("class file format version is not 50.0 -> 52.0: " + element.attr("data-old") + " -> " + element.attr("data-new"));
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
