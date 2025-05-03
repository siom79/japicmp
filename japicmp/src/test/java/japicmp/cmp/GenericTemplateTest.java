package japicmp.cmp;

import japicmp.config.Options;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiCompatibilityChangeType;
import japicmp.model.JApiMethod;
import japicmp.output.stdout.StdoutOutputGenerator;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static japicmp.util.Helper.getJApiClass;
import static japicmp.util.Helper.getJApiMethod;

class GenericTemplateTest {

	@Test
	void testClassWithTwoGenericTemplateParametersNew() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				ctClass.setGenericSignature("<T:Ljava/lang/Object;U:Ljava/lang/Short;>Ljava/lang/Object;");
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		Assertions.assertEquals(2, jApiClass.getGenericTemplates().size());
		Assertions.assertTrue(jApiClass.getCompatibilityChanges().stream().anyMatch(c -> c.getType() == JApiCompatibilityChangeType.CLASS_GENERIC_TEMPLATE_CHANGED));
		Assertions.assertEquals(JApiChangeStatus.NEW, jApiClass.getGenericTemplates().get(0).getChangeStatus());
		Assertions.assertEquals("T", jApiClass.getGenericTemplates().get(0).getName());
		Assertions.assertEquals("java.lang.Object", jApiClass.getGenericTemplates().get(0).getNewType());
		Assertions.assertEquals(JApiChangeStatus.NEW, jApiClass.getGenericTemplates().get(1).getChangeStatus());
		Assertions.assertEquals("U", jApiClass.getGenericTemplates().get(1).getName());
		Assertions.assertEquals("java.lang.Short", jApiClass.getGenericTemplates().get(1).getNewType());

