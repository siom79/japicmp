package japicmp.test.output.stdout;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.filter.JavadocLikePackageFilter;
import japicmp.model.JApiClass;
import japicmp.output.stdout.StdoutOutputGenerator;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static org.hamcrest.CoreMatchers.containsString;

class StdoutOutputGeneratorTest {

	@Test
	void test() {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		Options options = Options.newDefault();
		options.setOutputOnlyModifications(true);
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options, jApiClasses);
		String string = generator.generate();
		MatcherAssert.assertThat(string, containsString("+++  NEW CLASS: PUBLIC(+) japicmp.test.Added"));
		MatcherAssert.assertThat(string, containsString("---! REMOVED CLASS: PUBLIC(-) japicmp.test.Removed"));
		MatcherAssert.assertThat(string, containsString("***! MODIFIED CLASS: PUBLIC STATIC japicmp.test.Superclasses$SuperClassChanges"));
		MatcherAssert.assertThat(string, containsString("***! MODIFIED SUPERCLASS: japicmp.test.Superclasses$SuperclassB (<- japicmp.test.Superclasses$SuperclassA)"));
		MatcherAssert.assertThat(string, containsString("===  UNCHANGED CLASS: PUBLIC japicmp.test.Annotations$AuthorAnnotationGetsNewValue"));
		MatcherAssert.assertThat(string, containsString("\t***  MODIFIED ANNOTATION: japicmp.test.Annotations$Author"));
		MatcherAssert.assertThat(string, containsString("===  UNCHANGED CLASS: PUBLIC japicmp.test.Annotations"));
		MatcherAssert.assertThat(string, containsString("\t===  UNCHANGED FIELD: PUBLIC int fieldAnnotationValueModified"));
		MatcherAssert.assertThat(string, containsString("\t\t***  MODIFIED ANNOTATION: japicmp.test.Annotations$FieldAnnotation"));
		assertNoToStringModel(string);
	}

	private void assertNoToStringModel(String string) {
		ImmutableList<String> toString = FluentIterable.from(Splitter.on("\n").split(string)).filter(new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return input.contains("japicmp.model.");
			}
		}).toList();
		Assertions.assertEquals("", Joiner.on("\n").join(toString));
	}

	@Test
	void testOnlyModificationsAnnotationAddedToConstructor() {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.getFilters().getIncludes().add(new JavadocLikePackageFilter("japicmp.test.annotation", false));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(jarArchiveComparatorOptions);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		Options options = Options.newDefault();
		options.setOutputOnlyModifications(true);
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options, jApiClasses);
		String string = generator.generate();
		MatcherAssert.assertThat(string, containsString("===  UNCHANGED CLASS: PUBLIC japicmp.test.annotation.AnnotationAddedToConstructor"));
	}
}
