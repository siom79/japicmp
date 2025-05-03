package japicmp.cmp;

import japicmp.model.*;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtConstructorBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static japicmp.util.Helper.*;

class GenericsTest {

	@Test
	void testMethodWithGenericReturnTypeChanged() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethod method = CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.util.List")).name("method").body("return new java.util.ArrayList();").addToClass(ctClass);
				method.setGenericSignature("()Ljava/util/List<Ljava/lang/Object;>;");
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethod method = CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.util.List")).name("method").body("return new java.util.ArrayList();").addToClass(ctClass);
				method.setGenericSignature("()Ljava/util/List<Ljava/lang/Integer;>;");
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		Assertions.assertEquals(1, jApiClass.getMethods().size());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		Assertions.assertEquals(JApiChangeStatus.UNCHANGED, jApiMethod.getChangeStatus());
		JApiReturnType returnType = jApiMethod.getReturnType();
		Assertions.assertEquals(JApiChangeStatus.UNCHANGED, returnType.getChangeStatus());
		Assertions.assertTrue(returnType.getCompatibilityChanges().contains(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_RETURN_TYPE_GENERICS_CHANGED)));
		Assertions.assertEquals(1, returnType.getOldGenericTypes().size());
		Assertions.assertTrue(returnType.getOldGenericTypes().stream().anyMatch(gt -> gt.getType().equals("java.lang.Object")));
		Assertions.assertEquals(1, returnType.getNewGenericTypes().size());
		Assertions.assertTrue(returnType.getNewGenericTypes().stream().anyMatch(gt -> gt.getType().equals("java.lang.Integer")));
	}

	@Test
	void testMethodWithGenericArgChanged() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethod method = CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("method").parameter(classPool.get("java.util.List")).body("return;").addToClass(ctClass);
				method.setGenericSignature("(Ljava/util/List<Ljava/lang/Integer;>;)V");
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethod method = CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("method").parameter(classPool.get("java.util.List")).body("return;").addToClass(ctClass);
				method.setGenericSignature("(Ljava/util/List<Ljava/lang/Long;>;)V");
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		Assertions.assertEquals(1, jApiClass.getMethods().size());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		Assertions.assertEquals(JApiChangeStatus.UNCHANGED, jApiMethod.getChangeStatus());
		Assertions.assertEquals(1, jApiMethod.getParameters().size());
		JApiParameter firstParam = jApiMethod.getParameters().get(0);
		Assertions.assertEquals(JApiChangeStatus.UNCHANGED, firstParam.getChangeStatus());
		Assertions.assertTrue(firstParam.getCompatibilityChanges().contains(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_PARAMETER_GENERICS_CHANGED)));
		Assertions.assertEquals(1, firstParam.getOldGenericTypes().size());
		Assertions.assertTrue(firstParam.getOldGenericTypes().stream().anyMatch(gt -> gt.getType().equals("java.lang.Integer")));
		Assertions.assertEquals(1, firstParam.getNewGenericTypes().size());
		Assertions.assertTrue(firstParam.getNewGenericTypes().stream().anyMatch(gt -> gt.getType().equals("java.lang.Long")));
	}

	@Test
	void testConstructorWithGenericArgChanged() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtConstructor ctConstructor = CtConstructorBuilder.create().publicAccess().parameter(classPool.get("java.util.List")).body("return;").addToClass(ctClass);
				ctConstructor.setGenericSignature("(Ljava/util/List<Ljava/lang/Integer;>;)V");
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtConstructor ctConstructor = CtConstructorBuilder.create().publicAccess().parameter(classPool.get("java.util.List")).body("return;").addToClass(ctClass);
				ctConstructor.setGenericSignature("(Ljava/util/List<Ljava/lang/Long;>;)V");
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		Assertions.assertEquals(1, jApiClass.getConstructors().size());
		JApiConstructor jApiConstructor = getJApiConstructor(jApiClass.getConstructors(), Collections.singletonList("java.util.List"));
		Assertions.assertEquals(JApiChangeStatus.UNCHANGED, jApiConstructor.getChangeStatus());
		Assertions.assertEquals(1, jApiConstructor.getParameters().size());
		JApiParameter firstParam = jApiConstructor.getParameters().get(0);
		Assertions.assertEquals(JApiChangeStatus.UNCHANGED, firstParam.getChangeStatus());
		Assertions.assertTrue(firstParam.getCompatibilityChanges().contains(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_PARAMETER_GENERICS_CHANGED)));
		Assertions.assertEquals(1, firstParam.getOldGenericTypes().size());
		Assertions.assertTrue(firstParam.getOldGenericTypes().stream().anyMatch(gt -> gt.getType().equals("java.lang.Integer")));
		Assertions.assertEquals(1, firstParam.getNewGenericTypes().size());
		Assertions.assertTrue(firstParam.getNewGenericTypes().stream().anyMatch(gt -> gt.getType().equals("java.lang.Long")));
	}

	@Test
	void testMethodWithGenericTemplateUnChanged() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				ctClass.setGenericSignature("<T:Ljava/lang/Integer;>Ljava/lang/Object;");
				CtMethod method = CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.lang.Short")).name("method").parameter(classPool.get("java.lang.Integer")).parameter(classPool.get("java.lang.Short")).body("return;").addToClass(ctClass);
				method.setGenericSignature("<TEST:Ljava/lang/Integer;U:Ljava/lang/Short;>(TTEST;TU;)TT;");
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				ctClass.setGenericSignature("<T:Ljava/lang/Integer;>Ljava/lang/Object;");
				CtMethod method = CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.lang.Short")).name("method").parameter(classPool.get("java.lang.Integer")).parameter(classPool.get("java.lang.Short")).body("return;").addToClass(ctClass);
				method.setGenericSignature("<TEST:Ljava/lang/Integer;U:Ljava/lang/Short;>(TTEST;TU;)TT;");
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		Assertions.assertEquals(1, jApiClass.getMethods().size());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		Assertions.assertEquals(JApiChangeStatus.UNCHANGED, jApiMethod.getChangeStatus());
		Assertions.assertEquals(2, jApiMethod.getParameters().size());
		JApiParameter firstParam = jApiMethod.getParameters().get(0);
		Assertions.assertEquals(JApiChangeStatus.UNCHANGED, firstParam.getChangeStatus());
	}

	@Test
	void testMethodWithGenericTemplateClassChanged() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				ctClass.setGenericSignature("<T:Ljava/util/List<Ljava/lang/Integer;>;>Ljava/lang/Object;");
				CtMethod method = CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.lang.Short")).name("method").parameter(classPool.get("java.util.List")).body("return;").addToClass(ctClass);
				method.setGenericSignature("(TT;)V");
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				ctClass.setGenericSignature("<T:Ljava/util/List<Ljava/lang/Long;>;>Ljava/lang/Object;");
				CtMethod method = CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.lang.Short")).name("method").parameter(classPool.get("java.util.List")).body("return;").addToClass(ctClass);
				method.setGenericSignature("(TT;)V");
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		Assertions.assertEquals(1, jApiClass.getMethods().size());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		Assertions.assertEquals(JApiChangeStatus.UNCHANGED, jApiMethod.getChangeStatus());
		Assertions.assertEquals(1, jApiMethod.getParameters().size());
		JApiParameter firstParam = jApiMethod.getParameters().get(0);
		Assertions.assertEquals(JApiChangeStatus.UNCHANGED, firstParam.getChangeStatus());
		Assertions.assertTrue(firstParam.getCompatibilityChanges().contains(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_PARAMETER_GENERICS_CHANGED)));
		Assertions.assertEquals("java.lang.Integer", firstParam.getOldGenericTypes().get(0).getType());
		Assertions.assertEquals("java.lang.Long", firstParam.getNewGenericTypes().get(0).getType());
		Assertions.assertEquals("T", firstParam.getTemplateName());
	}

	@Test
	void testMethodWithGenericTemplateMethodChanged() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethod method = CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.lang.Short")).name("method").parameter(classPool.get("java.util.List")).body("return;").addToClass(ctClass);
				method.setGenericSignature("<TEST:Ljava/util/List<Ljava/lang/Integer;>;>(TTEST;)V");
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethod method = CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.lang.Short")).name("method").parameter(classPool.get("java.util.List")).body("return;").addToClass(ctClass);
				method.setGenericSignature("<TEST:Ljava/util/List<Ljava/lang/Long;>;>(TTEST;)V");
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		Assertions.assertEquals(1, jApiClass.getMethods().size());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		Assertions.assertEquals(JApiChangeStatus.UNCHANGED, jApiMethod.getChangeStatus());
		Assertions.assertEquals(1, jApiMethod.getParameters().size());
		JApiParameter firstParam = jApiMethod.getParameters().get(0);
		Assertions.assertEquals(JApiChangeStatus.UNCHANGED, firstParam.getChangeStatus());
		Assertions.assertTrue(firstParam.getCompatibilityChanges().contains(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_PARAMETER_GENERICS_CHANGED)));
		Assertions.assertEquals("java.lang.Integer", firstParam.getOldGenericTypes().get(0).getType());
		Assertions.assertEquals("java.lang.Long", firstParam.getNewGenericTypes().get(0).getType());
		Assertions.assertEquals("TEST", firstParam.getTemplateName());
	}
}
