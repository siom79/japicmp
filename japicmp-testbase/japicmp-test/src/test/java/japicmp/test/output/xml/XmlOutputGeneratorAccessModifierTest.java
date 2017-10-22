package japicmp.test.output.xml;

import japicmp.model.AccessModifier;
import japicmp.model.JApiClass;
import japicmp.test.AccessModifierLevel;
import japicmp.test.util.Helper;
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

import static japicmp.test.util.Helper.replaceLastDotWith$;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class XmlOutputGeneratorAccessModifierTest {
	private static Document documentPublic;
	private static Document documentPrivate;

	@BeforeClass
	public static void beforeClass() throws IOException {
		Path diffPublicXmlFilePath = Paths.get(System.getProperty("user.dir"), "target", "diff_public.xml");
		Path diffPublicHtmlFilePath = Paths.get(System.getProperty("user.dir"), "target", "diff_public.html");
		Path diffPrivateXmlFilePath = Paths.get(System.getProperty("user.dir"), "target", "diff_private.xml");
		Path diffPrivateHtmlFilePath = Paths.get(System.getProperty("user.dir"), "target", "diff_private.html");
		List<JApiClass> jApiClasses = Helper.compareTestV1WithTestV2(AccessModifier.PUBLIC);
		Helper.generateHtmlOutput(jApiClasses, diffPublicXmlFilePath.toString(), diffPublicHtmlFilePath.toString(), true, AccessModifier.PUBLIC);
		jApiClasses = Helper.compareTestV1WithTestV2(AccessModifier.PRIVATE);
		Helper.generateHtmlOutput(jApiClasses, diffPrivateXmlFilePath.toString(), diffPrivateHtmlFilePath.toString(), true, AccessModifier.PRIVATE);
		File htmlFilePublic = diffPublicHtmlFilePath.toFile();
		File htmlFilePrivate = diffPrivateHtmlFilePath.toFile();
		documentPublic = Jsoup.parse(htmlFilePublic, Charset.forName("UTF-8").toString());
		documentPrivate = Jsoup.parse(htmlFilePrivate, Charset.forName("UTF-8").toString());
	}

	@Test
	public void publicFilterAccessModifierChangesFromPrivateToPublicVisible() {
		Elements divForClass = XmlHelper.getDivForClass(documentPublic, replaceLastDotWith$(AccessModifierLevel.AccessModifierChangesFromPrivateToPublic.class.getCanonicalName()));
		assertThat(divForClass.isEmpty(), is(false));
	}

	@Test
	public void privateFilterAccessModifierChangesFromPrivateToPublicVisible() {
		Elements divForClass = XmlHelper.getDivForClass(documentPrivate, replaceLastDotWith$(AccessModifierLevel.AccessModifierChangesFromPrivateToPublic.class.getCanonicalName()));
		assertThat(divForClass.isEmpty(), is(false));
	}

	@Test
	public void publicFilterAccessModifierChangesFromPublicToPrivateVisible() {
		Elements divForClass = XmlHelper.getDivForClass(documentPublic, replaceLastDotWith$(AccessModifierLevel.AccessModifierChangesFromPublicToPrivate.class.getCanonicalName()));
		assertThat(divForClass.isEmpty(), is(false));
	}

	@Test
	public void privateFilterAccessModifierChangesFromPublicToPrivateVisible() {
		Elements divForClass = XmlHelper.getDivForClass(documentPrivate, replaceLastDotWith$(AccessModifierLevel.AccessModifierChangesFromPublicToPrivate.class.getCanonicalName()));
		assertThat(divForClass.isEmpty(), is(false));
	}

	@Test
	public void publicFilterAccessModifierChangesBelowPublicVisible() {
		Elements divForClass = XmlHelper.getDivForClass(documentPublic, replaceLastDotWith$(AccessModifierLevel.AccessModifierChangesBelowPublic.class.getCanonicalName()));
		assertThat(divForClass.isEmpty(), is(true));
	}

	@Test
	public void privateFilterAccessModifierChangesBelowPublicVisible() {
		Elements divForClass = XmlHelper.getDivForClass(documentPrivate, replaceLastDotWith$(AccessModifierLevel.AccessModifierChangesBelowPublic.class.getCanonicalName()));
		assertThat(divForClass.isEmpty(), is(false));
	}
}
