package japicmp.test.output.stdout;

import static japicmp.test.util.Helper.getArchive;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.JApiClass;
import japicmp.output.stdout.StdoutOutputGenerator;

import java.util.List;

import org.junit.Test;

public class StdoutOutputGeneratorTest {

	@Test
	public void test() {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		Options options = new Options();
		options.setOutputOnlyModifications(true);
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options);
		String string = generator.generate(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"), jApiClasses);
		assertThat(string, containsString("+++ NEW CLASS japicmp.test.Added"));
		assertThat(string, containsString("--- REMOVED CLASS japicmp.test.Removed"));
		assertThat(string, containsString("*** MODIFIED CLASS japicmp.test.Superclasses$SuperClassChanges"));
		assertThat(string, containsString("*** MODIFIED SUPERCLASS japicmp.test.Superclasses$SuperclassB (<- japicmp.test.Superclasses$SuperclassA)"));
	}
}
