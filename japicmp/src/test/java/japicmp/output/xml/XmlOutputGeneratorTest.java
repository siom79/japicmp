package japicmp.output.xml;

import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.JApiClass;
import japicmp.util.*;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class XmlOutputGeneratorTest {

	@Test
	void testXmlReport() throws Exception {
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
		reportOptions.setXmlOutputFile(Optional.of(Paths.get(System.getProperty("user.dir"), "target", "report.xml").toString()));
		XmlOutputGenerator generator = new XmlOutputGenerator(jApiClasses, reportOptions, new XmlOutputGeneratorOptions());

		XmlOutput xmlOutput = generator.generate();

		Files.write(Paths.get(System.getProperty("user.dir"), "target", "report.xml"), xmlOutput.getXmlOutputStream().get().toString().getBytes(StandardCharsets.UTF_8));
	}
}
