package japicmp.test.output.xml;

import japicmp.model.AccessModifier;
import japicmp.model.JApiClass;
import japicmp.test.AccessModifierLevel;
import japicmp.test.util.Helper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static japicmp.test.util.Helper.replaceLastDotWith$;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class XmlOutputGeneratorAccessModifierTest {
	private static Document documentPublic;
	private static Document documentPrivate;

	@BeforeAll
	public static void beforeClass() throws IOException {
		Path diffPublicHtmlFilePath = Paths.get(System.getProperty("user.dir"), "target", "diff_public.html");
		Path diffPrivateHtmlFilePath = Paths.get(System.getProperty("user.dir"), "target", "diff_private.html");
		List<JApiClass> jApiClasses = Helper.compareTestV1WithTestV2(AccessModifier.PUBLIC);
		Helper.generateHtmlOutput(jApiClasses, diffPublicHtmlFilePath.toString(), true, AccessModifier.PUBLIC);
		jApiClasses = Helper.compareTestV1WithTestV2(AccessModifier.PRIVATE);
		Helper.generateHtmlOutput(jApiClasses, diffPrivateHtmlFilePath.toString(), true, AccessModifier.PRIVATE);
		File htmlFilePublic = diffPublicHtmlFilePath.toFile();
		File htmlFilePrivate = diffPrivateHtmlFilePath.toFile();
		documentPublic = Jsoup.parse(htmlFilePublic, StandardCharsets.UTF_8.toString());
		documentPrivate = Jsoup.parse(htmlFilePrivate, StandardCharsets.UTF_8.toString());
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
		assertThat(divForClass.isEmpty(), is(false));
	}

	@Test
	public void privateFilterAccessModifierChangesBelowPublicVisible() {
		Elements divForClass = XmlHelper.getDivForClass(documentPrivate, replaceLastDotWith$(AccessModifierLevel.AccessModifierChangesBelowPublic.class.getCanonicalName()));
		assertThat(divForClass.isEmpty(), is(false));
	}
}
