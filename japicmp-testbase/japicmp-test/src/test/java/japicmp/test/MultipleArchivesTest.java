package japicmp.test;

import japicmp.util.Optional;
import japicmp.cmp.JApiCmpArchive;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.output.stdout.StdoutOutputGenerator;
import japicmp.output.xml.XmlOutput;
import japicmp.output.xml.XmlOutputGenerator;
import japicmp.output.xml.XmlOutputGeneratorOptions;
import japicmp.output.xml.model.JApiCmpXmlRoot;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MultipleArchivesTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setAccessModifier(AccessModifier.PRIVATE);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		jApiClasses = jarArchiveComparator.compare(createListOfOldArchives(), createListOfNewArchives());
	}

	private static List<JApiCmpArchive> createListOfOldArchives() {
		return Arrays.asList(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test2-v1.jar"));
	}

	private static List<JApiCmpArchive> createListOfNewArchives() {
		return Arrays.asList(getArchive("japicmp-test-v2.jar"), getArchive("japicmp-test2-v2.jar"));
	}

	@Test
	public void testUnchangedClassOfBothArchives() {
		JApiClass jApiClass = getJApiClass(jApiClasses, Unchanged.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		jApiClass = getJApiClass(jApiClasses, japicmp.test2.Unchanged.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
	}

	@Test
	public void testStdoutputGenerator() {
		Options options = Options.newDefault();
		options.getOldArchives().addAll(createListOfOldArchives());
		options.getNewArchives().addAll(createListOfNewArchives());
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options, jApiClasses);
		String output = generator.generate();
		assertThat(output.contains("===  UNCHANGED CLASS: PUBLIC japicmp.test.Unchanged"), is(true));
		assertThat(output.contains("===  UNCHANGED CLASS: PUBLIC japicmp.test2.Unchanged"), is(true));
		assertThat(output.contains("japicmp-test-v1.jar"), is(true));
		assertThat(output.contains("japicmp-test-v2.jar"), is(true));
		assertThat(output.contains("japicmp-test2-v1.jar"), is(true));
		assertThat(output.contains("japicmp-test2-v2.jar"), is(true));
	}

	@Test
	public void testXmlOutputGenerator() throws UnsupportedEncodingException {
		Options options = Options.newDefault();
		options.getOldArchives().addAll(createListOfOldArchives());
		options.getNewArchives().addAll(createListOfNewArchives());
		Path xmlOutputPath = Paths.get(System.getProperty("user.dir"), "target", "MultipleArchivesTest.xml");
		options.setXmlOutputFile(Optional.of(xmlOutputPath.toString()));
		XmlOutputGeneratorOptions xmlOutputGeneratorOptions = new XmlOutputGeneratorOptions();
		xmlOutputGeneratorOptions.setCreateSchemaFile(false);
		XmlOutputGenerator generator = new XmlOutputGenerator(jApiClasses, options, xmlOutputGeneratorOptions);
		XmlOutput xmlOutput = generator.generate();
		JApiCmpXmlRoot jApiCmpXmlRoot = xmlOutput.getJApiCmpXmlRoot();
		assertThat(jApiCmpXmlRoot.getOldJar().contains("japicmp-test-v1.jar"), is(true));
		assertThat(jApiCmpXmlRoot.getOldJar().contains("japicmp-test2-v1.jar"), is(true));
		assertThat(jApiCmpXmlRoot.getNewJar().contains("japicmp-test-v2.jar"), is(true));
		assertThat(jApiCmpXmlRoot.getNewJar().contains("japicmp-test2-v2.jar"), is(true));
		List<JApiClass> classes = jApiCmpXmlRoot.getClasses();
		assertThat(getJApiClass(classes, japicmp.test.Unchanged.class.getName()).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(getJApiClass(classes, japicmp.test2.Unchanged.class.getName()).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
	}
}
