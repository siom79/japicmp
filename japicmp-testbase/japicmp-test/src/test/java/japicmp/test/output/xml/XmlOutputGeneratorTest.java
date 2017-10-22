package japicmp.test.output.xml;

import com.google.common.io.Files;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.filter.JavadocLikePackageFilter;
import japicmp.model.JApiClass;
import japicmp.output.xml.XmlOutput;
import japicmp.output.xml.XmlOutputGenerator;
import japicmp.output.xml.XmlOutputGeneratorOptions;
import japicmp.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static japicmp.test.output.xml.XmlHelper.getDivForClass;
import static japicmp.test.util.Helper.getArchive;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class XmlOutputGeneratorTest {
	private static final String JAPICMP_TEST_SEMVER001 = "japicmp.test.semver001";
	private static final String TITLE = "Title with Ümläüte";
	private static List<JApiClass> jApiClasses;
	private static Document document;
	private static Document documentOnlyModifications;
	private static File htmlFile;

	@BeforeClass
	public static void beforeClass() throws IOException {
		Path diffXmlFilePath = Paths.get(System.getProperty("user.dir"), "target", "diff.xml");
		Path diffHtmlFilePath = Paths.get(System.getProperty("user.dir"), "target", "diff.html");
		Path diffOnlyModificationsXmlFilePath = Paths.get(System.getProperty("user.dir"), "target", "diff_onlyModifications.xml");
		Path diffOnlyModificationsHtmlFilePath = Paths.get(System.getProperty("user.dir"), "target", "diff_onlyModifications.html");
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new JavadocLikePackageFilter(JAPICMP_TEST_SEMVER001, false));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		generateHtmlOutput(diffXmlFilePath.toString(), diffHtmlFilePath.toString(), false);
		generateHtmlOutput(diffOnlyModificationsXmlFilePath.toString(), diffOnlyModificationsHtmlFilePath.toString(), true);
		htmlFile = Paths.get(System.getProperty("user.dir"), "target", "diff.html").toFile();
		document = Jsoup.parse(htmlFile, Charset.forName("UTF-8").toString());
		documentOnlyModifications = Jsoup.parse(diffOnlyModificationsHtmlFilePath.toFile(), Charset.forName("UTF-8").toString());
	}

	private static void generateHtmlOutput(String xmlOutpuFile, String htmlOutputFile, boolean outputOnlyModifications) {
		Options options = Options.newDefault();
		options.setXmlOutputFile(Optional.of(xmlOutpuFile));
		options.setHtmlOutputFile(Optional.of(htmlOutputFile));
		options.setOutputOnlyModifications(outputOnlyModifications);
		XmlOutputGeneratorOptions xmlOutputGeneratorOptions = new XmlOutputGeneratorOptions();
		xmlOutputGeneratorOptions.setTitle(TITLE);
		XmlOutputGenerator generator = new XmlOutputGenerator(jApiClasses, options, xmlOutputGeneratorOptions);
		XmlOutput xmlOutput = generator.generate();
		XmlOutputGenerator.writeToFiles(options, xmlOutput);
	}

	@Test
	public void testMetaInformationTable() throws IOException {
		assertThat(document.select("div.meta-information > table").isEmpty(), is(false));
		assertThat(document.select("div.meta-information > table > tbody > tr").size(), is(10));
	}

	@Test
	public void htmlFileNotContainsPackageSemver001() throws IOException {
		List<String> lines = Files.readLines(htmlFile, Charset.forName("UTF-8"));
		boolean containsPackageName = false;
		for (String line : lines) {
			if (line.contains(JAPICMP_TEST_SEMVER001)) {
				containsPackageName = true;
				break;
			}
		}
		assertThat(containsPackageName, is(false));
	}

	@Test
	public void superclassAllChangesAddedWithSuperclass() throws IOException {
		Elements divSuperClass = getSuperClassDiv(document, "japicmp.test.Superclasses$AddedWithSuperclass");
		assertThat(divSuperClass.select("table").isEmpty(), is(false));
	}

	@Test
	public void superclassOnlyModificationsAddedWithSuperclass() throws IOException {
		Elements divSuperClass = getSuperClassDiv(documentOnlyModifications, "japicmp.test.Superclasses$AddedWithSuperclass");
		assertThat(divSuperClass.select("table").isEmpty(), is(false));
	}

	private Elements getSuperClassDiv(Document document, String className) {
		Elements divForClass = getDivForClass(document, className);
		Elements divSuperclass = divForClass.select("div[class=class_superclass]");
		assertThat(divSuperclass.isEmpty(), is(false));
		return divSuperclass;
	}

	@Test
	public void superclassAllChangesAdded() throws IOException {
		Elements divSuperClass = getSuperClassDiv(document, "japicmp.test.Added");
		assertThat(divSuperClass.select("table").isEmpty(), is(false));
	}

	@Test
	public void superclassOnlyModificationsAdded() throws IOException {
		Elements divSuperClass = getSuperClassDiv(documentOnlyModifications, "japicmp.test.Added");
		assertThat(divSuperClass.select("table").isEmpty(), is(true));
	}

	@Test
	public void superclassAddedWithSuperclass() throws IOException {
		Elements divSuperClass = getSuperClassDiv(document, "japicmp.test.Superclasses$AddedWithSuperclass");
		assertThat(divSuperClass.select("table").isEmpty(), is(false));
	}

	@Test
	public void superclassAddedWithSuperclassOnlyModifications() throws IOException {
		Elements divSuperClass = getSuperClassDiv(documentOnlyModifications, "japicmp.test.Superclasses$AddedWithSuperclass");
		assertThat(divSuperClass.select("table").isEmpty(), is(false));
	}

	@Test
	public void superclassRemovedWithSuperclass() throws IOException {
		Elements divSuperClass = getSuperClassDiv(document, "japicmp.test.Superclasses$RemovedWithSuperclass");
		assertThat(divSuperClass.select("table").isEmpty(), is(false));
	}

	@Test
	public void superclassRemovedWithSuperclassOnlyModifications() throws IOException {
		Elements divSuperClass = getSuperClassDiv(documentOnlyModifications, "japicmp.test.Superclasses$RemovedWithSuperclass");
		assertThat(divSuperClass.select("table").isEmpty(), is(false));
	}

	@Test
	public void superclassNoSuperclassToSuperclass() throws IOException {
		Elements divSuperClass = getSuperClassDiv(document, "japicmp.test.Superclasses$NoSuperclassToSuperclass");
		assertThat(divSuperClass.select("table").isEmpty(), is(false));
	}

	@Test
	public void superclassNoSuperclassToSuperclassOnlyModifications() throws IOException {
		Elements divSuperClass = getSuperClassDiv(documentOnlyModifications, "japicmp.test.Superclasses$NoSuperclassToSuperclass");
		assertThat(divSuperClass.select("table").isEmpty(), is(false));
	}

	@Test
	public void titleSetProperly() {
		Elements title = document.select("title");
		assertThat(title.isEmpty(), is(false));
		assertThat(title.text(), is("Title with Ümläüte"));
	}
}
