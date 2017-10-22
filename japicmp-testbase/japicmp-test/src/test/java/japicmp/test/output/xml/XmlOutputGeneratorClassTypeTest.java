package japicmp.test.output.xml;

import japicmp.model.AccessModifier;
import japicmp.model.JApiClass;
import japicmp.test.ClassType;
import japicmp.test.util.Helper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class XmlOutputGeneratorClassTypeTest {
	private static Document documentPublic;

	@BeforeClass
	public static void beforeClass() throws IOException {
		Path diffPublicXmlPath = Paths.get(System.getProperty("user.dir"), "target", "diff_public.xml");
		Path diffPublicHtmlPath = Paths.get(System.getProperty("user.dir"), "target", "diff_public.html");
		List<JApiClass> jApiClasses = Helper.compareTestV1WithTestV2(AccessModifier.PUBLIC);
		Helper.generateHtmlOutput(jApiClasses, diffPublicXmlPath.toString(), diffPublicHtmlPath.toString(), false, AccessModifier.PUBLIC);
		File htmlFilePublic = Paths.get(System.getProperty("user.dir"), "target", "diff_public.html").toFile();
		documentPublic = Jsoup.parse(htmlFilePublic, Charset.forName("UTF-8").toString());
	}

	@Test
	public void testClassToAnnotation() {
		assertThatClassHeaderContains(ClassType.ClassToAnnotation.class, createHtmlChangeText("class", "annotation"));
	}

	@Test
	public void testClassToClass() {
		assertThatClassHeaderContains(ClassType.ClassToClass.class, createHtmlChangeText("class", "class"));
	}

	@Test
	public void testClassToEnum() {
		assertThatClassHeaderContains(ClassType.ClassToEnum.class, createHtmlChangeText("class", "enum"));
	}

	@Test
	public void testClassToInterface() {
		assertThatClassHeaderContains(ClassType.ClassToInterface.class, createHtmlChangeText("class", "interface"));
	}

	@Test
	public void testAnnotationToAnnotation() {
		assertThatClassHeaderContains(ClassType.AnnotationToAnnotation.class, createHtmlChangeText("annotation", "annotation"));
	}

	@Test
	public void testAnnotationToClass() {
		assertThatClassHeaderContains(ClassType.AnnotationToClass.class, createHtmlChangeText("annotation", "class"));
	}

	@Test
	public void testAnnotationToEnum() {
		assertThatClassHeaderContains(ClassType.AnnotationToEnum.class, createHtmlChangeText("annotation", "enum"));
	}

	@Test
	public void testAnnotationToInterface() {
		assertThatClassHeaderContains(ClassType.AnnotationToInterface.class, createHtmlChangeText("annotation", "interface"));
	}

	@Test
	public void testEnumToAnnotation() {
		assertThatClassHeaderContains(ClassType.EnumToAnnotation.class, createHtmlChangeText("enum", "annotation"));
	}

	@Test
	public void testEnumToClass() {
		assertThatClassHeaderContains(ClassType.EnumToClass.class, createHtmlChangeText("enum", "class"));
	}

	@Test
	public void testEnumToEnum() {
		assertThatClassHeaderContains(ClassType.EnumToEnum.class, createHtmlChangeText("enum", "enum"));
	}

	@Test
	public void testEnumToInterface() {
		assertThatClassHeaderContains(ClassType.EnumToInterface.class, createHtmlChangeText("enum", "interface"));
	}

	@Test
	public void testInterfaceToAnnotation() {
		assertThatClassHeaderContains(ClassType.InterfaceToAnnotation.class, createHtmlChangeText("interface", "annotation"));
	}

	@Test
	public void testInterfaceToClass() {
		assertThatClassHeaderContains(ClassType.InterfaceToClass.class, createHtmlChangeText("interface", "class"));
	}

	@Test
	public void testInterfaceToEnum() {
		assertThatClassHeaderContains(ClassType.InterfaceToEnum.class, createHtmlChangeText("interface", "enum"));
	}

	@Test
	public void testInterfaceToInterface() {
		assertThatClassHeaderContains(ClassType.InterfaceToInterface.class, createHtmlChangeText("interface", "interface"));
	}

	private void assertThatClassHeaderContains(Class<?> aClass, String htmlText) {
		Elements elements = getDivForClass(documentPublic, aClass.getName()).select("div.class_header > span.label > span");
		assertThat(elements.size(), is(not(0)));
		boolean found = false;
		for (Element element : elements) {
			if (element.html().contains(htmlText)) {
				found = true;
			}
		}
		assertThat(found, is(true));
	}

	private String createHtmlChangeText(String from, String to) {
		if (from.equals(to)) {
			return from;
		}
		return to + "&nbsp;(&lt;-&nbsp;" + from + ")";
	}
}
