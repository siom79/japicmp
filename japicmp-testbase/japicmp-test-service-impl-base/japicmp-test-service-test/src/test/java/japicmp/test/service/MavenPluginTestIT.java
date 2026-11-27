package japicmp.test.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class MavenPluginTestIT {

	/**
	 * Report of the execution with explicitly configured old and new classpath dependencies.
	 */
	@Test
	public void testTextOutput() throws IOException {
		assertReport(readReport("japicmp.diff"));
	}

	/**
	 * Report of the execution with {@code <resolveSeparateClassPaths>true</resolveSeparateClassPaths>}:
	 * the classpaths are resolved from the dependencies of the old and new artifact, hence the
	 * result must be the same as with the explicitly configured classpaths.
	 */
	@Test
	public void testTextOutputWithResolvedSeparateClassPaths() throws IOException {
		assertReport(readReport("separate-classpaths.diff"));
	}

	private static void assertReport(List<String> lines) {
		Optional<String> lineOptional = findLineThatContains(lines, "SubclassAddsNewStaticField");
		assertThat(lineOptional.isPresent(), is(true));
		assertThat(lineOptional.get().contains("***!"), is(true));
		// the superclass only exists in the old or new service artifact respectively, hence this
		// can only be evaluated with two separate classpaths
		lineOptional = findLineThatContains(lines, "SuperclassChanged");
		assertThat(lineOptional.isPresent(), is(true));
		assertThat(lineOptional.get().contains("***!"), is(true));
		assertThat(findLineThatContains(lines,
			"MODIFIED SUPERCLASS: japicmp.test.service.SuperclassV2 (<- japicmp.test.service.SuperclassV1)").isPresent(), is(true));
		// interfaces are only reported if they can be loaded from the respective classpath
		assertThat(findLineThatContains(lines, "UNCHANGED INTERFACE: japicmp.test.service.InterfaceMethodAdded").isPresent(), is(true));
	}

	private static List<String> readReport(String fileName) throws IOException {
		Path path = Paths.get(System.getProperty("user.dir"), "target", "japicmp", fileName);
		return Files.readAllLines(path, StandardCharsets.UTF_8);
	}

	private static Optional<String> findLineThatContains(List<String> lines, String str) {
		for (String line : lines) {
			if (line.contains(str)) {
				return Optional.of(line);
			}
		}
		return Optional.empty();
	}
}
