package japicmp.output.stdout;

import japicmp.cli.CliParser;
import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.AccessModifier;
import japicmp.model.JApiClass;
import japicmp.util.*;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;

class StdoutOutputGeneratorTest {

	@Test
	void testNoChanges() {
		Options options = Options.newDefault();
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options, new ArrayList<>(0));
		String generated = generator.generate();
		MatcherAssert.assertThat(generated, containsString(StdoutOutputGenerator.NO_CHANGES));
	}

	@Test
	void testWarningWhenIgnoreMissingClasses() {
		Options options = Options.newDefault();
		options.setIgnoreMissingClasses(true);
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options, new ArrayList<>(0));
		String generated = generator.generate();
		MatcherAssert.assertThat(generated, containsString(StdoutOutputGenerator.WARNING));
		MatcherAssert.assertThat(generated, containsString(CliParser.IGNORE_MISSING_CLASSES));
	}

	@Test
	void testNoClassFileFormatVersionIfInterfaceRemoved() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass myInterface = CtInterfaceBuilder.create().name("MyInterface").addToClassPool(classPool);
				return Collections.singletonList(myInterface);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) {
				return Collections.emptyList();
			}
		});
		Options options = Options.newDefault();
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options, jApiClasses);
		String generated = generator.generate();
		Assertions.assertFalse(generated.contains("-1.-1"));
	}

	@Test
	void testMethodWithGenericTypes() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethod method = CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.util.List")).name("method").parameter(classPool.get("java.util.List")).body("return;").addToClass(ctClass);
				method.setGenericSignature("(Ljava/util/List<Ljava/lang/Integer;>;)Ljava/util/List<Ljava/lang/Byte;>;");
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethod method = CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.util.List")).name("method").parameter(classPool.get("java.util.List")).body("return;").addToClass(ctClass);
				method.setGenericSignature("(Ljava/util/List<Ljava/lang/Long;>;)Ljava/util/List<Ljava/lang/Short;>;");
				return Collections.singletonList(ctClass);
			}
		});
		Options options = Options.newDefault();
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options, jApiClasses);
		String generated = generator.generate();
		Assertions.assertTrue(generated.contains("===* UNCHANGED METHOD: PUBLIC java.util.List<java.lang.Short>(<- <java.lang.Byte>) method(java.util.List<java.lang.Long>(<- <java.lang.Integer>))"));
	}

	@Test
	void testMethodWithGenericTypesRemoved() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethod method = CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.util.List")).name("method").parameter(classPool.get("java.util.List")).body("return;").addToClass(ctClass);
				method.setGenericSignature("(Ljava/util/List<Ljava/lang/Integer;>;)Ljava/util/List<Ljava/lang/Byte;>;");
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		Options options = Options.newDefault();
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options, jApiClasses);
		String generated = generator.generate();
		Assertions.assertTrue(generated.contains("---! REMOVED METHOD: PUBLIC(-) java.util.List<java.lang.Byte> method(java.util.List<java.lang.Integer>)"));
	}

	@Test
	void testFieldWithGenericTypes() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtField ctField = CtFieldBuilder.create().type(classPool.get("java.util.List")).name("list").addToClass(ctClass);
				ctField.setGenericSignature("Ljava/util/List<Ljava/lang/Byte;>;");
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtField ctField = CtFieldBuilder.create().type(classPool.get("java.util.List")).name("list").addToClass(ctClass);
				ctField.setGenericSignature("Ljava/util/List<Ljava/lang/Short;>;");
				return Collections.singletonList(ctClass);
			}
		});
		Options options = Options.newDefault();
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options, jApiClasses);
		String generated = generator.generate();
		Assertions.assertTrue(generated.contains("===* UNCHANGED FIELD: PUBLIC java.util.List<java.lang.Short>(<- <java.lang.Byte>) list"));
	}

	@Test
	void testSummaryOnly() throws Exception {
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
		Options options = Options.newDefault();
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options, jApiClasses);
		String generated = generator.generate();
		Assertions.assertTrue(generated.contains("***! MODIFIED CLASS: PUBLIC japicmp.Test"));
		Assertions.assertTrue(generated.contains("===  CLASS FILE FORMAT VERSION"));
		Assertions.assertTrue(generated.contains("===  UNCHANGED SUPERCLASS"));
		Assertions.assertTrue(generated.contains("---! REMOVED FIELD"));
		Assertions.assertTrue(generated.contains("---! REMOVED CONSTRUCTOR"));
		Assertions.assertTrue(generated.contains("---! REMOVED METHOD"));
		Assertions.assertTrue(generated.contains("---  REMOVED ANNOTATION"));
		options.setReportOnlySummary(true);
		generated = generator.generate();
		Assertions.assertTrue(generated.contains("***! MODIFIED CLASS: PUBLIC japicmp.Test"));
		Assertions.assertFalse(generated.contains("===  CLASS FILE FORMAT VERSION"));
		Assertions.assertFalse(generated.contains("===  UNCHANGED SUPERCLASS"));
		Assertions.assertFalse(generated.contains("---! REMOVED FIELD"));
		Assertions.assertFalse(generated.contains("---! REMOVED CONSTRUCTOR"));
		Assertions.assertFalse(generated.contains("---! REMOVED METHOD"));
		Assertions.assertFalse(generated.contains("---  REMOVED ANNOTATION"));
	}
}
