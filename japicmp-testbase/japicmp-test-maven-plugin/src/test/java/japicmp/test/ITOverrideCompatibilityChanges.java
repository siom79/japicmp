package japicmp.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ITOverrideCompatibilityChanges {

	public void testSemanticVersion() throws IOException {
		Path htmlPath = Paths.get(System.getProperty("user.dir"), "target", "japicmp", "override-compatibility-changes.html");
 		assertThat(Files.exists(htmlPath), is(true));
		List<String> htmlLines = Files.readAllLines(htmlPath, Charset.forName("UTF-8"));
		List<String> cssLines = Files.readAllLines(Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "css", "stylesheet.css"), Charset.forName("UTF-8"));
		assertThat(htmlLines.size(), not(is(0)));
		assertThat(cssLines.size(), not(is(0)));
		for (String cssLine : cssLines) {
			assertThat(htmlLines, hasItem(cssLine));
		}

		Document document = Jsoup.parse(htmlPath.toFile(), "UTF-8");
		Elements rows = document.select("#semver-version");
		Element element = rows.get(0);
		assertThat(element.text(), is("0.0.1"));
 	}
}