		StdoutOutputGenerator generator = new StdoutOutputGenerator(Options.newDefault(), jApiClasses);
		String generated = generator.generate();
		Assertions.assertTrue(generated.contains("GENERIC TEMPLATES: +++ T:java.lang.Object, +++ U:java.lang.Short"));
	}

	@Test
	void testNewClassNotDetectedAsIncompatibility() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				return Collections.emptyList();
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				ctClass.setGenericSignature("<T:Ljava/lang/Object;U:Ljava/lang/Short;>Ljava/lang/Object;");
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		Assertions.assertEquals(2, jApiClass.getGenericTemplates().size());
		Assertions.assertTrue(jApiClass.getCompatibilityChanges().stream().noneMatch(c -> c.getType() == JApiCompatibilityChangeType.CLASS_GENERIC_TEMPLATE_CHANGED));
	}

	@Test
	void testClassWithTwoGenericTemplateParametersRemoved() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				ctClass.setGenericSignature("<T:Ljava/lang/Object;U:Ljava/lang/Short;>Ljava/lang/Object;");
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		Assertions.assertEquals(2, jApiClass.getGenericTemplates().size());
		Assertions.assertTrue(jApiClass.getCompatibilityChanges().stream().anyMatch(c -> c.getType() == JApiCompatibilityChangeType.CLASS_GENERIC_TEMPLATE_CHANGED));
		Assertions.assertEquals(JApiChangeStatus.REMOVED, jApiClass.getGenericTemplates().get(0).getChangeStatus());
		Assertions.assertEquals("T", jApiClass.getGenericTemplates().get(0).getName());
		Assertions.assertEquals("java.lang.Object", jApiClass.getGenericTemplates().get(0).getOldTypeOptional().get());
		Assertions.assertEquals(JApiChangeStatus.REMOVED, jApiClass.getGenericTemplates().get(1).getChangeStatus());
		Assertions.assertEquals("U", jApiClass.getGenericTemplates().get(1).getName());
		Assertions.assertEquals("java.lang.Short", jApiClass.getGenericTemplates().get(1).getOldTypeOptional().get());

		StdoutOutputGenerator generator = new StdoutOutputGenerator(Options.newDefault(), jApiClasses);
		String generated = generator.generate();
		Assertions.assertTrue(generated.contains("GENERIC TEMPLATES: --- T:java.lang.Object, --- U:java.lang.Short"));
	}

	@Test
	void testClassWithTwoGenericTemplateParametersModified() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				ctClass.setGenericSignature("<T:Ljava/lang/Object;U:Ljava/lang/Short;>Ljava/lang/Object;");
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				ctClass.setGenericSignature("<T:Ljava/lang/Integer;>Ljava/lang/Object;");
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		Assertions.assertEquals(2, jApiClass.getGenericTemplates().size());
		Assertions.assertTrue(jApiClass.getCompatibilityChanges().stream().anyMatch(c -> c.getType() == JApiCompatibilityChangeType.CLASS_GENERIC_TEMPLATE_CHANGED));
		Assertions.assertEquals(JApiChangeStatus.MODIFIED, jApiClass.getGenericTemplates().get(0).getChangeStatus());
		Assertions.assertEquals("T", jApiClass.getGenericTemplates().get(0).getName());
		Assertions.assertEquals("java.lang.Object", jApiClass.getGenericTemplates().get(0).getOldTypeOptional().get());
		Assertions.assertEquals("java.lang.Integer", jApiClass.getGenericTemplates().get(0).getNewTypeOptional().get());
		Assertions.assertEquals(JApiChangeStatus.REMOVED, jApiClass.getGenericTemplates().get(1).getChangeStatus());
		Assertions.assertEquals("U", jApiClass.getGenericTemplates().get(1).getName());
		Assertions.assertEquals("java.lang.Short", jApiClass.getGenericTemplates().get(1).getOldTypeOptional().get());

		StdoutOutputGenerator generator = new StdoutOutputGenerator(Options.newDefault(), jApiClasses);
		String generated = generator.generate();
		Assertions.assertTrue(generated.contains("GENERIC TEMPLATES: *** T:java.lang.Integer (<-java.lang.Object), --- U:java.lang.Short"));
	}

	@Test
	void testClassWithOneGenericTemplateParametersGenericsModified() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				ctClass.setGenericSignature("<T:Ljava/util/List<Ljava/lang/Integer;>;>Ljava/lang/Object;");
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				ctClass.setGenericSignature("<T:Ljava/util/List<Ljava/lang/Long;>;>Ljava/lang/Object;");
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		Assertions.assertEquals(1, jApiClass.getGenericTemplates().size());
		Assertions.assertFalse(jApiClass.getCompatibilityChanges().stream().anyMatch(c -> c.getType() == JApiCompatibilityChangeType.CLASS_GENERIC_TEMPLATE_CHANGED));
		Assertions.assertTrue(jApiClass.getCompatibilityChanges().stream().anyMatch(c -> c.getType() == JApiCompatibilityChangeType.CLASS_GENERIC_TEMPLATE_GENERICS_CHANGED));
		Assertions.assertEquals(JApiChangeStatus.UNCHANGED, jApiClass.getGenericTemplates().get(0).getChangeStatus());
		Assertions.assertEquals("T", jApiClass.getGenericTemplates().get(0).getName());
		Assertions.assertEquals("java.util.List", jApiClass.getGenericTemplates().get(0).getOldTypeOptional().get());
		Assertions.assertEquals("java.util.List", jApiClass.getGenericTemplates().get(0).getNewTypeOptional().get());
		Assertions.assertEquals("java.lang.Integer", jApiClass.getGenericTemplates().get(0).getOldGenericTypes().get(0).getType());
		Assertions.assertEquals("java.lang.Long", jApiClass.getGenericTemplates().get(0).getNewGenericTypes().get(0).getType());

		StdoutOutputGenerator generator = new StdoutOutputGenerator(Options.newDefault(), jApiClasses);
		String generated = generator.generate();
		Assertions.assertTrue(generated.contains("GENERIC TEMPLATES: ===* T:java.util.List<java.lang.Long>(<- <java.lang.Integer>)"));
	}

	@Test
	void testMethodWithOneGenericTemplateParametersAndTwoInterfacesModified() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethod ctMethod = CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.util.List")).name("method").parameter(classPool.get("java.util.List")).addToClass(ctClass);
				ctMethod.setGenericSignature("<X::Ljava/util/List<Ljava/lang/Integer;>;:Ljava/io/Serializable;:Ljapicmp/test/Generics$MyInterface<Ljava/lang/Integer;>;>(TX;)TX;");
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethod ctMethod = CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.util.List")).name("method").parameter(classPool.get("java.util.List")).addToClass(ctClass);
				ctMethod.setGenericSignature("<X::Ljava/util/List<Ljava/lang/Long;>;:Ljapicmp/test/Generics$MyInterface<Ljava/lang/Integer;>;>(TX;)TX;");
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		Assertions.assertEquals(1, jApiMethod.getGenericTemplates().size());
		Assertions.assertEquals("java.util.List", jApiMethod.getGenericTemplates().get(0).getNewTypeOptional().get());
		Assertions.assertEquals("java.util.List", jApiMethod.getGenericTemplates().get(0).getOldTypeOptional().get());
		Assertions.assertEquals(2, jApiMethod.getGenericTemplates().get(0).getOldInterfaceTypes().size());
		Assertions.assertEquals("java.io.Serializable", jApiMethod.getGenericTemplates().get(0).getOldInterfaceTypes().get(0).getType());
		Assertions.assertEquals("japicmp.test.Generics$MyInterface", jApiMethod.getGenericTemplates().get(0).getOldInterfaceTypes().get(1).getType());
		Assertions.assertEquals(1, jApiMethod.getGenericTemplates().get(0).getNewInterfaceTypes().size());
		Assertions.assertEquals("japicmp.test.Generics$MyInterface", jApiMethod.getGenericTemplates().get(0).getNewInterfaceTypes().get(0).getType());

		StdoutOutputGenerator generator = new StdoutOutputGenerator(Options.newDefault(), jApiClasses);
		String generated = generator.generate();
		Assertions.assertTrue(generated.contains("GENERIC TEMPLATES: ===* X:java.util.List<java.lang.Long>(<- <java.lang.Integer>) & japicmp.test.Generics$MyInterface<java.lang.Integer> (<- & java.io.Serializable & japicmp.test.Generics$MyInterface<java.lang.Integer>)"));
	}

	@Test
	void testClassWithOneGenericTemplateParametersAndTwoInterfacesModified() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				ctClass.setGenericSignature("<X::Ljava/util/List<Ljava/lang/Integer;>;:Ljava/io/Serializable;:Ljapicmp/test/Generics$MyInterface<Ljava/lang/Integer;>;>Ljava/lang/Object;");
				CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.util.List")).name("method").parameter(classPool.get("java.util.List")).addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				ctClass.setGenericSignature("<X::Ljava/util/List<Ljava/lang/Long;>;:Ljapicmp/test/Generics$MyInterface<Ljava/lang/Integer;>;>Ljava/lang/Object;");
				CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.util.List")).name("method").parameter(classPool.get("java.util.List")).addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		Assertions.assertEquals(1, jApiClass.getGenericTemplates().size());
		Assertions.assertEquals("java.util.List", jApiClass.getGenericTemplates().get(0).getNewTypeOptional().get());
		Assertions.assertEquals("java.util.List", jApiClass.getGenericTemplates().get(0).getOldTypeOptional().get());
		Assertions.assertEquals(2, jApiClass.getGenericTemplates().get(0).getOldInterfaceTypes().size());
		Assertions.assertEquals("java.io.Serializable", jApiClass.getGenericTemplates().get(0).getOldInterfaceTypes().get(0).getType());
		Assertions.assertEquals("japicmp.test.Generics$MyInterface", jApiClass.getGenericTemplates().get(0).getOldInterfaceTypes().get(1).getType());
		Assertions.assertEquals(1, jApiClass.getGenericTemplates().get(0).getNewInterfaceTypes().size());
		Assertions.assertEquals("japicmp.test.Generics$MyInterface", jApiClass.getGenericTemplates().get(0).getNewInterfaceTypes().get(0).getType());

		StdoutOutputGenerator generator = new StdoutOutputGenerator(Options.newDefault(), jApiClasses);
		String generated = generator.generate();
		Assertions.assertTrue(generated.contains("GENERIC TEMPLATES: ===* X:java.util.List<java.lang.Long>(<- <java.lang.Integer>) & japicmp.test.Generics$MyInterface<java.lang.Integer> (<- & java.io.Serializable & japicmp.test.Generics$MyInterface<java.lang.Integer>)"));
	}
}
