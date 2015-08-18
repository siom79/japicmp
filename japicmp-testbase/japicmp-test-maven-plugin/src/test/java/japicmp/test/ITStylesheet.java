package japicmp.test;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class ITStylesheet {

	@Test
	public void testStylesheetIsUsed() throws IOException {
		Path htmlPath = Paths.get(System.getProperty("user.dir"), "target", "japicmp", "japicmp.html");
		assertThat(Files.exists(htmlPath), is(true));
		String htmlString = readFileAsString(htmlPath);
		String cssString = readFileAsString(Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "css", "stylesheet.css"));
		assertThat(htmlString.indexOf(cssString), not(is(-1)));
	}

	private String readFileAsString(Path htmlPath) throws IOException {
		byte[] htmlBytes = Files.readAllBytes(htmlPath);
		return new String(htmlBytes, "UTF-8");
	}
}
