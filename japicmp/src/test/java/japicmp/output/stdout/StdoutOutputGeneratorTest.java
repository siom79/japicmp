package japicmp.output.stdout;

import japicmp.cli.JApiCli;
import japicmp.config.Options;
import japicmp.model.JApiClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class StdoutOutputGeneratorTest {

	@Test
	public void testNoChanges() {
		Options options = Options.newDefault();
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options, new ArrayList<JApiClass>(0));
		String generated = generator.generate();
		assertThat(generated, containsString(StdoutOutputGenerator.NO_CHANGES));
	}

	@Test
	public void testWarningWhenIgnoreMissingClasses() {
		Options options = Options.newDefault();
		options.setIgnoreMissingClasses(true);
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options, new ArrayList<JApiClass>(0));
		String generated = generator.generate();
		assertThat(generated, containsString(StdoutOutputGenerator.WARNING));
		assertThat(generated, containsString(JApiCli.IGNORE_MISSING_CLASSES));
	}
}
