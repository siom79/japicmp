package japicmp.compat;

import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiCompatibilityChange;
import japicmp.model.JApiConstructor;
import japicmp.model.JApiField;
import japicmp.model.JApiMethod;
import japicmp.model.JApiSuperclass;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtConstructorBuilder;
import japicmp.util.CtFieldBuilder;
import japicmp.util.CtInterfaceBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static japicmp.util.Helper.getJApiClass;
import static japicmp.util.Helper.getJApiConstructor;
import static japicmp.util.Helper.getJApiField;
import static japicmp.util.Helper.getJApiMethod;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class CompatibilityChangesTest {

	@Test
	public void testClassRemoved() throws Exception {
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
				return Collections.emptyList();
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.REMOVED));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.CLASS_REMOVED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
	}

	@Test
	public void testClassNowAbstract() throws Exception {
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
				CtClass ctClass = CtClassBuilder.create().abstractModifier().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.CLASS_NOW_ABSTRACT));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
	}

	@Test
	public void testClassNowFinal() throws Exception {
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
				CtClass ctClass = CtClassBuilder.create().finalModifier().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.CLASS_NOW_FINAL));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
	}

	@Test
	public void testClassNoLongerPublic() throws Exception {
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
				CtClass ctClass = CtClassBuilder.create().privateModifier().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.CLASS_NO_LONGER_PUBLIC));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
	}

	@Test
	public void testClassTypeChanged() throws Exception {
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
				CtClass ctClass = CtInterfaceBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.CLASS_TYPE_CHANGED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
	}

	@Test
	public void testSuperclassRemoved() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				return Arrays.asList(ctClass, superclass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtInterfaceBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getSuperclass().getCompatibilityChanges(), hasItem(JApiCompatibilityChange.SUPERCLASS_REMOVED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
	}

	@Test
	public void testSuperclassChanged() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				return Arrays.asList(ctClass, superclass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass2").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				return Arrays.asList(ctClass, superclass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getSuperclass().getCompatibilityChanges(), hasItem(JApiCompatibilityChange.SUPERCLASS_REMOVED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
	}

	@Test
	public void testSuperclassAdded() throws Exception {
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
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				return Arrays.asList(ctClass, superclass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getSuperclass().getCompatibilityChanges(), hasItem(JApiCompatibilityChange.SUPERCLASS_ADDED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		JApiSuperclass superclass = jApiClass.getSuperclass();
		assertThat(superclass.isBinaryCompatible(), is(true));
		assertThat(superclass.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.SUPERCLASS_ADDED));
	}

	@Test
	public void testSuperclassUnchangedObject() throws Exception {
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
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getCompatibilityChanges().size(), is(0));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		JApiSuperclass superclass = jApiClass.getSuperclass();
		assertThat(superclass.isBinaryCompatible(), is(true));
		assertThat(superclass.getCompatibilityChanges().size(), is(0));
	}

	@Test
	public void testMethodRemovedInSuperclass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(superclass).name("getInstance").addToClass(superclass);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				return Arrays.asList(ctClass, superclass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				return Arrays.asList(ctClass, superclass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.METHOD_REMOVED_IN_SUPERCLASS));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
	}

	@Test
	public void testMethodRemovedInSuperclassButOverriddenInSubclass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(superclass).name("getInstance").addToClass(superclass);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(superclass).name("getInstance").addToClass(ctClass);
				return Arrays.asList(ctClass, superclass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(superclass).name("getInstance").addToClass(ctClass);
				return Arrays.asList(ctClass, superclass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
	}

	@Test
	public void testFieldRemovedInSuperclass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).name("field").addToClass(superclass);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				return Arrays.asList(ctClass, superclass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				return Arrays.asList(ctClass, superclass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.FIELD_REMOVED_IN_SUPERCLASS));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
	}

	@Test
	public void testFieldRemovedInSuperclassButOverriddenInSubclass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).name("field").addToClass(superclass);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).name("field").addToClass(ctClass);
				return Arrays.asList(ctClass, superclass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).name("field").addToClass(ctClass);
				return Arrays.asList(ctClass, superclass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
	}

	@Test
	public void testMethodRemoved() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("isRemoved").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "isRemoved");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.METHOD_REMOVED));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
		JApiSuperclass superclass = jApiClass.getSuperclass();
		assertThat(superclass.isBinaryCompatible(), is(true));
		assertThat(superclass.getCompatibilityChanges().size(), is(0));
	}

	@Test
	public void testMethodLessAccessiblePublicToPrivate() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("isRemoved").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().privateAccess().returnType(CtClass.booleanType).name("isRemoved").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "isRemoved");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.METHOD_LESS_ACCESSIBLE));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}

	@Test
	public void testClassLessAccessiblePublicToPrivate() throws Exception {
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
				CtClass ctClass = CtClassBuilder.create().privateModifier().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.CLASS_LESS_ACCESSIBLE));
	}

	@Test
	public void testMethodLessAccessibleThanInSuperclass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("isRemoved").body("return true;").addToClass(superclass);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				return Arrays.asList(superclass, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("isRemoved").body("return true;").addToClass(superclass);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				CtMethodBuilder.create().protectedAccess().returnType(CtClass.booleanType).name("isRemoved").body("return true;").addToClass(ctClass);
				return Arrays.asList(superclass, ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "isRemoved");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.METHOD_LESS_ACCESSIBLE_THAN_IN_SUPERCLASS));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}

	@Test
	public void testMethodStaticOverridesNonStatic() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("isOverridden").body("return true;").addToClass(superclass);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				return Arrays.asList(superclass, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("isOverridden").body("return true;").addToClass(superclass);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().staticAccess().returnType(CtClass.booleanType).name("isOverridden").body("return true;").addToClass(ctClass);
				return Arrays.asList(superclass, ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "isOverridden");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.METHOD_IS_STATIC_AND_OVERRIDES_NOT_STATIC));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}

	@Test
	public void testMethodReturnTypeChanges() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("returnTypeChanges").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("returnTypeChanges").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "returnTypeChanges");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.METHOD_RETURN_TYPE_CHANGED));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}

	@Test
	public void testMethodNowAbstract() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("methodBecomesAbstract").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().returnType(CtClass.booleanType).name("methodBecomesAbstract").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodBecomesAbstract");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.METHOD_NOW_ABSTRACT));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}

	@Test
	public void testMethodNowFinal() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("methodBecomesFinal").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().finalMethod().returnType(CtClass.booleanType).name("methodBecomesFinal").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodBecomesFinal");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.METHOD_NOW_FINAL));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}

	@Test
	public void testMethodNowStatic() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("methodBecomesStatic").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().staticAccess().returnType(CtClass.booleanType).name("methodBecomesStatic").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodBecomesStatic");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.METHOD_NOW_STATIC));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}

	@Test
	public void testMethodNoLongerStatic() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().staticAccess().returnType(CtClass.booleanType).name("methodNoLongerStatic").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("methodNoLongerStatic").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodNoLongerStatic");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.METHOD_NO_LONGER_STATIC));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}

	@Test
	public void testFieldStaticOverridesStatic() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtFieldBuilder.create().staticAccess().type(CtClass.intType).name("field").addToClass(superclass);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtFieldBuilder.create().staticAccess().type(CtClass.intType).name("field").addToClass(superclass);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				CtFieldBuilder.create().staticAccess().type(CtClass.intType).name("field").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiField jApiField = getJApiField(jApiClass.getFields(), "field");
		assertThat(jApiField.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.FIELD_STATIC_AND_OVERRIDES_STATIC));
		assertThat(jApiField.isBinaryCompatible(), is(false));
	}

	@Test
	public void testFieldLessAccessibleThanInSuperclass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).name("field").addToClass(superclass);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).name("field").addToClass(superclass);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				CtFieldBuilder.create().packageProtectedAccess().type(CtClass.intType).name("field").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiField jApiField = getJApiField(jApiClass.getFields(), "field");
		assertThat(jApiField.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.FIELD_LESS_ACCESSIBLE_THAN_IN_SUPERCLASS));
		assertThat(jApiField.isBinaryCompatible(), is(false));
	}

	@Test
	public void testFieldNowFinal() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).name("field").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtFieldBuilder.create().finalAccess().type(CtClass.intType).name("field").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiField jApiField = getJApiField(jApiClass.getFields(), "field");
		assertThat(jApiField.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.FIELD_NOW_FINAL));
		assertThat(jApiField.isBinaryCompatible(), is(false));
	}

	@Test
	public void testFieldNowStatic() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).name("field").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtFieldBuilder.create().staticAccess().type(CtClass.intType).name("field").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiField jApiField = getJApiField(jApiClass.getFields(), "field");
		assertThat(jApiField.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.FIELD_NOW_STATIC));
		assertThat(jApiField.isBinaryCompatible(), is(false));
	}

	@Test
	public void testFieldNoLongerStatic() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtFieldBuilder.create().staticAccess().type(CtClass.intType).name("field").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).name("field").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiField jApiField = getJApiField(jApiClass.getFields(), "field");
		assertThat(jApiField.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.FIELD_NO_LONGER_STATIC));
		assertThat(jApiField.isBinaryCompatible(), is(false));
	}

	@Test
	public void testFieldTypeChanged() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).name("field").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.floatType).name("field").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiField jApiField = getJApiField(jApiClass.getFields(), "field");
		assertThat(jApiField.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.FIELD_TYPE_CHANGED));
		assertThat(jApiField.isBinaryCompatible(), is(false));
	}

	@Test
	public void testFieldRemoved() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).name("field").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiField jApiField = getJApiField(jApiClass.getFields(), "field");
		assertThat(jApiField.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.FIELD_REMOVED));
		assertThat(jApiField.isBinaryCompatible(), is(false));
	}

	@Test
	public void testFieldLessAccessible() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).name("field").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtFieldBuilder.create().packageProtectedAccess().type(CtClass.intType).name("field").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiField jApiField = getJApiField(jApiClass.getFields(), "field");
		assertThat(jApiField.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.FIELD_LESS_ACCESSIBLE));
		assertThat(jApiField.isBinaryCompatible(), is(false));
	}

	@Test
	public void testConstructorRemoved() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtConstructorBuilder.create().publicAccess().parameter(CtClass.intType).addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiConstructor jApiConstructor = getJApiConstructor(jApiClass.getConstructors(), Collections.singletonList("int"));
		assertThat(jApiConstructor.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.CONSTRUCTOR_REMOVED));
		assertThat(jApiConstructor.isBinaryCompatible(), is(false));
	}

	@Test
	public void testConstructorLessAccessible() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtConstructorBuilder.create().publicAccess().parameter(CtClass.intType).addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtConstructorBuilder.create().protectedAccess().parameter(CtClass.intType).addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiConstructor jApiConstructor = getJApiConstructor(jApiClass.getConstructors(), Collections.singletonList("int"));
		assertThat(jApiConstructor.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.CONSTRUCTOR_LESS_ACCESSIBLE));
		assertThat(jApiConstructor.isBinaryCompatible(), is(false));
	}

	@Test
	public void testMethodAddedToInterface() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtInterfaceBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtInterfaceBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().name("method").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.METHOD_ADDED_TO_INTERFACE));
		assertThat(jApiMethod.isBinaryCompatible(), is(true));
		assertThat(jApiMethod.isSourceCompatible(), is(false));
	}

	@Test
	public void testAbstractClassNowExtendsAnotherAbstractClass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superClass = CtClassBuilder.create().abstractModifier().name("japicmp.Superclass").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().abstractModifier().name("japicmp.Test").addToClassPool(classPool);
				return Arrays.asList(ctClass, superClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superClass = CtClassBuilder.create().abstractModifier().name("japicmp.Superclass").addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.intType).abstractMethod().name("newAbstractMethod").addToClass(superClass);
				CtClass ctClass = CtClassBuilder.create().abstractModifier().name("japicmp.Test").withSuperclass(superClass).addToClassPool(classPool);
				return Arrays.asList(ctClass, superClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(false));
		jApiClass = getJApiClass(jApiClasses, "japicmp.Superclass");
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "newAbstractMethod");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.METHOD_ABSTRACT_ADDED_TO_CLASS));
		assertThat(jApiMethod.isBinaryCompatible(), is(true));
		assertThat(jApiMethod.isSourceCompatible(), is(false));
	}

	@Test
	public void testMethodAddedToNewInterface() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				return Collections.emptyList();
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtInterfaceBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().name("method").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.NEW));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(jApiMethod.getCompatibilityChanges().size(), is(0));
		assertThat(jApiMethod.isBinaryCompatible(), is(true));
		assertThat(jApiMethod.isSourceCompatible(), is(true));
	}

	@Test
	public void testInterfaceMovedToAbstractClass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctInterface = CtInterfaceBuilder.create().name("Interface").addToClassPool(classPool);
				CtMethodBuilder.create().abstractMethod().returnType(CtClass.voidType).name("method").addToClass(ctInterface);
				CtClass ctClass = CtClassBuilder.create().name("Test").implementsInterface(ctInterface).addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.voidType).name("method").body("int a = 42;").addToClass(ctClass);
				return Arrays.asList(ctInterface, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctInterface = CtInterfaceBuilder.create().name("Interface").addToClassPool(classPool);
				CtMethodBuilder.create().abstractMethod().returnType(CtClass.voidType).name("method").addToClass(ctInterface);
				CtClass ctAbstractClass = CtClassBuilder.create().name("AbstractTest").implementsInterface(ctInterface).addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("Test").withSuperclass(ctAbstractClass).addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.voidType).name("method").body("int a = 42;").addToClass(ctClass);
				return Arrays.asList(ctInterface, ctClass, ctAbstractClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		assertThat(jApiClass.getInterfaces().size(), is(1));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(jApiMethod.getCompatibilityChanges().size(), is(0));
		assertThat(jApiMethod.isBinaryCompatible(), is(true));
		assertThat(jApiMethod.isSourceCompatible(), is(true));
	}

	@Test
	public void testAbstractMethodMovedToInterface() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctInterface = CtInterfaceBuilder.create().name("Interface").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("Test").implementsInterface(ctInterface).addToClassPool(classPool);
				CtMethodBuilder.create().abstractMethod().returnType(CtClass.voidType).name("method").addToClass(ctClass);
				return Arrays.asList(ctInterface, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctInterface = CtInterfaceBuilder.create().name("Interface").addToClassPool(classPool);
				CtMethodBuilder.create().abstractMethod().returnType(CtClass.voidType).name("method").addToClass(ctInterface);
				CtClass ctClass = CtClassBuilder.create().name("Test").implementsInterface(ctInterface).addToClassPool(classPool);
				return Arrays.asList(ctInterface, ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		assertThat(jApiClass.getInterfaces().size(), is(1));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.REMOVED));
		assertThat(jApiMethod.isBinaryCompatible(), is(true));
		assertThat(jApiMethod.isSourceCompatible(), is(true));
	}

	@Test
	public void testMethodMovedFromOneInterfaceToAnother() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctInterface1 = CtInterfaceBuilder.create().name("Interface1").addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.intType).abstractMethod().name("method").addToClass(ctInterface1);
				CtClass ctInterface2 = CtInterfaceBuilder.create().name("Interface2").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().abstractModifier().name("japicmp.Test").implementsInterface(ctInterface1).implementsInterface(ctInterface2).addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.intType).name("method").body("return 42;").addToClass(ctClass);
				return Arrays.asList(ctClass, ctInterface1, ctInterface2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctInterface1 = CtInterfaceBuilder.create().name("Interface1").addToClassPool(classPool);
				CtClass ctInterface2 = CtInterfaceBuilder.create().name("Interface2").addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.intType).abstractMethod().name("method").addToClass(ctInterface2);
				CtClass ctClass = CtClassBuilder.create().abstractModifier().name("japicmp.Test").implementsInterface(ctInterface1).implementsInterface(ctInterface2).addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.intType).name("method").body("return 42;").addToClass(ctClass);
				return Arrays.asList(ctClass, ctInterface1, ctInterface2);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
	}

	@Test
	public void testMethodMovedFromOneAbstractClassToAnother() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass abstractClass1 = CtClassBuilder.create().abstractModifier().name("AbstractClass1").addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.intType).abstractMethod().name("method").addToClass(abstractClass1);
				CtClass abstractClass2 = CtClassBuilder.create().abstractModifier().name("AbstractClass2").withSuperclass(abstractClass1).addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().abstractModifier().name("japicmp.Test").withSuperclass(abstractClass2).addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.intType).name("method").body("return 42;").addToClass(ctClass);
				return Arrays.asList(ctClass, abstractClass1, abstractClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass abstractClass1 = CtClassBuilder.create().abstractModifier().name("AbstractClass1").addToClassPool(classPool);
				CtClass abstractClass2 = CtClassBuilder.create().abstractModifier().name("AbstractClass2").withSuperclass(abstractClass1).addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.intType).abstractMethod().name("method").addToClass(abstractClass2);
				CtClass ctClass = CtClassBuilder.create().abstractModifier().name("japicmp.Test").withSuperclass(abstractClass2).addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.intType).name("method").body("return 42;").addToClass(ctClass);
				return Arrays.asList(ctClass, abstractClass1, abstractClass2);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
	}

	@Test
	public void testClassNowCheckedException() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(classPool.get("java.lang.Exception")).addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(false));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.CLASS_NOW_CHECKED_EXCEPTION));
	}

	@Test
	public void testMethodThrowsNewCheckedException() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("method").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("method").exceptions(new CtClass[] {classPool.get("java.lang.Exception")}).addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		JApiMethod method = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(method.getExceptions().size(), Is.is(1));
		assertThat(method.getExceptions().get(0).getChangeStatus(), Is.is(JApiChangeStatus.NEW));
		assertThat(method.getExceptions().get(0).isCheckedException(), Is.is(true));
		assertThat(method.isBinaryCompatible(), is(true));
		assertThat(method.isSourceCompatible(), is(false));
		assertThat(method.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.METHOD_NOW_THROWS_CHECKED_EXCEPTION));
	}

	@Test
	public void testMethodThrowsNewRuntimeException() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("method").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("method").exceptions(new CtClass[] {classPool.get("java.lang.RuntimeException")}).addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		JApiMethod method = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(method.getExceptions().size(), Is.is(1));
		assertThat(method.getExceptions().get(0).getChangeStatus(), Is.is(JApiChangeStatus.NEW));
		assertThat(method.getExceptions().get(0).isCheckedException(), Is.is(false));
		assertThat(method.isBinaryCompatible(), is(true));
		assertThat(method.isSourceCompatible(), is(true));
		assertThat(method.getCompatibilityChanges().size(), is(0));
	}

	@Test
	public void testNewMethodThrowsCheckedException() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("method").exceptions(new CtClass[] {classPool.get("java.lang.Exception")}).addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		JApiMethod method = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(method.getExceptions().size(), Is.is(1));
		assertThat(method.getExceptions().get(0).getChangeStatus(), Is.is(JApiChangeStatus.NEW));
		assertThat(method.getExceptions().get(0).isCheckedException(), Is.is(true));
		assertThat(method.isBinaryCompatible(), is(true));
		assertThat(method.isSourceCompatible(), is(true));
	}

	@Test
	public void testMemberVariableMovedToSuperclass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superClass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superClass).addToClassPool(classPool);
				CtFieldBuilder.create().protectedAccess().type(CtClass.intType).name("test").addToClass(ctClass);
				return Arrays.asList(superClass, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superClass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtFieldBuilder.create().protectedAccess().type(CtClass.intType).name("test").addToClass(superClass);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superClass).addToClassPool(classPool);
				return Arrays.asList(superClass, ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
	}

	@Test
	public void testInterfaceImplementedBySuperclass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctInterface = CtInterfaceBuilder.create().name("japicmp.Interface").addToClassPool(classPool);
				CtClass superClass = CtClassBuilder.create().name("japicmp.Superclass").implementsInterface(ctInterface).addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superClass).implementsInterface(ctInterface).addToClassPool(classPool);
				return Arrays.asList(superClass, ctClass, ctInterface);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctInterface = CtInterfaceBuilder.create().name("japicmp.Interface").addToClassPool(classPool);
				CtClass superClass = CtClassBuilder.create().name("japicmp.Superclass").implementsInterface(ctInterface).addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superClass).addToClassPool(classPool);
				return Arrays.asList(superClass, ctClass, ctInterface);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
	}

	@Test
	public void testInterfaceAddDefaultMethod() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctInterface = CtInterfaceBuilder.create().name("japicmp.Interface").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").implementsInterface(ctInterface).addToClassPool(classPool);
				return Arrays.asList(ctClass, ctInterface);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctInterface = CtInterfaceBuilder.create().name("japicmp.Interface").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("defaultMethod").addToClass(ctInterface);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").implementsInterface(ctInterface).addToClassPool(classPool);
				return Arrays.asList(ctClass, ctInterface);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		jApiClass = getJApiClass(jApiClasses, "japicmp.Interface");
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.isSourceCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "defaultMethod");
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
		assertThat(jApiMethod.isSourceCompatible(), is(false));
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.METHOD_NEW_DEFAULT));
		assertThat(jApiMethod.getCompatibilityChanges(), not(hasItem(JApiCompatibilityChange.METHOD_ADDED_TO_INTERFACE)));
	}

	@Test
	public void testInterfaceAbstractMethodChangesToDefaultMethod() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctInterface = CtInterfaceBuilder.create().name("japicmp.Interface").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().name("defaultMethod").addToClass(ctInterface);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").implementsInterface(ctInterface).addToClassPool(classPool);
				return Arrays.asList(ctClass, ctInterface);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctInterface = CtInterfaceBuilder.create().name("japicmp.Interface").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("defaultMethod").addToClass(ctInterface);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").implementsInterface(ctInterface).addToClassPool(classPool);
				return Arrays.asList(ctClass, ctInterface);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		jApiClass = getJApiClass(jApiClasses, "japicmp.Interface");
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.isSourceCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "defaultMethod");
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
		assertThat(jApiMethod.isSourceCompatible(), is(false));
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.METHOD_ABSTRACT_NOW_DEFAULT));
		assertThat(jApiMethod.getCompatibilityChanges(), not(hasItem(JApiCompatibilityChange.METHOD_ADDED_TO_INTERFACE)));
	}

	@Test
	public void testAnnotcationDeprecatedAddedToMethod() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass aClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("method").addToClass(aClass);
				return Collections.singletonList(aClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass aClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().withAnnotation("java.lang.Deprecated").name("method").addToClass(aClass);
				return Collections.singletonList(aClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.ANNOTATION_DEPRECATED_ADDED));
	}

	@Test
	public void testAnnotcationDeprecatedAddedToClass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass aClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(aClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass aClass = CtClassBuilder.create().name("japicmp.Test").withAnnotation("java.lang.Deprecated").addToClassPool(classPool);
				return Collections.singletonList(aClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.ANNOTATION_DEPRECATED_ADDED));
	}

	@Test
	public void testAnnotcationDeprecatedRemovedFromClass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass aClass = CtClassBuilder.create().name("japicmp.Test").withAnnotation("java.lang.Deprecated").addToClassPool(classPool);
				return Collections.singletonList(aClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass aClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(aClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getCompatibilityChanges(), not(hasItem(JApiCompatibilityChange.ANNOTATION_DEPRECATED_ADDED)));
	}

	/**
	 * Tests that no regression of issue #222 occurs
	 * @throws Exception
	 */
	@Test
	public void testMethodMovedToSuperClass() throws Exception {
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
            @Override
            public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
                CtClass aClass = CtClassBuilder.create().name("japicmp.A").addToClassPool(classPool);
                CtClass bClass = CtClassBuilder.create().name("japicmp.B").withSuperclass(aClass).addToClassPool(classPool);
                CtClass cClass = CtClassBuilder.create().name("japicmp.C").withSuperclass(bClass).addToClassPool(classPool);

                CtMethodBuilder.create().name("foo").returnType(aClass).publicAccess().addToClass(bClass);

                return Arrays.asList(aClass, bClass, cClass);
            }

            @Override
            public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
                CtClass aClass = CtClassBuilder.create().name("japicmp.A").addToClassPool(classPool);
                CtClass bClass = CtClassBuilder.create().name("japicmp.B").withSuperclass(aClass).addToClassPool(classPool);
                CtClass cClass = CtClassBuilder.create().name("japicmp.C").withSuperclass(bClass).addToClassPool(classPool);

                CtMethodBuilder.create().name("foo").returnType(aClass).publicAccess().addToClass(aClass);
                return Arrays.asList(aClass, bClass, cClass);
            }
        });
        JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.C");
        assertThat(jApiClass.getCompatibilityChanges(), not(hasItem(JApiCompatibilityChange.METHOD_REMOVED_IN_SUPERCLASS)));

	}
}
