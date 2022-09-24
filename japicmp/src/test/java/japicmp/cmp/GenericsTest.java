package japicmp.cmp;

import japicmp.model.*;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtConstructorBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static japicmp.util.Helper.*;

public class GenericsTest {

	@Test
	public void testMethodWithGenericReturnTypeChanged() throws Exception {
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
		Assert.assertEquals(1, jApiClass.getMethods().size());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		Assert.assertEquals(JApiChangeStatus.UNCHANGED, jApiMethod.getChangeStatus());
		JApiReturnType returnType = jApiMethod.getReturnType();
		Assert.assertEquals(JApiChangeStatus.UNCHANGED, returnType.getChangeStatus());
		Assert.assertTrue(returnType.getCompatibilityChanges().contains(JApiCompatibilityChange.METHOD_RETURN_TYPE_GENERICS_CHANGED));
		Assert.assertEquals(1, returnType.getOldGenericTypes().size());
		Assert.assertTrue(returnType.getOldGenericTypes().stream().anyMatch(gt -> gt.getType().equals("java.lang.Object")));
		Assert.assertEquals(1, returnType.getNewGenericTypes().size());
		Assert.assertTrue(returnType.getNewGenericTypes().stream().anyMatch(gt -> gt.getType().equals("java.lang.Integer")));
	}

	@Test
	public void testMethodWithGenericArgChanged() throws Exception {
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
		Assert.assertEquals(1, jApiClass.getMethods().size());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		Assert.assertEquals(JApiChangeStatus.UNCHANGED, jApiMethod.getChangeStatus());
		Assert.assertEquals(1, jApiMethod.getParameters().size());
		JApiParameter firstParam = jApiMethod.getParameters().get(0);
		Assert.assertEquals(JApiChangeStatus.UNCHANGED, firstParam.getChangeStatus());
		Assert.assertTrue(firstParam.getCompatibilityChanges().contains(JApiCompatibilityChange.METHOD_PARAMETER_GENERICS_CHANGED));
		Assert.assertEquals(1, firstParam.getOldGenericTypes().size());
		Assert.assertTrue(firstParam.getOldGenericTypes().stream().anyMatch(gt -> gt.getType().equals("java.lang.Integer")));
		Assert.assertEquals(1, firstParam.getNewGenericTypes().size());
		Assert.assertTrue(firstParam.getNewGenericTypes().stream().anyMatch(gt -> gt.getType().equals("java.lang.Long")));
	}

	@Test
	public void testConstructorWithGenericArgChanged() throws Exception {
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
		Assert.assertEquals(1, jApiClass.getConstructors().size());
		JApiConstructor jApiConstructor = getJApiConstructor(jApiClass.getConstructors(), Collections.singletonList("java.util.List"));
		Assert.assertEquals(JApiChangeStatus.UNCHANGED, jApiConstructor.getChangeStatus());
		Assert.assertEquals(1, jApiConstructor.getParameters().size());
		JApiParameter firstParam = jApiConstructor.getParameters().get(0);
		Assert.assertEquals(JApiChangeStatus.UNCHANGED, firstParam.getChangeStatus());
		Assert.assertTrue(firstParam.getCompatibilityChanges().contains(JApiCompatibilityChange.METHOD_PARAMETER_GENERICS_CHANGED));
		Assert.assertEquals(1, firstParam.getOldGenericTypes().size());
		Assert.assertTrue(firstParam.getOldGenericTypes().stream().anyMatch(gt -> gt.getType().equals("java.lang.Integer")));
		Assert.assertEquals(1, firstParam.getNewGenericTypes().size());
		Assert.assertTrue(firstParam.getNewGenericTypes().stream().anyMatch(gt -> gt.getType().equals("java.lang.Long")));
	}

	@Test
	public void testMethodWithGenericTemplateUnChanged() throws Exception {
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
		Assert.assertEquals(1, jApiClass.getMethods().size());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		Assert.assertEquals(JApiChangeStatus.UNCHANGED, jApiMethod.getChangeStatus());
		Assert.assertEquals(2, jApiMethod.getParameters().size());
		JApiParameter firstParam = jApiMethod.getParameters().get(0);
		Assert.assertEquals(JApiChangeStatus.UNCHANGED, firstParam.getChangeStatus());
	}

	@Test
	public void testMethodWithGenericTemplateClassChanged() throws Exception {
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
		Assert.assertEquals(1, jApiClass.getMethods().size());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		Assert.assertEquals(JApiChangeStatus.UNCHANGED, jApiMethod.getChangeStatus());
		Assert.assertEquals(1, jApiMethod.getParameters().size());
		JApiParameter firstParam = jApiMethod.getParameters().get(0);
		Assert.assertEquals(JApiChangeStatus.UNCHANGED, firstParam.getChangeStatus());
		Assert.assertTrue(firstParam.getCompatibilityChanges().contains(JApiCompatibilityChange.METHOD_PARAMETER_GENERICS_CHANGED));
		Assert.assertEquals("java.lang.Integer", firstParam.getOldGenericTypes().get(0).getType());
		Assert.assertEquals("java.lang.Long", firstParam.getNewGenericTypes().get(0).getType());
		Assert.assertEquals("T", firstParam.getTemplateName());
	}

	@Test
	public void testMethodWithGenericTemplateMethodChanged() throws Exception {
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
		Assert.assertEquals(1, jApiClass.getMethods().size());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		Assert.assertEquals(JApiChangeStatus.UNCHANGED, jApiMethod.getChangeStatus());
		Assert.assertEquals(1, jApiMethod.getParameters().size());
		JApiParameter firstParam = jApiMethod.getParameters().get(0);
		Assert.assertEquals(JApiChangeStatus.UNCHANGED, firstParam.getChangeStatus());
		Assert.assertTrue(firstParam.getCompatibilityChanges().contains(JApiCompatibilityChange.METHOD_PARAMETER_GENERICS_CHANGED));
		Assert.assertEquals("java.lang.Integer", firstParam.getOldGenericTypes().get(0).getType());
		Assert.assertEquals("java.lang.Long", firstParam.getNewGenericTypes().get(0).getType());
		Assert.assertEquals("TEST", firstParam.getTemplateName());
	}
}
