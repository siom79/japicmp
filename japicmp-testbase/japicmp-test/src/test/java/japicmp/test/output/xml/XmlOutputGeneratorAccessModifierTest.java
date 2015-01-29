package japicmp.test.output.xml;

import com.google.common.base.Optional;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.AccessModifier;
import japicmp.model.JApiClass;
import japicmp.output.xml.XmlOutputGenerator;
import japicmp.test.AccessModifierLevel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.replaceLastDotWith$;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class XmlOutputGeneratorAccessModifierTest {
	private static Document documentPublic;
	private static Document documentPrivate;

	@BeforeClass
	public static void beforeClass() throws IOException {
		List<JApiClass> jApiClasses = compare();
		generateHtmlOutput(jApiClasses, "target/diff_public.xml", "target/diff_public.html", AccessModifier.PUBLIC);
		jApiClasses = compare();
		generateHtmlOutput(jApiClasses, "target/diff_private.xml", "target/diff_private.html", AccessModifier.PRIVATE);
		File htmlFilePublic = Paths.get(System.getProperty("user.dir"), "target", "diff_public.html").toFile();
		File htmlFilePrivate = Paths.get(System.getProperty("user.dir"), "target", "diff_private.html").toFile();
		documentPublic = Jsoup.parse(htmlFilePublic, Charset.forName("UTF-8").toString());
		documentPrivate = Jsoup.parse(htmlFilePrivate, Charset.forName("UTF-8").toString());
	}

	private static List<JApiClass> compare() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		return jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	private static void generateHtmlOutput(List<JApiClass> jApiClasses, String xmlOutputFile, String htmlOutputFile, AccessModifier accessModifier) {
		XmlOutputGenerator generator = new XmlOutputGenerator();
		Options options = new Options();
		options.setXmlOutputFile(Optional.of(xmlOutputFile));
		options.setHtmlOutputFile(Optional.of(htmlOutputFile));
		options.setOutputOnlyModifications(true);
		options.setAccessModifier(Optional.of(accessModifier));
		generator.generate("/old/Path", "/new/Path", jApiClasses, options);
	}

	@Test
	public void publicFilterAccessModifierChangesFromPrivateToPublicVisible() {
		Elements divForClass = XmlOutputGeneratorTest.getDivForClass(documentPublic, replaceLastDotWith$(AccessModifierLevel.AccessModifierChangesFromPrivateToPublic.class.getCanonicalName()));
		assertThat(divForClass.isEmpty(), is(false));
	}

	@Test
	public void privateFilterAccessModifierChangesFromPrivateToPublicVisible() {
		Elements divForClass = XmlOutputGeneratorTest.getDivForClass(documentPrivate, replaceLastDotWith$(AccessModifierLevel.AccessModifierChangesFromPrivateToPublic.class.getCanonicalName()));
		assertThat(divForClass.isEmpty(), is(false));
	}

	@Test
	public void publicFilterAccessModifierChangesFromPublicToPrivateVisible() {
		Elements divForClass = XmlOutputGeneratorTest.getDivForClass(documentPublic, replaceLastDotWith$(AccessModifierLevel.AccessModifierChangesFromPublicToPrivate.class.getCanonicalName()));
		assertThat(divForClass.isEmpty(), is(false));
	}

	@Test
	public void privateFilterAccessModifierChangesFromPublicToPrivateVisible() {
		Elements divForClass = XmlOutputGeneratorTest.getDivForClass(documentPrivate, replaceLastDotWith$(AccessModifierLevel.AccessModifierChangesFromPublicToPrivate.class.getCanonicalName()));
		assertThat(divForClass.isEmpty(), is(false));
	}

	@Test
	public void publicFilterAccessModifierChangesBelowPublicVisible() {
		Elements divForClass = XmlOutputGeneratorTest.getDivForClass(documentPublic, replaceLastDotWith$(AccessModifierLevel.AccessModifierChangesBelowPublic.class.getCanonicalName()));
		assertThat(divForClass.isEmpty(), is(true));
	}

	@Test
	public void privateFilterAccessModifierChangesBelowPublicVisible() {
		Elements divForClass = XmlOutputGeneratorTest.getDivForClass(documentPrivate, replaceLastDotWith$(AccessModifierLevel.AccessModifierChangesBelowPublic.class.getCanonicalName()));
		assertThat(divForClass.isEmpty(), is(false));
	}
}
