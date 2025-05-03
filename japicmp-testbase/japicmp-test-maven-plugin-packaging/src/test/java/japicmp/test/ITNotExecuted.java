package japicmp.test;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ITNotExecuted {

	@Test
	public void testNotExecuted() {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp")), is(false));
	}
}
