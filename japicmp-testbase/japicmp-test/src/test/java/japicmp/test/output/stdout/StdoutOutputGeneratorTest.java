package japicmp.test.output.stdout;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.filter.JavadocLikePackageFilter;
import japicmp.model.JApiClass;
import japicmp.output.stdout.StdoutOutputGenerator;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static japicmp.test.util.Helper.getArchive;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class StdoutOutputGeneratorTest {

	@Test
	public void test() {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		Options options = Options.newDefault();
		options.setOutputOnlyModifications(true);
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options, jApiClasses);
		String string = generator.generate();
		assertThat(string, containsString("+++  NEW CLASS: PUBLIC(+) japicmp.test.Added"));
		assertThat(string, containsString("---! REMOVED CLASS: PUBLIC(-) japicmp.test.Removed"));
		assertThat(string, containsString("***! MODIFIED CLASS: PUBLIC STATIC japicmp.test.Superclasses$SuperClassChanges"));
		assertThat(string, containsString("***! MODIFIED SUPERCLASS: japicmp.test.Superclasses$SuperclassB (<- japicmp.test.Superclasses$SuperclassA)"));
		assertThat(string, containsString("===  UNCHANGED CLASS: PUBLIC japicmp.test.Annotations$AuthorAnnotationGetsNewValue"));
		assertThat(string, containsString("\t***  MODIFIED ANNOTATION: japicmp.test.Annotations$Author"));
		assertThat(string, containsString("===  UNCHANGED CLASS: PUBLIC japicmp.test.Annotations"));
		assertThat(string, containsString("\t===  UNCHANGED FIELD: PUBLIC int fieldAnnotationValueModified"));
		assertThat(string, containsString("\t\t***  MODIFIED ANNOTATION: japicmp.test.Annotations$FieldAnnotation"));
		assertNoToStringModel(string);
	}

	private void assertNoToStringModel(String string) {
		assertEquals("", Stream.of(string.split("\n")).filter(input -> input.contains("japicmp.model.")).collect(Collectors.joining("\n")));
	}

	@Test
	public void testOnlyModificationsAnnotationAddedToConstructor() {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.getFilters().getIncludes().add(new JavadocLikePackageFilter("japicmp.test.annotation", false));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(jarArchiveComparatorOptions);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		Options options = Options.newDefault();
		options.setOutputOnlyModifications(true);
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options, jApiClasses);
		String string = generator.generate();
		assertThat(string, containsString("===  UNCHANGED CLASS: PUBLIC japicmp.test.annotation.AnnotationAddedToConstructor"));
	}
}
