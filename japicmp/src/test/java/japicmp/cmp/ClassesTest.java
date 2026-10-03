package japicmp.cmp;

import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiCompatibilityChangeType;
import japicmp.model.JApiMethod;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtConstructorBuilder;
import japicmp.util.CtFieldBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static japicmp.util.Helper.getJApiClass;
import static japicmp.util.Helper.getJApiField;
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

	@Test
	void testPrivateMethodAddedDoesNotModifyClassWithAccessModifierProtected() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PROTECTED);
		JApiClass jApiClass = getJApiClass(compareClassWithNewPrivateMethod(jarArchiveComparatorOptions), CtClassBuilder.DEFAULT_CLASS_NAME);
		MatcherAssert.assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		MatcherAssert.assertThat(jApiClass.isChangeCausedByClassElement(), is(false));
		MatcherAssert.assertThat(getJApiMethod(jApiClass.getMethods(), "method").getChangeStatus(), is(JApiChangeStatus.NEW));
	}

	@Test
	void testPrivateMethodAddedModifiesClassWithAccessModifierPrivate() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		JApiClass jApiClass = getJApiClass(compareClassWithNewPrivateMethod(jarArchiveComparatorOptions), CtClassBuilder.DEFAULT_CLASS_NAME);
		MatcherAssert.assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		MatcherAssert.assertThat(jApiClass.isChangeCausedByClassElement(), is(true));
	}

	private List<JApiClass> compareClassWithNewPrivateMethod(JarArchiveComparatorOptions jarArchiveComparatorOptions) throws Exception {
		return ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) {
				CtClass ctClass = CtClassBuilder.create().addToClassPool(classPool);
				return Arrays.asList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().addToClassPool(classPool);
				CtMethodBuilder.create().privateAccess().returnType(CtClass.voidType).name("method").addToClass(ctClass);
				return Arrays.asList(ctClass);
			}
		});
	}

	@Test
	void testPrivateFieldAddedDoesNotModifyClassWithAccessModifierProtected() throws Exception {
		JApiClass jApiClass = compareClass(AccessModifier.PROTECTED, ctClass -> {
		}, ctClass -> CtFieldBuilder.create().privateAccess().type(CtClass.intType).name("field").addToClass(ctClass));
		MatcherAssert.assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		MatcherAssert.assertThat(jApiClass.isChangeCausedByClassElement(), is(false));
		MatcherAssert.assertThat(getJApiField(jApiClass.getFields(), "field").getChangeStatus(), is(JApiChangeStatus.NEW));
	}

	@Test
	void testPrivateFieldAddedModifiesClassWithAccessModifierPrivate() throws Exception {
		JApiClass jApiClass = compareClass(AccessModifier.PRIVATE, ctClass -> {
		}, ctClass -> CtFieldBuilder.create().privateAccess().type(CtClass.intType).name("field").addToClass(ctClass));
		MatcherAssert.assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		MatcherAssert.assertThat(jApiClass.isChangeCausedByClassElement(), is(true));
	}

	@Test
	void testPrivateConstructorAddedDoesNotModifyClassWithAccessModifierProtected() throws Exception {
		JApiClass jApiClass = compareClass(AccessModifier.PROTECTED, ctClass -> {
		}, ctClass -> CtConstructorBuilder.create().privateAccess().parameter(CtClass.intType).addToClass(ctClass));
		MatcherAssert.assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		MatcherAssert.assertThat(jApiClass.isChangeCausedByClassElement(), is(false));
		MatcherAssert.assertThat(jApiClass.getConstructors().get(0).getChangeStatus(), is(JApiChangeStatus.NEW));
	}

	@Test
	void testPrivateConstructorAddedModifiesClassWithAccessModifierPrivate() throws Exception {
		JApiClass jApiClass = compareClass(AccessModifier.PRIVATE, ctClass -> {
		}, ctClass -> CtConstructorBuilder.create().privateAccess().parameter(CtClass.intType).addToClass(ctClass));
		MatcherAssert.assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		MatcherAssert.assertThat(jApiClass.isChangeCausedByClassElement(), is(true));
	}

	@Test
	void testMethodFromPrivateToPublicModifiesClassWithAccessModifierProtected() throws Exception {
		JApiClass jApiClass = compareClass(AccessModifier.PROTECTED,
			ctClass -> CtMethodBuilder.create().privateAccess().returnType(CtClass.voidType).name("method").addToClass(ctClass),
			ctClass -> CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("method").addToClass(ctClass));
		MatcherAssert.assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		MatcherAssert.assertThat(jApiClass.isChangeCausedByClassElement(), is(true));
	}

	@Test
	void testMethodFromPublicToPrivateModifiesClassWithAccessModifierProtected() throws Exception {
		JApiClass jApiClass = compareClass(AccessModifier.PROTECTED,
			ctClass -> CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("method").addToClass(ctClass),
			ctClass -> CtMethodBuilder.create().privateAccess().returnType(CtClass.voidType).name("method").addToClass(ctClass));
		MatcherAssert.assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		MatcherAssert.assertThat(jApiClass.isChangeCausedByClassElement(), is(true));
	}

	@Test
	void testMethodFromPackageProtectedToPrivateDoesNotModifyClassWithAccessModifierProtected() throws Exception {
		JApiClass jApiClass = compareClass(AccessModifier.PROTECTED,
			ctClass -> CtMethodBuilder.create().packageProtectedAccess().returnType(CtClass.voidType).name("method").addToClass(ctClass),
			ctClass -> CtMethodBuilder.create().privateAccess().returnType(CtClass.voidType).name("method").addToClass(ctClass));
		MatcherAssert.assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		MatcherAssert.assertThat(jApiClass.isChangeCausedByClassElement(), is(false));
	}

	@Test
	void testConstructorFromPackageProtectedToPrivateIsNotReportedAsNotExtendableWithAccessModifierProtected() throws Exception {
		//the constructor is not part of the compared API, hence the class was not extendable for clients before
		JApiClass jApiClass = compareClass(AccessModifier.PROTECTED,
			ctClass -> CtConstructorBuilder.create().modifier(0).addToClass(ctClass),
			ctClass -> CtConstructorBuilder.create().privateAccess().addToClass(ctClass));
		MatcherAssert.assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		MatcherAssert.assertThat(hasClassNowNotExtendable(jApiClass), is(false));
	}

	@Test
	void testConstructorFromPackageProtectedToPrivateIsReportedAsNotExtendableWithAccessModifierPrivate() throws Exception {
		JApiClass jApiClass = compareClass(AccessModifier.PRIVATE,
			ctClass -> CtConstructorBuilder.create().modifier(0).addToClass(ctClass),
			ctClass -> CtConstructorBuilder.create().privateAccess().addToClass(ctClass));
		MatcherAssert.assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		MatcherAssert.assertThat(hasClassNowNotExtendable(jApiClass), is(true));
	}

	@Test
	void testConstructorFromPublicToPrivateIsReportedAsNotExtendableWithAccessModifierProtected() throws Exception {
		JApiClass jApiClass = compareClass(AccessModifier.PROTECTED,
			ctClass -> CtConstructorBuilder.create().publicAccess().addToClass(ctClass),
			ctClass -> CtConstructorBuilder.create().privateAccess().addToClass(ctClass));
		MatcherAssert.assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		MatcherAssert.assertThat(hasClassNowNotExtendable(jApiClass), is(true));
	}

	private boolean hasClassNowNotExtendable(JApiClass jApiClass) {
		return jApiClass.getCompatibilityChanges().stream()
			.anyMatch(change -> change.getType() == JApiCompatibilityChangeType.CLASS_NOW_NOT_EXTENDABLE);
	}

	private interface ClassCustomizer {
		void customize(CtClass ctClass) throws Exception;
	}

	private JApiClass compareClass(AccessModifier accessModifier, final ClassCustomizer oldClass, final ClassCustomizer newClass) throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(accessModifier);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().addToClassPool(classPool);
				oldClass.customize(ctClass);
				return Arrays.asList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().addToClassPool(classPool);
				newClass.customize(ctClass);
				return Arrays.asList(ctClass);
			}
		});
		return getJApiClass(jApiClasses, CtClassBuilder.DEFAULT_CLASS_NAME);
	}
}
