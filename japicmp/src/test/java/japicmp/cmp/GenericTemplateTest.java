package japicmp.cmp;

import japicmp.config.Options;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiCompatibilityChange;
import japicmp.model.JApiMethod;
import japicmp.output.stdout.StdoutOutputGenerator;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static japicmp.util.Helper.getJApiClass;
import static japicmp.util.Helper.getJApiMethod;

public class GenericTemplateTest {

	@Test
	public void testClassWithTwoGenericTemplateParametersNew() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				ctClass.setGenericSignature("<T:Ljava/lang/Object;U:Ljava/lang/Short;>Ljava/lang/Object;");
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		Assert.assertEquals(2, jApiClass.getGenericTemplates().size());
		Assert.assertTrue(jApiClass.getCompatibilityChanges().stream().anyMatch(c -> c == JApiCompatibilityChange.CLASS_GENERIC_TEMPLATE_CHANGED));
		Assert.assertEquals(JApiChangeStatus.NEW, jApiClass.getGenericTemplates().get(0).getChangeStatus());
		Assert.assertEquals("T", jApiClass.getGenericTemplates().get(0).getName());
		Assert.assertEquals("java.lang.Object", jApiClass.getGenericTemplates().get(0).getNewType());
		Assert.assertEquals(JApiChangeStatus.NEW, jApiClass.getGenericTemplates().get(1).getChangeStatus());
		Assert.assertEquals("U", jApiClass.getGenericTemplates().get(1).getName());
		Assert.assertEquals("java.lang.Short", jApiClass.getGenericTemplates().get(1).getNewType());

