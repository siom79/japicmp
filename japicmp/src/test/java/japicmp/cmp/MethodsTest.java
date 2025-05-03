package japicmp.cmp;

import japicmp.model.*;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static japicmp.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;

class MethodsTest {

	@Test
	void testClassWithTwoMethodsWithSameSignatureButDifferentReturnTypeUnchanged() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("get").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("get").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		MatcherAssert.assertThat(jApiClass.getMethods().size(), is(2));
	}

	@Test
	void testClassWithTwoMethodsWithSameSignatureButDifferentReturnTypeTwoNewMethods() throws Exception {
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
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("get").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		MatcherAssert.assertThat(jApiClass.getMethods().size(), is(2));
		for (JApiMethod jApiMethod : jApiClass.getMethods()) {
			MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
		}
	}

	@Test
	void testClassWithTwoMethodsWithSameSignatureButDifferentReturnTypeOneNewMethod() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("get").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		MatcherAssert.assertThat(jApiClass.getMethods().size(), is(2));
		boolean intReturnFound = false;
		boolean booleanReturnFound = false;
		for (JApiMethod jApiMethod : jApiClass.getMethods()) {
			JApiReturnType returnType = jApiMethod.getReturnType();
			if (Objects.equals(returnType.getNewReturnType(), "int") && Objects.equals(returnType.getOldReturnType(), "int")) {
				intReturnFound = true;
				MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
			}
			if (Objects.equals(returnType.getNewReturnType(), "boolean")) {
				booleanReturnFound = true;
				MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
			}
		}
		MatcherAssert.assertThat(intReturnFound, is(true));
		MatcherAssert.assertThat(booleanReturnFound, is(true));
	}

	@Test
	void testClassWithTwoMethodsWithSameSignatureButDifferentReturnTypeOneRemovedMethod() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("get").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		MatcherAssert.assertThat(jApiClass.getMethods().size(), is(2));
		boolean intReturnFound = false;
		boolean booleanReturnFound = false;
		for (JApiMethod jApiMethod : jApiClass.getMethods()) {
			JApiReturnType returnType = jApiMethod.getReturnType();
			if (Objects.equals(returnType.getNewReturnType(), "int") && Objects.equals(returnType.getOldReturnType(), "int")) {
				intReturnFound = true;
				MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
			}
			if (Objects.equals(returnType.getOldReturnType(), "boolean")) {
				booleanReturnFound = true;
				MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.REMOVED));
			}
		}
		MatcherAssert.assertThat(intReturnFound, is(true));
		MatcherAssert.assertThat(booleanReturnFound, is(true));
	}

	@Test
	void testClassWithTwoMethodsWithSameSignatureButDifferentReturnTypeTwoRemovedMethods() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("get").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		MatcherAssert.assertThat(jApiClass.getMethods().size(), is(2));
		for (JApiMethod jApiMethod : jApiClass.getMethods()) {
			MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.REMOVED));
		}
	}

	@Test
	void testClassWithTwoMethodsWithSameSignatureButDifferentReturnTypeOneModifiedMethod() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("get").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.doubleType).name("get").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		MatcherAssert.assertThat(jApiClass.getMethods().size(), is(2));
		boolean intReturnFound = false;
		boolean changedReturnFound = false;
		for (JApiMethod jApiMethod : jApiClass.getMethods()) {
			JApiReturnType returnType = jApiMethod.getReturnType();
			if (Objects.equals(returnType.getNewReturnType(), "int") && Objects.equals(returnType.getOldReturnType(), "int")) {
				intReturnFound = true;
				MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
			}
			if (Objects.equals(returnType.getNewReturnType(), "double") && Objects.equals(returnType.getOldReturnType(), "boolean")) {
				changedReturnFound = true;
				MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
			}
		}
		MatcherAssert.assertThat(intReturnFound, is(true));
		MatcherAssert.assertThat(changedReturnFound, is(true));
	}

	@Test
	void testClassOneMethodAdded() throws Exception {
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
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		MatcherAssert.assertThat(jApiClass.getMethods().size(), is(1));
		JApiMethod jApiMethod = jApiClass.getMethods().get(0);
		MatcherAssert.assertThat(jApiMethod.getReturnType().getChangeStatus(), is(JApiChangeStatus.NEW));
		MatcherAssert.assertThat(jApiMethod.getReturnType().getNewReturnType(), is("int"));
		MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
		MatcherAssert.assertThat(jApiMethod.getAccessModifier().getNewModifier().get(), is(AccessModifier.PUBLIC));
	}

	@Test
	void testClassOneMethodRemoved() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		MatcherAssert.assertThat(jApiClass.getMethods().size(), is(1));
		JApiMethod jApiMethod = jApiClass.getMethods().get(0);
		MatcherAssert.assertThat(jApiMethod.getReturnType().getChangeStatus(), is(JApiChangeStatus.REMOVED));
		MatcherAssert.assertThat(jApiMethod.getReturnType().getOldReturnType(), is("int"));
		MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.REMOVED));
		MatcherAssert.assertThat(jApiMethod.getAccessModifier().getOldModifier().get(), is(AccessModifier.PUBLIC));
	}

	@Test
	void testClassOneMethodReturnTypeChanged() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("get").body("return false;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		MatcherAssert.assertThat(jApiClass.getMethods().size(), is(1));
		JApiMethod jApiMethod = jApiClass.getMethods().get(0);
		MatcherAssert.assertThat(jApiMethod.getReturnType().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		MatcherAssert.assertThat(jApiMethod.getReturnType().getOldReturnType(), is("int"));
		MatcherAssert.assertThat(jApiMethod.getReturnType().getNewReturnType(), is("boolean"));
		MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		MatcherAssert.assertThat(jApiMethod.getAccessModifier().getOldModifier().get(), is(AccessModifier.PUBLIC));
	}

	@Test
	void testClassOneMethodAccessModifierChanged() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().privateAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		MatcherAssert.assertThat(jApiClass.getMethods().size(), is(1));
		JApiMethod jApiMethod = jApiClass.getMethods().get(0);
		MatcherAssert.assertThat(jApiMethod.getReturnType().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		MatcherAssert.assertThat(jApiMethod.getReturnType().getOldReturnType(), is("int"));
		MatcherAssert.assertThat(jApiMethod.getReturnType().getNewReturnType(), is("int"));
		MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		MatcherAssert.assertThat(jApiMethod.getAccessModifier().getOldModifier().get(), is(AccessModifier.PUBLIC));
		MatcherAssert.assertThat(jApiMethod.getAccessModifier().getNewModifier().get(), is(AccessModifier.PRIVATE));
		MatcherAssert.assertThat(jApiMethod.getAccessModifier().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
	}

	@Test
	void testClassOneMethodUnchanged() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		MatcherAssert.assertThat(jApiClass.getMethods().size(), is(1));
		JApiMethod jApiMethod = jApiClass.getMethods().get(0);
		MatcherAssert.assertThat(jApiMethod.getReturnType().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		MatcherAssert.assertThat(jApiMethod.getReturnType().getOldReturnType(), is("int"));
		MatcherAssert.assertThat(jApiMethod.getReturnType().getNewReturnType(), is("int"));
		MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		MatcherAssert.assertThat(jApiMethod.getAccessModifier().getOldModifier().get(), is(AccessModifier.PUBLIC));
	}

	@Test
	void testClassWithTwoMethodsOneAddedWithAdditionalParameter() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").body("return 42;").addToClass(ctClass);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("get").parameter(CtClass.intType).body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		MatcherAssert.assertThat(jApiClass.getMethods().size(), is(2));
		boolean noParamFound = false;
		boolean oneParamFound = false;
		for (JApiMethod jApiMethod : jApiClass.getMethods()) {
			if (jApiMethod.getParameters().size() == 0) {
				noParamFound = true;
				MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
			}
			if (jApiMethod.getParameters().size() == 1) {
				oneParamFound = true;
				MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
			}
		}
		MatcherAssert.assertThat(noParamFound, is(true));
		MatcherAssert.assertThat(oneParamFound, is(true));
	}
}
