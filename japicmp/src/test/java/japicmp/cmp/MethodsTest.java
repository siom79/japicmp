package japicmp.cmp;

import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;
import japicmp.model.JApiReturnType;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static japicmp.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MethodsTest {

	@Test
	public void testClassWithTwoMethodsWithSameSignatureButDifferentReturnTypeUnchanged() throws Exception {
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
		assertThat(jApiClass.getMethods().size(), is(2));
	}

	@Test
	public void testClassWithTwoMethodsWithSameSignatureButDifferentReturnTypeTwoNewMethods() throws Exception {
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
		assertThat(jApiClass.getMethods().size(), is(2));
		for (JApiMethod jApiMethod : jApiClass.getMethods()) {
			assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
		}
	}

	@Test
	public void testClassWithTwoMethodsWithSameSignatureButDifferentReturnTypeOneNewMethod() throws Exception {
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
		assertThat(jApiClass.getMethods().size(), is(2));
		boolean intReturnFound = false;
		boolean booleanReturnFound = false;
		for (JApiMethod jApiMethod : jApiClass.getMethods()) {
			JApiReturnType returnType = jApiMethod.getReturnType();
			if (Objects.equals(returnType.getNewReturnType(), "int") && Objects.equals(returnType.getOldReturnType(), "int")) {
				intReturnFound = true;
				assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
			}
			if (Objects.equals(returnType.getNewReturnType(), "boolean")) {
				booleanReturnFound = true;
				assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
			}
		}
		assertThat(intReturnFound, is(true));
		assertThat(booleanReturnFound, is(true));
	}

	@Test
	public void testClassWithTwoMethodsWithSameSignatureButDifferentReturnTypeOneRemovedMethod() throws Exception {
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
		assertThat(jApiClass.getMethods().size(), is(2));
		boolean intReturnFound = false;
		boolean booleanReturnFound = false;
		for (JApiMethod jApiMethod : jApiClass.getMethods()) {
			JApiReturnType returnType = jApiMethod.getReturnType();
			if (Objects.equals(returnType.getNewReturnType(), "int") && Objects.equals(returnType.getOldReturnType(), "int")) {
				intReturnFound = true;
				assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
			}
			if (Objects.equals(returnType.getOldReturnType(), "boolean")) {
				booleanReturnFound = true;
				assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.REMOVED));
			}
		}
		assertThat(intReturnFound, is(true));
		assertThat(booleanReturnFound, is(true));
	}

	@Test
	public void testClassWithTwoMethodsWithSameSignatureButDifferentReturnTypeTwoRemovedMethods() throws Exception {
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
		assertThat(jApiClass.getMethods().size(), is(2));
		for (JApiMethod jApiMethod : jApiClass.getMethods()) {
			assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.REMOVED));
		}
	}

	@Test
	public void testClassWithTwoMethodsWithSameSignatureButDifferentReturnTypeOneModifiedMethod() throws Exception {
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
		assertThat(jApiClass.getMethods().size(), is(2));
		boolean intReturnFound = false;
		boolean changedReturnFound = false;
		for (JApiMethod jApiMethod : jApiClass.getMethods()) {
			JApiReturnType returnType = jApiMethod.getReturnType();
			if (Objects.equals(returnType.getNewReturnType(), "int") && Objects.equals(returnType.getOldReturnType(), "int")) {
				intReturnFound = true;
				assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
			}
			if (Objects.equals(returnType.getNewReturnType(), "double") && Objects.equals(returnType.getOldReturnType(), "boolean")) {
				changedReturnFound = true;
				assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
			}
		}
		assertThat(intReturnFound, is(true));
		assertThat(changedReturnFound, is(true));
	}

	@Test
	public void testClassOneMethodAdded() throws Exception {
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
		assertThat(jApiClass.getMethods().size(), is(1));
		JApiMethod jApiMethod = jApiClass.getMethods().get(0);
		assertThat(jApiMethod.getReturnType().getChangeStatus(), is(JApiChangeStatus.NEW));
		assertThat(jApiMethod.getReturnType().getNewReturnType(), is("int"));
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
		assertThat(jApiMethod.getAccessModifier().getNewModifier().get(), is(AccessModifier.PUBLIC));
	}

	@Test
	public void testClassOneMethodRemoved() throws Exception {
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
		assertThat(jApiClass.getMethods().size(), is(1));
		JApiMethod jApiMethod = jApiClass.getMethods().get(0);
		assertThat(jApiMethod.getReturnType().getChangeStatus(), is(JApiChangeStatus.REMOVED));
		assertThat(jApiMethod.getReturnType().getOldReturnType(), is("int"));
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.REMOVED));
		assertThat(jApiMethod.getAccessModifier().getOldModifier().get(), is(AccessModifier.PUBLIC));
	}

	@Test
	public void testClassOneMethodReturnTypeChanged() throws Exception {
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
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("get").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getMethods().size(), is(1));
		JApiMethod jApiMethod = jApiClass.getMethods().get(0);
		assertThat(jApiMethod.getReturnType().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiMethod.getReturnType().getOldReturnType(), is("int"));
		assertThat(jApiMethod.getReturnType().getNewReturnType(), is("boolean"));
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiMethod.getAccessModifier().getOldModifier().get(), is(AccessModifier.PUBLIC));
	}

	@Test
	public void testClassOneMethodAccessModifierChanged() throws Exception {
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
		assertThat(jApiClass.getMethods().size(), is(1));
		JApiMethod jApiMethod = jApiClass.getMethods().get(0);
		assertThat(jApiMethod.getReturnType().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiMethod.getReturnType().getOldReturnType(), is("int"));
		assertThat(jApiMethod.getReturnType().getNewReturnType(), is("int"));
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiMethod.getAccessModifier().getOldModifier().get(), is(AccessModifier.PUBLIC));
		assertThat(jApiMethod.getAccessModifier().getNewModifier().get(), is(AccessModifier.PRIVATE));
		assertThat(jApiMethod.getAccessModifier().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
	}

	@Test
	public void testClassOneMethodUnchanged() throws Exception {
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
		assertThat(jApiClass.getMethods().size(), is(1));
		JApiMethod jApiMethod = jApiClass.getMethods().get(0);
		assertThat(jApiMethod.getReturnType().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiMethod.getReturnType().getOldReturnType(), is("int"));
		assertThat(jApiMethod.getReturnType().getNewReturnType(), is("int"));
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiMethod.getAccessModifier().getOldModifier().get(), is(AccessModifier.PUBLIC));
	}

	@Test
	public void testClassWithTwoMethodsOneAddedWithAdditionalParameter() throws Exception {
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
		assertThat(jApiClass.getMethods().size(), is(2));
		boolean noParamFound = false;
		boolean oneParamFound = false;
		for (JApiMethod jApiMethod : jApiClass.getMethods()) {
			if (jApiMethod.getParameters().size() == 0) {
				noParamFound = true;
				assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
			}
			if (jApiMethod.getParameters().size() == 1) {
				oneParamFound = true;
				assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
			}
		}
		assertThat(noParamFound, is(true));
		assertThat(oneParamFound, is(true));
	}
}