		StdoutOutputGenerator generator = new StdoutOutputGenerator(Options.newDefault(), jApiClasses);
		String generated = generator.generate();
		Assert.assertTrue(generated.contains("GENERIC TEMPLATES: +++ T:java.lang.Object, +++ U:java.lang.Short"));
	}

	@Test
	public void testClassWithTwoGenericTemplateParametersRemoved() throws Exception {
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
		Assert.assertEquals(2, jApiClass.getGenericTemplates().size());
		Assert.assertTrue(jApiClass.getCompatibilityChanges().stream().anyMatch(c -> c == JApiCompatibilityChange.CLASS_GENERIC_TEMPLATE_CHANGED));
		Assert.assertEquals(JApiChangeStatus.REMOVED, jApiClass.getGenericTemplates().get(0).getChangeStatus());
		Assert.assertEquals("T", jApiClass.getGenericTemplates().get(0).getName());
		Assert.assertEquals("java.lang.Object", jApiClass.getGenericTemplates().get(0).getOldTypeOptional().get());
		Assert.assertEquals(JApiChangeStatus.REMOVED, jApiClass.getGenericTemplates().get(1).getChangeStatus());
		Assert.assertEquals("U", jApiClass.getGenericTemplates().get(1).getName());
		Assert.assertEquals("java.lang.Short", jApiClass.getGenericTemplates().get(1).getOldTypeOptional().get());

		StdoutOutputGenerator generator = new StdoutOutputGenerator(Options.newDefault(), jApiClasses);
		String generated = generator.generate();
		Assert.assertTrue(generated.contains("GENERIC TEMPLATES: --- T:java.lang.Object, --- U:java.lang.Short"));
	}

	@Test
	public void testClassWithTwoGenericTemplateParametersModified() throws Exception {
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
		Assert.assertEquals(2, jApiClass.getGenericTemplates().size());
		Assert.assertTrue(jApiClass.getCompatibilityChanges().stream().anyMatch(c -> c == JApiCompatibilityChange.CLASS_GENERIC_TEMPLATE_CHANGED));
		Assert.assertEquals(JApiChangeStatus.MODIFIED, jApiClass.getGenericTemplates().get(0).getChangeStatus());
		Assert.assertEquals("T", jApiClass.getGenericTemplates().get(0).getName());
		Assert.assertEquals("java.lang.Object", jApiClass.getGenericTemplates().get(0).getOldTypeOptional().get());
		Assert.assertEquals("java.lang.Integer", jApiClass.getGenericTemplates().get(0).getNewTypeOptional().get());
		Assert.assertEquals(JApiChangeStatus.REMOVED, jApiClass.getGenericTemplates().get(1).getChangeStatus());
		Assert.assertEquals("U", jApiClass.getGenericTemplates().get(1).getName());
		Assert.assertEquals("java.lang.Short", jApiClass.getGenericTemplates().get(1).getOldTypeOptional().get());

		StdoutOutputGenerator generator = new StdoutOutputGenerator(Options.newDefault(), jApiClasses);
		String generated = generator.generate();
		Assert.assertTrue(generated.contains("GENERIC TEMPLATES: *** T:java.lang.Integer (<-java.lang.Object), --- U:java.lang.Short"));
	}

	@Test
	public void testClassWithOneGenericTemplateParametersGenericsModified() throws Exception {
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
		Assert.assertEquals(1, jApiClass.getGenericTemplates().size());
		Assert.assertFalse(jApiClass.getCompatibilityChanges().stream().anyMatch(c -> c == JApiCompatibilityChange.CLASS_GENERIC_TEMPLATE_CHANGED));
		Assert.assertTrue(jApiClass.getCompatibilityChanges().stream().anyMatch(c -> c == JApiCompatibilityChange.CLASS_GENERIC_TEMPLATE_GENERICS_CHANGED));
		Assert.assertEquals(JApiChangeStatus.UNCHANGED, jApiClass.getGenericTemplates().get(0).getChangeStatus());
		Assert.assertEquals("T", jApiClass.getGenericTemplates().get(0).getName());
		Assert.assertEquals("java.util.List", jApiClass.getGenericTemplates().get(0).getOldTypeOptional().get());
		Assert.assertEquals("java.util.List", jApiClass.getGenericTemplates().get(0).getNewTypeOptional().get());
		Assert.assertEquals("java.lang.Integer", jApiClass.getGenericTemplates().get(0).getOldGenericTypes().get(0).getType());
		Assert.assertEquals("java.lang.Long", jApiClass.getGenericTemplates().get(0).getNewGenericTypes().get(0).getType());

		StdoutOutputGenerator generator = new StdoutOutputGenerator(Options.newDefault(), jApiClasses);
		String generated = generator.generate();
		Assert.assertTrue(generated.contains("GENERIC TEMPLATES: ===* T:java.util.List<java.lang.Long>(<- <java.lang.Integer>)"));
	}

	@Test
	public void testMethodWithOneGenericTemplateParametersAndTwoInterfacesModified() throws Exception {
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
		Assert.assertEquals(1, jApiMethod.getGenericTemplates().size());
		Assert.assertEquals("java.util.List", jApiMethod.getGenericTemplates().get(0).getNewTypeOptional().get());
		Assert.assertEquals("java.util.List", jApiMethod.getGenericTemplates().get(0).getOldTypeOptional().get());
		Assert.assertEquals(2, jApiMethod.getGenericTemplates().get(0).getOldInterfaceTypes().size());
		Assert.assertEquals("java.io.Serializable", jApiMethod.getGenericTemplates().get(0).getOldInterfaceTypes().get(0).getType());
		Assert.assertEquals("japicmp.test.Generics$MyInterface", jApiMethod.getGenericTemplates().get(0).getOldInterfaceTypes().get(1).getType());
		Assert.assertEquals(1, jApiMethod.getGenericTemplates().get(0).getNewInterfaceTypes().size());
		Assert.assertEquals("japicmp.test.Generics$MyInterface", jApiMethod.getGenericTemplates().get(0).getNewInterfaceTypes().get(0).getType());

		StdoutOutputGenerator generator = new StdoutOutputGenerator(Options.newDefault(), jApiClasses);
		String generated = generator.generate();
		Assert.assertTrue(generated.contains("GENERIC TEMPLATES: ===* X:java.util.List<java.lang.Long>(<- <java.lang.Integer>) & japicmp.test.Generics$MyInterface<java.lang.Integer> (<- & java.io.Serializable & japicmp.test.Generics$MyInterface<java.lang.Integer>)"));
	}

	@Test
	public void testClassWithOneGenericTemplateParametersAndTwoInterfacesModified() throws Exception {
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
		Assert.assertEquals(1, jApiClass.getGenericTemplates().size());
		Assert.assertEquals("java.util.List", jApiClass.getGenericTemplates().get(0).getNewTypeOptional().get());
		Assert.assertEquals("java.util.List", jApiClass.getGenericTemplates().get(0).getOldTypeOptional().get());
		Assert.assertEquals(2, jApiClass.getGenericTemplates().get(0).getOldInterfaceTypes().size());
		Assert.assertEquals("java.io.Serializable", jApiClass.getGenericTemplates().get(0).getOldInterfaceTypes().get(0).getType());
		Assert.assertEquals("japicmp.test.Generics$MyInterface", jApiClass.getGenericTemplates().get(0).getOldInterfaceTypes().get(1).getType());
		Assert.assertEquals(1, jApiClass.getGenericTemplates().get(0).getNewInterfaceTypes().size());
		Assert.assertEquals("japicmp.test.Generics$MyInterface", jApiClass.getGenericTemplates().get(0).getNewInterfaceTypes().get(0).getType());

		StdoutOutputGenerator generator = new StdoutOutputGenerator(Options.newDefault(), jApiClasses);
		String generated = generator.generate();
		Assert.assertTrue(generated.contains("GENERIC TEMPLATES: ===* X:java.util.List<java.lang.Long>(<- <java.lang.Integer>) & japicmp.test.Generics$MyInterface<java.lang.Integer> (<- & java.io.Serializable & japicmp.test.Generics$MyInterface<java.lang.Integer>)"));
	}
}
