package japicmp.test;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ITSkipped {

	@Test
	public void testSkipViaUserProperty() {
		Path xmlPath = Paths.get(System.getProperty("user.dir"), "target", "japicmp", "japicmp.xml");
		assertThat(Files.exists(xmlPath), is(false));
		Path htmlPath = Paths.get(System.getProperty("user.dir"), "target", "japicmp", "japicmp.html");
		assertThat(Files.exists(htmlPath), is(true));
	}
}
