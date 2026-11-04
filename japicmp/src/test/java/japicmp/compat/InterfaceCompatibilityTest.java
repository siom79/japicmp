package japicmp.compat;

import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.*;
import japicmp.util.*;
import javassist.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static japicmp.util.Helper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;

class InterfaceCompatibilityTest {

	@Test
	void testMethodAddedToInterface() throws Exception {
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
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ADDED_TO_INTERFACE)));
		assertThat(jApiMethod.isBinaryCompatible(), is(true));
		assertThat(jApiMethod.isSourceCompatible(), is(false));
	}

	@Test
	void testMethodStaticAddedToInterface() throws Exception {
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
				CtMethodBuilder.create().publicAccess().staticAccess().name("method").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_NEW_STATIC_ADDED_TO_INTERFACE)));
		assertThat(jApiMethod.isBinaryCompatible(), is(true));
		assertThat(jApiMethod.isSourceCompatible(), is(true));
	}

	@Test
	void testMethodNotStaticChangesToStaticInInterface() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtInterfaceBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("method").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtInterfaceBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().staticAccess().name("method").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.isSourceCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_NON_STATIC_IN_INTERFACE_NOW_STATIC)));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
		assertThat(jApiMethod.isSourceCompatible(), is(false));
	}

	@Test
	void testMethodStaticChangesToNotStaticInInterface() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtInterfaceBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().staticAccess().name("method").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtInterfaceBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("method").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.isSourceCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_STATIC_IN_INTERFACE_NO_LONGER_STATIC)));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
		assertThat(jApiMethod.isSourceCompatible(), is(false));
	}

	@Test
	void testMethodAddedToNewInterface() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) {
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
	void testInterfaceMovedToAbstractClass() throws Exception {
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
	void testAbstractMethodMovedToInterface() throws Exception {
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
	void testMethodMovedFromOneInterfaceToAnother() throws Exception {
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
	void testMethodMovedFromOneAbstractClassToAnother() throws Exception {
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
	void testInterfaceImplementedBySuperclass() throws Exception {
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
	void testInterfaceAddDefaultMethod() throws Exception {
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
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "defaultMethod");
		assertThat(jApiMethod.isBinaryCompatible(), is(true));
		assertThat(jApiMethod.isSourceCompatible(), is(true));
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_NEW_DEFAULT)));
		assertThat(jApiMethod.getCompatibilityChanges(), not(hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ADDED_TO_INTERFACE))));
	}

	@Test
	void testInterfaceDefaultMethodOverriddenInSubInterface() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctInterface = CtInterfaceBuilder.create().name("japicmp.Interface").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("defaultMethod").addToClass(ctInterface);
				CtClass ctInterfaceSub = CtInterfaceBuilder.create().name("japicmp.InterfaceSub").withSuperInterface(ctInterface).addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").implementsInterface(ctInterfaceSub).addToClassPool(classPool);
				return Arrays.asList(ctClass, ctInterface, ctInterfaceSub);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctInterface = CtInterfaceBuilder.create().name("japicmp.Interface").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("defaultMethod").addToClass(ctInterface);
				CtClass ctInterfaceSub = CtInterfaceBuilder.create().name("japicmp.InterfaceSub").withSuperInterface(ctInterface).addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("defaultMethod").addToClass(ctInterfaceSub);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").implementsInterface(ctInterfaceSub).addToClassPool(classPool);
				return Arrays.asList(ctClass, ctInterface, ctInterfaceSub);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.InterfaceSub");
		assertThat(jApiClass.getCompatibilityChanges().size(), is(0));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "defaultMethod");
		assertThat(jApiMethod.getCompatibilityChanges().size(), is(0));
		jApiClass = getJApiClass(jApiClasses, "japicmp.Interface");
		assertThat(jApiClass.getCompatibilityChanges().size(), is(0));
		jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getCompatibilityChanges().size(), is(1));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_DEFAULT_ADDED_IN_IMPLEMENTED_INTERFACE)));
	}

	@Test
	void testInterfaceAbstractMethodChangesToDefaultMethod() throws Exception {
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
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ABSTRACT_NOW_DEFAULT)));
		assertThat(jApiMethod.getCompatibilityChanges(), not(hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ADDED_TO_INTERFACE))));
	}

	@Test
	void testAddDefaultMethodToInterface() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass aInterface = CtInterfaceBuilder.create().name("japicmp.I").addToClassPool(classPool);
				CtClass aClass = CtClassBuilder.create().name("japicmp.C").implementsInterface(aInterface).addToClassPool(classPool);
				return Arrays.asList(aInterface, aClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass aInterface = CtInterfaceBuilder.create().name("japicmp.I").addToClassPool(classPool);
				CtMethodBuilder.create().name("defaultMethod").returnType(CtClass.booleanType).body("return true;").addToClass(aInterface);
				CtClass aClass = CtClassBuilder.create().name("japicmp.C").implementsInterface(aInterface).addToClassPool(classPool);
				return Arrays.asList(aInterface, aClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.C");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_DEFAULT_ADDED_IN_IMPLEMENTED_INTERFACE)));
	}

	@Test
	void testAddAbstractMethodToInterface() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass aInterface = CtInterfaceBuilder.create().name("japicmp.I").addToClassPool(classPool);
				CtClass aClass = CtClassBuilder.create().name("japicmp.C").implementsInterface(aInterface).addToClassPool(classPool);
				return Arrays.asList(aInterface, aClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass aInterface = CtInterfaceBuilder.create().name("japicmp.I").addToClassPool(classPool);
				CtMethodBuilder.create().abstractMethod().name("method").returnType(CtClass.booleanType).addToClass(aInterface);
				CtClass aClass = CtClassBuilder.create().name("japicmp.C").implementsInterface(aInterface).addToClassPool(classPool);
				return Arrays.asList(aInterface, aClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.C");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(false));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE)));
	}

	@Test
	void testNewInterfaceWithInterface() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) {
				return Collections.emptyList();
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass aInterface1 = CtInterfaceBuilder.create().name("japicmp.I1").addToClassPool(classPool);
				CtMethodBuilder.create().abstractMethod().name("method").returnType(CtClass.booleanType).addToClass(aInterface1);
				CtClass aInterface2 = CtInterfaceBuilder.create().name("japicmp.I2").withSuperInterface(aInterface1).addToClassPool(classPool);
				return Arrays.asList(aInterface1, aInterface2);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.I2");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		assertThat(jApiClass.getCompatibilityChanges(), not(hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE))));
	}

	@Test
	void testNewInterfaceWithClass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) {
				return Collections.emptyList();
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass aInterface1 = CtInterfaceBuilder.create().name("japicmp.I1").addToClassPool(classPool);
				CtMethodBuilder.create().abstractMethod().name("method").returnType(CtClass.booleanType).addToClass(aInterface1);
				CtClass c1 = CtClassBuilder.create().name("japicmp.C1").implementsInterface(aInterface1).addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("method").returnType(CtClass.booleanType).addToClass(c1);
				CtClass c2 = CtClassBuilder.create().name("japicmp.C2").withSuperclass(c1).addToClassPool(classPool);
				return Arrays.asList(aInterface1, c1, c2);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.I1");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		assertThat(jApiClass.getCompatibilityChanges(), not(hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE))));
		jApiClass = getJApiClass(jApiClasses, "japicmp.C1");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		assertThat(jApiClass.getCompatibilityChanges(), not(hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE))));
		jApiClass = getJApiClass(jApiClasses, "japicmp.C2");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		assertThat(jApiClass.getCompatibilityChanges(), not(hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE))));
	}

	@Test
	void testNewMethodInSuperInterface() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("I").addToClassPool(classPool);
				CtClass si = CtInterfaceBuilder.create().name("SI").withSuperInterface(superInterface).addToClassPool(classPool);
				return Arrays.asList(superInterface, si);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("I").addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.voidType).publicAccess().abstractMethod().name("method").addToClass(superInterface);
				CtClass si = CtInterfaceBuilder.create().name("SI").withSuperInterface(superInterface).addToClassPool(classPool);
				return Arrays.asList(superInterface, si);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "SI");
		assertThat(jApiClass.getCompatibilityChanges(), not(hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE))));
	}

	@Test
	void testClassExtendsUnaryOperator() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) {
				return Collections.emptyList();
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass clazz = CtClassBuilder.create().name("com.acme.japicmp.NewOperator")
						.implementsInterface(classPool.get("java.util.function.UnaryOperator")).addToClassPool(classPool);
				CtMethodBuilder.create().returnType(classPool.get("java.lang.Object")).publicAccess().name("apply")
						.parameter(classPool.get("java.lang.Object")).addToClass(clazz);
				return Collections.singletonList(clazz);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "com.acme.japicmp.NewOperator");
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.INTERFACE_ADDED)));
		assertThat(jApiClass.getCompatibilityChanges(), not(hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE))));
		JApiImplementedInterface implementedInterface = jApiClass.getInterfaces().stream().filter(i -> i.getFullyQualifiedName().equals("java.util.function.UnaryOperator")).findFirst().orElse(null);
		assertThat(implementedInterface, notNullValue());
		assertThat(implementedInterface.getCorrespondingJApiClass().isPresent(), is(true));
		assertThat(implementedInterface.getCorrespondingJApiClass().get().getFullyQualifiedName(), is("java.util.function.UnaryOperator"));
	}

	@Test
	void testSuperInterfaceWithAbstractMethodAddedToInterface() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("japicmp.SuperInterface").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().name("method").addToClass(superInterface);
				CtClass ctClass = CtInterfaceBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Arrays.asList(superInterface, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("japicmp.SuperInterface").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().name("method").addToClass(superInterface);
				CtClass ctClass = CtInterfaceBuilder.create().name("japicmp.Test").withSuperInterface(superInterface).addToClassPool(classPool);
				return Arrays.asList(superInterface, ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.INTERFACE_ADDED)));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE)));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(false));
	}

	@Test
	void testSuperInterfaceWithAlreadyDeclaredMethodAddedToInterface() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("japicmp.SuperInterface").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().name("method").addToClass(superInterface);
				CtClass ctClass = CtInterfaceBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().name("method").addToClass(ctClass);
				return Arrays.asList(superInterface, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("japicmp.SuperInterface").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().name("method").addToClass(superInterface);
				CtClass ctClass = CtInterfaceBuilder.create().name("japicmp.Test").withSuperInterface(superInterface).addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().name("method").addToClass(ctClass);
				return Arrays.asList(superInterface, ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.INTERFACE_ADDED)));
		assertThat(jApiClass.getCompatibilityChanges(), not(hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE))));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
	}

	@Test
	void testMarkerSuperInterfaceAddedToInterface() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("japicmp.SuperInterface").addToClassPool(classPool);
				CtClass ctClass = CtInterfaceBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Arrays.asList(superInterface, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("japicmp.SuperInterface").addToClassPool(classPool);
				CtClass ctClass = CtInterfaceBuilder.create().name("japicmp.Test").withSuperInterface(superInterface).addToClassPool(classPool);
				return Arrays.asList(superInterface, ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getCompatibilityChanges(), not(hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE))));
		assertThat(jApiClass.isSourceCompatible(), is(true));
	}
}
