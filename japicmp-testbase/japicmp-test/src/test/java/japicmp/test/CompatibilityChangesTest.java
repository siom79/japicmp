package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiField;
import japicmp.model.JApiMethod;
import japicmp.test.ClassMembers.ClassConstructorChangesAccessibility;
import japicmp.test.ClassMembers.ClassFieldChangesAccessibility;
import japicmp.test.ClassMembers.ClassLosesConstructor;
import japicmp.test.ClassMembers.ClassLosesField;
import japicmp.test.ClassMembers.ClassLosesMethod;
import japicmp.test.ClassMembers.ClassMethodChangesAccessibility;
import japicmp.test.ClassMembers.SubclassWithMethodToBeRemovedButContainedInSuperclass;
import japicmp.test.ClassModifier.AbstractToNonAbstractClass;
import japicmp.test.ClassModifier.FinalToNonFinalInnerClass;
import japicmp.test.ClassModifier.NonAbstractToAbstractClass;
import japicmp.test.ClassModifier.NonFinalToFinalInnerClass;
import japicmp.test.Enums.AbcToAb;
import japicmp.test.Enums.AbcToAbcd;
import japicmp.test.Interfaces.ClassWithInterfaceLosesMethod;
import japicmp.test.Interfaces.SubclassWithSuperclassLosesMethod;
import japicmp.test.binarycompatiblity.SubclassOverridesStaticField;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static japicmp.test.util.Helper.getJApiField;
import static japicmp.test.util.Helper.getJApiMethod;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CompatibilityChangesTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void test_JLS_13_4_1() {
		JApiClass abstractToNonAbstractClass = getJApiClass(jApiClasses, AbstractToNonAbstractClass.class.getName());
		assertThat(abstractToNonAbstractClass.isBinaryCompatible(), is(true));
		JApiClass nonAbstractToAbstractClass = getJApiClass(jApiClasses, NonAbstractToAbstractClass.class.getName());
		assertThat(nonAbstractToAbstractClass.isBinaryCompatible(), is(false));
	}

	@Test
	public void test_JLS_13_4_2() {
		JApiClass finalToNonFinalInnerClass = getJApiClass(jApiClasses, FinalToNonFinalInnerClass.class.getName());
		assertThat(finalToNonFinalInnerClass.isBinaryCompatible(), is(true));
		JApiClass nonFinalToFinalInnerClass = getJApiClass(jApiClasses, NonFinalToFinalInnerClass.class.getName());
		assertThat(nonFinalToFinalInnerClass.isBinaryCompatible(), is(false));
	}

	@Test
	public void test_JLS_13_4_3() {
		JApiClass publicToPrivateInnerClass = getJApiClass(jApiClasses, "japicmp.test.ClassModifier$PublicToPrivateInnerClass");
		assertThat(publicToPrivateInnerClass.isBinaryCompatible(), is(false));
	}

	@Test
	public void test_JLS_13_4_4() {
		JApiClass interfaceLosesMethod = getJApiClass(jApiClasses, Interfaces.InterfaceLosesMethod.class.getName());
		JApiMethod method = getJApiMethod(interfaceLosesMethod.getMethods(), "method");
		assertThat(method.getChangeStatus(), is(JApiChangeStatus.REMOVED));
		assertThat(interfaceLosesMethod.isBinaryCompatible(), is(false));
		JApiClass classWithInterfaceLosesMethod = getJApiClass(jApiClasses, ClassWithInterfaceLosesMethod.class.getName());
		assertThat(classWithInterfaceLosesMethod.isBinaryCompatible(), is(true));
		JApiClass superclassLosesMethod = getJApiClass(jApiClasses, Interfaces.SuperclassLosesMethod.class.getName());
		JApiMethod jApiMethod = getJApiMethod(superclassLosesMethod.getMethods(), "method");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.REMOVED));
		assertThat(superclassLosesMethod.isBinaryCompatible(), is(false));
		JApiClass subclassWithSuperclassLosesMethod = getJApiClass(jApiClasses, SubclassWithSuperclassLosesMethod.class.getName());
		assertThat(subclassWithSuperclassLosesMethod.isBinaryCompatible(), is(false));
	}

	@Test
	public void test_JLS_13_4_6() {
		JApiClass classLosesField = getJApiClass(jApiClasses, ClassLosesField.class.getName());
		assertThat(classLosesField.isBinaryCompatible(), is(false));
		JApiClass classLosesMethod = getJApiClass(jApiClasses, ClassLosesMethod.class.getName());
		assertThat(classLosesMethod.isBinaryCompatible(), is(false));
		JApiClass classLosesConstructor = getJApiClass(jApiClasses, ClassLosesConstructor.class.getName());
		assertThat(classLosesConstructor.isBinaryCompatible(), is(false));
	}

	@Test
	public void test_JLS_13_4_7() {
		JApiClass classFieldChangesAccessibility = getJApiClass(jApiClasses, ClassFieldChangesAccessibility.class.getName());
		assertThat(classFieldChangesAccessibility.isBinaryCompatible(), is(false));
		JApiClass classMethodChangesAccessibility = getJApiClass(jApiClasses, ClassMethodChangesAccessibility.class.getName());
		assertThat(classMethodChangesAccessibility.isBinaryCompatible(), is(false));
		JApiClass classConstructorChangesAccessibility = getJApiClass(jApiClasses, ClassConstructorChangesAccessibility.class.getName());
		assertThat(classConstructorChangesAccessibility.isBinaryCompatible(), is(false));
	}

	@Test
	public void test_JLS_13_4_8() {
		JApiClass subclassExtendsSuperclassWithField = getJApiClass(jApiClasses, SubclassOverridesStaticField.class.getName());
		assertThat(subclassExtendsSuperclassWithField.isBinaryCompatible(), is(false));
	}

	@Test
	public void test_JLS_13_4_9() {
		JApiClass fields = getJApiClass(jApiClasses, Fields.class.getName());
		JApiField finalToNonFinalField = getJApiField(fields.getFields(), "finalToNonFinalField");
		assertThat(finalToNonFinalField.isBinaryCompatible(), is(true));
		JApiField nonfinalToFinalField = getJApiField(fields.getFields(), "nonfinalToFinalField");
		assertThat(nonfinalToFinalField.isBinaryCompatible(), is(false));
	}

	@Test
	public void test_JLS_13_4_10() {
		JApiClass fields = getJApiClass(jApiClasses, Fields.class.getName());
		JApiField staticToNonStaticField = getJApiField(fields.getFields(), "staticToNonStaticField");
		assertThat(staticToNonStaticField.isBinaryCompatible(), is(false));
		JApiField nonStaticToStaticField = getJApiField(fields.getFields(), "nonStaticToStaticField");
		assertThat(nonStaticToStaticField.isBinaryCompatible(), is(false));
	}

	@Test
	public void test_JLS_13_4_12() {
		JApiClass subclassWithMethod = getJApiClass(jApiClasses, SubclassWithMethodToBeRemovedButContainedInSuperclass.class.getName());
		assertThat(subclassWithMethod.isBinaryCompatible(), is(true));
	}

	@Test
	public void test_JLS_13_4_15() {
		JApiClass jApiClass = getJApiClass(jApiClasses, MethodReturnType.class.getName());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodReturnTypeUnchanged");
		assertThat(jApiMethod.isBinaryCompatible(), is(true));
		jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodReturnTypeChangesFromIntToString");
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}

	@Test
	public void test_JLS_13_4_16() {
		JApiClass abstractModifier = getJApiClass(jApiClasses, AbstractModifier.class.getName());
		JApiMethod abstractToNonAbstract = getJApiMethod(abstractModifier.getMethods(), "abstractToNonAbstract");
		JApiMethod nonAbstractToAbstract = getJApiMethod(abstractModifier.getMethods(), "nonAbstractToAbstract");
		assertThat(abstractToNonAbstract.isBinaryCompatible(), is(true));
		assertThat(nonAbstractToAbstract.isBinaryCompatible(), is(false));
	}

	@Test
	public void test_JLS_13_4_17() {
		JApiClass methods = getJApiClass(jApiClasses, Methods.class.getName());
		JApiMethod finalToNonFinalMethod = getJApiMethod(methods.getMethods(), "finalToNonFinalMethod");
		JApiMethod nonFinalToFinalMethod = getJApiMethod(methods.getMethods(), "nonFinalToFinalMethod");
		JApiMethod staticNonFinalToStaticFinalMethod = getJApiMethod(methods.getMethods(), "staticNonFinalToStaticFinalMethod");
		assertThat(finalToNonFinalMethod.isBinaryCompatible(), is(true));
		assertThat(nonFinalToFinalMethod.isBinaryCompatible(), is(false));
		assertThat(staticNonFinalToStaticFinalMethod.isBinaryCompatible(), is(true));
	}

	@Test
	public void test_JLS_13_4_18() {
		JApiClass methods = getJApiClass(jApiClasses, Methods.class.getName());
		JApiMethod staticToNonStaticMethod = getJApiMethod(methods.getMethods(), "staticToNonStaticMethod");
		JApiMethod nonStaticToStaticMethod = getJApiMethod(methods.getMethods(), "nonStaticToStaticMethod");
		assertThat(staticToNonStaticMethod.isBinaryCompatible(), is(false));
		assertThat(nonStaticToStaticMethod.isBinaryCompatible(), is(false));
	}

	@Test
	public void test_JLS_13_4_26() {
		JApiClass abcToAbcd = getJApiClass(jApiClasses, AbcToAbcd.class.getName());
		assertThat(abcToAbcd.isBinaryCompatible(), is(true));
		JApiClass abcToAb = getJApiClass(jApiClasses, AbcToAb.class.getName());
		assertThat(abcToAb.isBinaryCompatible(), is(false));
	}
}
