package japicmp.cmp;

import japicmp.config.Options;
import japicmp.model.*;
import japicmp.output.stdout.StdoutOutputGenerator;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtInterfaceBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static japicmp.util.Helper.getJApiClass;
import static japicmp.util.Helper.getJApiImplementedInterface;
import static japicmp.util.Helper.getJApiMethod;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InterfacesTest {

	@Test
	public void testMethodAddedToInterfaceDefinedInSuperInterface() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("SuperInterface").addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.voidType).publicAccess().abstractMethod().name("method").addToClass(superInterface);
				CtClass subInterface = CtInterfaceBuilder.create().name("SubInterface").withSuperInterface(superInterface).addToClassPool(classPool);
				return Arrays.asList(superInterface, subInterface);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("SuperInterface").addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.voidType).publicAccess().abstractMethod().name("method").addToClass(superInterface);
				CtClass subInterface = CtInterfaceBuilder.create().name("SubInterface").withSuperInterface(superInterface).addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.voidType).publicAccess().abstractMethod().name("method").addToClass(subInterface);
				return Arrays.asList(superInterface, subInterface);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "SubInterface");
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
		assertThat(jApiMethod.isBinaryCompatible(), is(true));
		assertThat(jApiMethod.isSourceCompatible(), is(true));
	}

	@Test
	public void testMethodAddedToInterfaceAndInSuperInterface() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("SuperInterface").addToClassPool(classPool);
				CtClass subInterface = CtInterfaceBuilder.create().name("SubInterface").withSuperInterface(superInterface).addToClassPool(classPool);
				return Arrays.asList(superInterface, subInterface);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("SuperInterface").addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.voidType).publicAccess().abstractMethod().name("method").addToClass(superInterface);
				CtClass subInterface = CtInterfaceBuilder.create().name("SubInterface").withSuperInterface(superInterface).addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.voidType).publicAccess().abstractMethod().name("method").addToClass(subInterface);
				return Arrays.asList(superInterface, subInterface);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "SubInterface");
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
		assertThat(jApiMethod.isBinaryCompatible(), is(true));
		assertThat(jApiMethod.isSourceCompatible(), is(false));
	}

	@Test
	public void testInterfaceMarkerAdded() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("Interface").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("Test").addToClassPool(classPool);
				return Arrays.asList(superInterface, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("Interface").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("Test").implementsInterface(superInterface).addToClassPool(classPool);
				return Arrays.asList(superInterface, ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "Test");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
	}

	@Test
	public void testInterfaceAdded() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("Interface").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().name("method").addToClass(superInterface);
				CtClass ctClass = CtClassBuilder.create().name("Test").addToClassPool(classPool);
				return Arrays.asList(superInterface, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("Interface").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().name("method").addToClass(superInterface);
				CtClass ctClass = CtClassBuilder.create().name("Test").implementsInterface(superInterface).addToClassPool(classPool);
				return Arrays.asList(superInterface, ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "Test");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.INTERFACE_ADDED));
	}

	@Test
	public void testMethodPulledUp() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("SuperInterface").addToClassPool(classPool);
				CtClass subInterface = CtInterfaceBuilder.create().name("SubInterface").withSuperInterface(superInterface).addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.voidType).publicAccess().abstractMethod().name("method").addToClass(subInterface);
				return Arrays.asList(superInterface, subInterface);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superInterface = CtInterfaceBuilder.create().name("SuperInterface").addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.voidType).publicAccess().abstractMethod().name("method").addToClass(superInterface);
				CtClass subInterface = CtInterfaceBuilder.create().name("SubInterface").withSuperInterface(superInterface).addToClassPool(classPool);
				return Arrays.asList(superInterface, subInterface);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "SuperInterface");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(false));
		jApiClass = getJApiClass(jApiClasses, "SubInterface");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(false));
	}

	@Test
	public void testClassImplementsInterface() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClassInterface = CtInterfaceBuilder.create().name("Interface").addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.voidType).publicAccess().abstractMethod().name("method").addToClass(ctClassInterface);
				CtClass ctClass = CtClassBuilder.create().name("Test").addToClassPool(classPool);
				return Arrays.asList(ctClassInterface, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClassInterface = CtInterfaceBuilder.create().name("Interface").addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.voidType).publicAccess().abstractMethod().name("method").addToClass(ctClassInterface);
				CtClass ctClass = CtClassBuilder.create().name("Test").implementsInterface(ctClassInterface).addToClassPool(classPool);
				CtMethodBuilder.create().returnType(CtClass.voidType).publicAccess().name("method").addToClass(ctClass);
				return Arrays.asList(ctClassInterface, ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "Test");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
	}

	@Test
	public void testInterfaceHierarchyHasOneMoreLevel() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClassInterface = CtInterfaceBuilder.create().name("Interface").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("Test").implementsInterface(ctClassInterface).addToClassPool(classPool);
				return Arrays.asList(ctClassInterface, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClassInterface = CtInterfaceBuilder.create().name("Interface").addToClassPool(classPool);
				CtClass ctClassSubInterface = CtInterfaceBuilder.create().name("SubInterface").withSuperInterface(ctClassInterface).addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("Test").implementsInterface(ctClassSubInterface).addToClassPool(classPool);
				return Arrays.asList(ctClassInterface, ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "Test");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		assertThat(jApiClass.getInterfaces().size(), is(2));
		assertThat(getJApiImplementedInterface(jApiClass.getInterfaces(), "Interface").getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(getJApiImplementedInterface(jApiClass.getInterfaces(), "SubInterface").getChangeStatus(), is(JApiChangeStatus.NEW));
	}
}
