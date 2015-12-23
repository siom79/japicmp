package japicmp.test;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ITNotExecuted {

	@Test
	public void testNotExecuted() {
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "japicmp")), is(false));
	}
}
