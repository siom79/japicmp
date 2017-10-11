package japicmp.cmp;

import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiCompatibilityChange;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtInterfaceBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static japicmp.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class SuperclassTest {

	@Test
	public void testClassHasNoSuperclass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = classPool.get("java.lang.Object");
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = classPool.get("java.lang.Object");
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "java.lang.Object");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
	}

	@Test
	public void testNewClass() throws Exception {
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
				CtClass ctClassSuper = CtClassBuilder.create().name("japicmp.Super").addToClassPool(classPool);
				ctClass.setSuperclass(ctClassSuper);
				return Arrays.asList(ctClass, ctClassSuper);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.NEW));
		assertThat(jApiClass.getSuperclass().getChangeStatus(), is(JApiChangeStatus.NEW));
		assertThat(jApiClass.getSuperclass().getOldSuperclass().isPresent(), is(false));
		assertThat(jApiClass.getSuperclass().getNewSuperclass().isPresent(), is(true));
		assertThat(jApiClass.getSuperclass().getNewSuperclass().get().getName(), is("japicmp.Super"));
	}

	@Test
	public void testRemovedClass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtClass ctClassSuper = CtClassBuilder.create().name("japicmp.Super").addToClassPool(classPool);
				ctClass.setSuperclass(ctClassSuper);
				return Arrays.asList(ctClass, ctClassSuper);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				return Collections.emptyList();
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.REMOVED));
		assertThat(jApiClass.getSuperclass().getChangeStatus(), is(JApiChangeStatus.REMOVED));
		assertThat(jApiClass.getSuperclass().getOldSuperclass().isPresent(), is(true));
		assertThat(jApiClass.getSuperclass().getNewSuperclass().isPresent(), is(false));
		assertThat(jApiClass.getSuperclass().getOldSuperclass().get().getName(), is("japicmp.Super"));
	}

	@Test
	public void testClassHierarchyHasOneMoreLevel() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClassBase = CtInterfaceBuilder.create().name("Base").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("Test").withSuperclass(ctClassBase).addToClassPool(classPool);
				return Arrays.asList(ctClassBase, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClassBase = CtClassBuilder.create().name("Base").addToClassPool(classPool);
				CtClass ctClassIntermediate = CtClassBuilder.create().name("Intermediate").withSuperclass(ctClassBase).addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("Test").withSuperclass(ctClassIntermediate).addToClassPool(classPool);
				return Arrays.asList(ctClassBase, ctClassIntermediate, ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "Test");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		assertThat(jApiClass.getCompatibilityChanges(), not(hasItem(JApiCompatibilityChange.SUPERCLASS_REMOVED)));
		assertThat(jApiClass.getSuperclass().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		jApiClass = getJApiClass(jApiClasses, "Intermediate");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.NEW));
	}

	@Test
	public void testClassHierarchyHasOneMoreLevelWithExistingClasses() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClassBase = CtInterfaceBuilder.create().name("Base").addToClassPool(classPool);
				CtClass ctClassIntermediate = CtClassBuilder.create().name("Intermediate").withSuperclass(ctClassBase).addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("Test").withSuperclass(ctClassBase).addToClassPool(classPool);
				return Arrays.asList(ctClassBase, ctClassIntermediate, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClassBase = CtClassBuilder.create().name("Base").addToClassPool(classPool);
				CtClass ctClassIntermediate = CtClassBuilder.create().name("Intermediate").withSuperclass(ctClassBase).addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("Test").withSuperclass(ctClassIntermediate).addToClassPool(classPool);
				return Arrays.asList(ctClassBase, ctClassIntermediate, ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "Test");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		assertThat(jApiClass.getSuperclass().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		jApiClass = getJApiClass(jApiClasses, "Intermediate");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
	}

	@Test
	public void testClassHierarchyHasOneLessLevel() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClassBase = CtClassBuilder.create().name("Base").addToClassPool(classPool);
				CtClass ctClassIntermediate = CtClassBuilder.create().name("Intermediate").withSuperclass(ctClassBase).addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("Test").withSuperclass(ctClassIntermediate).addToClassPool(classPool);
				return Arrays.asList(ctClassBase, ctClassIntermediate, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClassBase = CtInterfaceBuilder.create().name("Base").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("Test").withSuperclass(ctClassBase).addToClassPool(classPool);
				return Arrays.asList(ctClassBase, ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "Test");
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.isSourceCompatible(), is(false));
		assertThat(jApiClass.getSuperclass().getCompatibilityChanges(), hasItem(JApiCompatibilityChange.SUPERCLASS_REMOVED));
		assertThat(jApiClass.getSuperclass().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		jApiClass = getJApiClass(jApiClasses, "Intermediate");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.REMOVED));
	}
}
