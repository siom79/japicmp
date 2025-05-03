package japicmp.cmp;

import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static japicmp.util.Helper.getJApiClass;
import static japicmp.util.Helper.getJApiMethod;
import static org.hamcrest.CoreMatchers.is;

class ClassesTest {

	@Test
	void testAbstractMethodAdded() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) {
				CtClass superClass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtClass subClass = CtClassBuilder.create().name("japicmp.Subclass").withSuperclass(superClass).addToClassPool(classPool);
				return Arrays.asList(superClass, subClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superClass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtClass subClass = CtClassBuilder.create().name("japicmp.Subclass").withSuperclass(superClass).addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().returnType(CtClass.voidType).name("method").addToClass(subClass);
				return Arrays.asList(superClass, subClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Subclass");
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
		MatcherAssert.assertThat(jApiMethod.isBinaryCompatible(), is(true));
		MatcherAssert.assertThat(jApiMethod.isSourceCompatible(), is(false));
	}

	@Test
	void testAbstractMethodAddedThatOverridesExistingAbstractMethod() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superClass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().returnType(CtClass.voidType).name("method").addToClass(superClass);
				CtClass subClass = CtClassBuilder.create().name("japicmp.Subclass").withSuperclass(superClass).addToClassPool(classPool);
				return Arrays.asList(superClass, subClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superClass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().returnType(CtClass.voidType).name("method").addToClass(superClass);
				CtClass subClass = CtClassBuilder.create().name("japicmp.Subclass").withSuperclass(superClass).addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().returnType(CtClass.voidType).name("method").addToClass(subClass);
				return Arrays.asList(superClass, subClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Subclass");
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
		MatcherAssert.assertThat(jApiMethod.isBinaryCompatible(), is(true));
		MatcherAssert.assertThat(jApiMethod.isSourceCompatible(), is(true));
	}

	@Test
	void testAbstractMethodAddedThatOverridesNewAbstractMethod() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) {
				CtClass superClass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtClass subClass = CtClassBuilder.create().name("japicmp.Subclass").withSuperclass(superClass).addToClassPool(classPool);
				return Arrays.asList(superClass, subClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superClass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().returnType(CtClass.voidType).name("method").addToClass(superClass);
				CtClass subClass = CtClassBuilder.create().name("japicmp.Subclass").withSuperclass(superClass).addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().returnType(CtClass.voidType).name("method").addToClass(subClass);
				return Arrays.asList(superClass, subClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Subclass");
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
		MatcherAssert.assertThat(jApiMethod.isBinaryCompatible(), is(true));
		MatcherAssert.assertThat(jApiMethod.isSourceCompatible(), is(false));
	}

	@Test
	void testAbstractMethodAddedViaNewSuperclass() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superClass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().returnType(CtClass.voidType).name("method").addToClass(superClass);
				CtClass subClass = CtClassBuilder.create().name("japicmp.Subclass").addToClassPool(classPool);
				return Arrays.asList(superClass, subClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superClass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().returnType(CtClass.voidType).name("method").addToClass(superClass);
				CtClass subClass = CtClassBuilder.create().name("japicmp.Subclass").withSuperclass(superClass).addToClassPool(classPool);
				return Arrays.asList(superClass, subClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Subclass");
		MatcherAssert.assertThat(jApiClass.isBinaryCompatible(), is(true));
		MatcherAssert.assertThat(jApiClass.isSourceCompatible(), is(false));
	}
}
