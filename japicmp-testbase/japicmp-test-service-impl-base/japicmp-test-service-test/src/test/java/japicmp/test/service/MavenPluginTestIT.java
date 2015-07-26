package japicmp.test.service;

import com.google.common.base.Optional;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MavenPluginTestIT {

	@Test
	public void testTextOutput() throws IOException {
		Path path = Paths.get(System.getProperty("user.dir"), "target", "japicmp", "japicmp.diff");
		List<String> lines = Files.readAllLines(path, Charset.forName("UTF-8"));
		Optional<String> lineOptional = findLineThatContains(lines, "SubclassAddsNewStaticField");
		assertThat(lineOptional.isPresent(), is(true));
		assertThat(lineOptional.get().contains("***!"), is(true));
	}

	private Optional<String> findLineThatContains(List<String> lines, String str) {
		for (String line : lines) {
			if (line.contains(str)) {
				return Optional.of(line);
			}
		}
		return Optional.absent();
	}
}
