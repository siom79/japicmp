package japicmp.output.xml;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiClassType;
import japicmp.util.Optional;
import javassist.CtClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class XmlOutputGeneratorTest {

	@Test
	public void testWithHtmlStylesheetOption() throws IOException {
		Path stylesheetPath = Paths.get(System.getProperty("user.dir"), "target", XmlOutputGeneratorTest.class.getSimpleName() + "_with.css");
		Path htmlReportPath = Paths.get(System.getProperty("user.dir"), "target", XmlOutputGeneratorTest.class.getSimpleName() + "_with.html");
		String stylesheetContent = "body {font-family: Monospace;}";
		Options options = Options.newDefault();
		options.setHtmlStylesheet(Optional.of(stylesheetPath.toString()));
		options.setHtmlOutputFile(Optional.of(htmlReportPath.toString()));
		Files.write(stylesheetPath, Collections.singletonList(stylesheetContent), Charset.forName("UTF-8"));
		generateHtmlReport(options);
		boolean foundStyleSheet = false;
		List<String> lines = Files.readAllLines(htmlReportPath, Charset.forName("UTF-8"));
		for (String line : lines) {
			if (line.contains(stylesheetContent)) {
				foundStyleSheet = true;
			}
		}
		assertThat(foundStyleSheet, is(true));
	}

	@Test
	public void testWithoutHtmlStylesheetOption() throws IOException {
		Path htmlReportPath = Paths.get(System.getProperty("user.dir"), "target", XmlOutputGeneratorTest.class.getSimpleName() + "_without.html");
		Options options = Options.newDefault();
		options.setHtmlOutputFile(Optional.of(htmlReportPath.toString()));
		generateHtmlReport(options);
		boolean foundStyleSheet = false;
		List<String> lines = Files.readAllLines(htmlReportPath, Charset.forName("UTF-8"));
		for (String line : lines) {
			if (line.contains("font-family: Verdana;")) {
				foundStyleSheet = true;
			}
		}
		assertThat(foundStyleSheet, is(true));
	}

	@Test(expected = JApiCmpException.class)
	public void testWithNotExistingHtmlStylesheetOption() throws IOException {
		Path stylesheetPath = Paths.get(System.getProperty("user.dir"), "target", XmlOutputGeneratorTest.class.getSimpleName() + "_not_existing.css");
		Path htmlReportPath = Paths.get(System.getProperty("user.dir"), "target", XmlOutputGeneratorTest.class.getSimpleName() + "_with.html");
		Options options = Options.newDefault();
		options.setHtmlStylesheet(Optional.of(stylesheetPath.toString()));
		options.setHtmlOutputFile(Optional.of(htmlReportPath.toString()));
		generateHtmlReport(options);
	}

	private void generateHtmlReport(Options options) {
		List<JApiClass> jApiClasses = new ArrayList<>();
		JarArchiveComparatorOptions jarArchiveComparatorOptions = JarArchiveComparatorOptions.of(options);
		JApiClassType classType = new JApiClassType(Optional.<JApiClassType.ClassType>absent(), Optional.<JApiClassType.ClassType>absent(), JApiChangeStatus.REMOVED);
		jApiClasses.add(new JApiClass(new JarArchiveComparator(jarArchiveComparatorOptions), "japicmp.Test", Optional.<CtClass>absent(), Optional.<CtClass>absent(), JApiChangeStatus.NEW, classType));
		XmlOutputGeneratorOptions xmlOutputGeneratorOptions = new XmlOutputGeneratorOptions();
		xmlOutputGeneratorOptions.setCreateSchemaFile(true);
		XmlOutputGenerator generator = new XmlOutputGenerator(jApiClasses, options, xmlOutputGeneratorOptions);
		XmlOutput xmlOutput = generator.generate();
		XmlOutputGenerator.writeToFiles(options, xmlOutput);
	}
}
