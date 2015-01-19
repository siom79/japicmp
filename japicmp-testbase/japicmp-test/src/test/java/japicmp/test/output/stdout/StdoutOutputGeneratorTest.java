package japicmp.test.output.stdout;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import japicmp.config.ImmutableOptions;
import japicmp.output.stdout.StdoutOutputGenerator;
import japicmp.test.output.OutputTestHelper;
import org.junit.Test;

public class StdoutOutputGeneratorTest {

	@Test
	public void test() {

		OutputTestHelper.Config config = OutputTestHelper.newTestConfig();
		ImmutableOptions.Builder opt = config.options();
		ImmutableOptions options = opt.withOnlyModifications(true).build();

		StdoutOutputGenerator generator = new StdoutOutputGenerator(options);
		String string = generator.generate(options.getOldArchive(), options.getNewArchive(), config
				.classes());
		assertThat(string, containsString("+++  NEW CLASS: PUBLIC(+) japicmp.test.Added"));
		assertThat(string, containsString("---! REMOVED CLASS: PUBLIC(-) japicmp.test.Removed"));
		assertThat(string, containsString("***! MODIFIED CLASS: PUBLIC STATIC japicmp.test.Superclasses$SuperClassChanges"));
		assertThat(string, containsString("***! MODIFIED SUPERCLASS: japicmp.test.Superclasses$SuperclassB (<- japicmp.test.Superclasses$SuperclassA)"));
	}
}
