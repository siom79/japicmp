package japicmp.output.html;

import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.JApiClass;
import japicmp.util.*;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Evaluator;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;

public class HtmlOutputGeneratorTest {

	@Test
    public void testHtmlReport() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws CannotCompileException {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("toBeRemoved").returnType(CtClass.booleanType).addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws CannotCompileException, NotFoundException {
				CtClass newInterface = CtInterfaceBuilder.create().name("NewInterface").addToClassPool(classPool);
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).implementsInterface(newInterface).addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("newMethod").returnType(CtClass.booleanType).addToClass(ctClass);
				CtFieldBuilder.create().type(CtClass.booleanType).name("bField").addToClass(ctClass);
				CtConstructorBuilder.create().publicAccess().parameters(new CtClass[] {CtClass.intType, CtClass.booleanType}).exceptions(new CtClass[] {classPool.get("java.lang.Exception")}).addToClass(ctClass);
				return Arrays.asList(ctClass, superclass);
			}
		});
		Options reportOptions = Options.newDefault();
		reportOptions.setIgnoreMissingClasses(true);
		HtmlOutputGenerator generator = new HtmlOutputGenerator(jApiClasses, reportOptions, new HtmlOutputGeneratorOptions());

		HtmlOutput htmlOutput = generator.generate();

		Files.write(Paths.get(System.getProperty("user.dir"), "target", "report.html"), htmlOutput.getHtml().getBytes(StandardCharsets.UTF_8));
		Document document = Jsoup.parse(htmlOutput.getHtml());
		assertThat(document.select("#meta-accessmodifier-value").text(), is("PROTECTED"));
		assertThat(document.select("#warning-missingclasses").text(), containsString("WARNING"));
	}

	@Test
	public void testSummaryOnly() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withAnnotation("japicmp.Annotation").addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).name("foo").addToClass(ctClass);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("bar").addToClass(ctClass);
				CtConstructorBuilder.create().publicAccess().addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});

		Options reportOptions = Options.newDefault();
		reportOptions.setIgnoreMissingClasses(true);
		HtmlOutputGenerator generator = new HtmlOutputGenerator(jApiClasses, reportOptions, new HtmlOutputGeneratorOptions());

		HtmlOutput htmlOutput = generator.generate();

		Document document = Jsoup.parse(htmlOutput.getHtml());
		assertThat(document.select("#meta-accessmodifier-value").text(), is("PROTECTED"));
		assertThat(document.select("#warning-missingclasses").text(), containsString("WARNING"));

		String summary = document.select("#toc").text();
		assertThat(summary, containsString("MODIFIED"));
		assertThat(summary, containsString("japicmp.Test"));

		String details = document.select(new Evaluator.Id("japicmp.Test")).text();
		assertThat(details, containsString("FIELD_REMOVED"));
		assertThat(details, containsString("CONSTRUCTOR_REMOVED"));
		assertThat(details, containsString("METHOD_REMOVED"));
		assertThat(details, containsString("ANNOTATION_REMOVED"));

		reportOptions.setReportOnlySummary(true);
		htmlOutput = generator.generate();

		document = Jsoup.parse(htmlOutput.getHtml());
		assertThat(document.select("#meta-accessmodifier-value").text(), is("PROTECTED"));
		assertThat(document.select("#warning-missingclasses").text(), containsString("WARNING"));

		summary = document.select("#toc").text();
		assertThat(summary, containsString("MODIFIED"));
		assertThat(summary, containsString("japicmp.Test"));

		details = document.select(new Evaluator.Id("japicmp.Test")).text();
		assertThat(details, not(containsString("FIELD_REMOVED")));
		assertThat(details, not(containsString("CONSTRUCTOR_REMOVED")));
		assertThat(details, not(containsString("METHOD_REMOVED")));
		assertThat(details, not(containsString("ANNOTATION_REMOVED")));
	}
}
