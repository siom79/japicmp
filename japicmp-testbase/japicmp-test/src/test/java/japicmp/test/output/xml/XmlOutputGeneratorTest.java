package japicmp.test.output.xml;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.filter.JavadocLikePackageFilter;
import japicmp.model.JApiClass;
import japicmp.output.html.HtmlOutput;
import japicmp.output.html.HtmlOutputGenerator;
import japicmp.output.html.HtmlOutputGeneratorOptions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static japicmp.test.output.xml.XmlHelper.getDivForClass;
import static japicmp.test.util.Helper.getArchive;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class XmlOutputGeneratorTest {
	private static final String JAPICMP_TEST_SEMVER001 = "japicmp.test.semver001";
	private static final String TITLE = "Title with Ümläüte";
	private static List<JApiClass> jApiClasses;
	private static Document document;
	private static Document documentOnlyModifications;
	private static File htmlFile;

	@BeforeAll
	public static void beforeClass() throws IOException {
		Path diffHtmlFilePath = Paths.get(System.getProperty("user.dir"), "target", "diff.html");
		Path diffOnlyModificationsHtmlFilePath = Paths.get(System.getProperty("user.dir"), "target", "diff_onlyModifications.html");
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new JavadocLikePackageFilter(JAPICMP_TEST_SEMVER001, false));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		generateHtmlOutput(diffHtmlFilePath.toString(), false);
		generateHtmlOutput(diffOnlyModificationsHtmlFilePath.toString(), true);
		htmlFile = Paths.get(System.getProperty("user.dir"), "target", "diff.html").toFile();
		document = Jsoup.parse(htmlFile, StandardCharsets.UTF_8.toString());
		documentOnlyModifications = Jsoup.parse(diffOnlyModificationsHtmlFilePath.toFile(), StandardCharsets.UTF_8.toString());
	}

	private static void generateHtmlOutput( String htmlOutputFile, boolean outputOnlyModifications) throws IOException {
		Options options = Options.newDefault();
		options.setHtmlOutputFile(Optional.of(htmlOutputFile));
		options.setOutputOnlyModifications(outputOnlyModifications);
		HtmlOutputGeneratorOptions htmlOutputGeneratorOptions = new HtmlOutputGeneratorOptions();
		htmlOutputGeneratorOptions.setTitle(TITLE);
		HtmlOutputGenerator generator = new HtmlOutputGenerator(jApiClasses, options, htmlOutputGeneratorOptions);
		HtmlOutput htmlOutput = generator.generate();
		Files.write(Paths.get(htmlOutputFile), htmlOutput.getHtml().getBytes(StandardCharsets.UTF_8));
	}

	@Test
	public void testMetaInformationTable() {
		assertThat(document.select("div.meta-information > table").isEmpty(), is(false));
		assertThat(document.select("div.meta-information > table > tbody > tr").size(), is(10));
	}

	@Test
	public void htmlFileNotContainsPackageSemver001() throws IOException {
		List<String> lines = Files.readAllLines(htmlFile.toPath(), StandardCharsets.UTF_8);
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
	public void superclassAllChangesAddedWithSuperclass() {
		Elements divSuperClass = getSuperClassDiv(document, "japicmp.test.Superclasses$AddedWithSuperclass");
		assertThat(divSuperClass.select("table").isEmpty(), is(false));
	}

	@Test
	public void superclassOnlyModificationsAddedWithSuperclass() {
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
	public void superclassAllChangesAdded() {
		Elements divSuperClass = getSuperClassDiv(document, "japicmp.test.Superclasses$AddedWithSuperclass");
		assertThat(divSuperClass.select("table").isEmpty(), is(false));
	}

	@Test
	public void superclassOnlyModificationsAdded() {
		Elements divSuperClass = getSuperClassDiv(documentOnlyModifications, "japicmp.test.Added");
		assertThat(divSuperClass.select("table").isEmpty(), is(true));
	}

	@Test
	public void superclassAddedWithSuperclass() {
		Elements divSuperClass = getSuperClassDiv(document, "japicmp.test.Superclasses$AddedWithSuperclass");
		assertThat(divSuperClass.select("table").isEmpty(), is(false));
	}

	@Test
	public void superclassAddedWithSuperclassOnlyModifications() {
		Elements divSuperClass = getSuperClassDiv(documentOnlyModifications, "japicmp.test.Superclasses$AddedWithSuperclass");
		assertThat(divSuperClass.select("table").isEmpty(), is(false));
	}

	@Test
	public void superclassRemovedWithSuperclass() {
		Elements divSuperClass = getSuperClassDiv(document, "japicmp.test.Superclasses$RemovedWithSuperclass");
		assertThat(divSuperClass.select("table").isEmpty(), is(false));
	}

	@Test
	public void superclassRemovedWithSuperclassOnlyModifications() {
		Elements divSuperClass = getSuperClassDiv(documentOnlyModifications, "japicmp.test.Superclasses$RemovedWithSuperclass");
		assertThat(divSuperClass.select("table").isEmpty(), is(false));
	}

	@Test
	public void superclassNoSuperclassToSuperclass() {
		Elements divSuperClass = getSuperClassDiv(document, "japicmp.test.Superclasses$NoSuperclassToSuperclass");
		assertThat(divSuperClass.select("table").isEmpty(), is(false));
	}

	@Test
	public void superclassNoSuperclassToSuperclassOnlyModifications() {
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
