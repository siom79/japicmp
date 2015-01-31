package japicmp.test.output.stdout;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.JApiClass;
import japicmp.output.stdout.StdoutOutputGenerator;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class StdoutOutputGeneratorTest {

	@Test
	public void test() {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		Options options = new Options();
		options.setOutputOnlyModifications(true);
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options);
		String string = generator.generate(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"), jApiClasses);
		assertThat(string, containsString("+++  NEW CLASS: PUBLIC(+) japicmp.test.Added"));
		assertThat(string, containsString("---! REMOVED CLASS: PUBLIC(-) japicmp.test.Removed"));
		assertThat(string, containsString("***! MODIFIED CLASS: PUBLIC STATIC japicmp.test.Superclasses$SuperClassChanges"));
		assertThat(string, containsString("***! MODIFIED SUPERCLASS: japicmp.test.Superclasses$SuperclassB (<- japicmp.test.Superclasses$SuperclassA)"));
		assertNoToStringModel(string);
	}

	private void assertNoToStringModel(String string) {
		ImmutableList<String> toString = FluentIterable.from(Splitter.on("\n").split(string)).filter(new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return input.contains("japicmp.model.");
			}
		}).toList();
		assertEquals("", Joiner.on("\n").join(toString));
	}
}
