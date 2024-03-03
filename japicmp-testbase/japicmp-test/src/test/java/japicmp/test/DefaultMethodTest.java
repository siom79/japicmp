package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiCompatibilityChange;
import japicmp.model.JApiCompatibilityChangeType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

public class DefaultMethodTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void testCompatibilityChangeDefaultMethod() {
		JApiClass jApiClass = getJApiClass(jApiClasses, DefaultMethod.CDefault.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_DEFAULT_ADDED_IN_IMPLEMENTED_INTERFACE)));
	}

	@Test
	public void testCompatibilityChangeAbstractMethod() {
		JApiClass jApiClass = getJApiClass(jApiClasses, DefaultMethod.CAbstract.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(false));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE)));
	}

	@Test
	public void testCompatibilityAddMethodAndDefaultInSubInterfaces() {
		JApiClass jApiClass = getJApiClass(jApiClasses, DefaultMethod.DefaultInSubInterface.CClass.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(
			jApiClass.getCompatibilityChanges(),
			containsInAnyOrder(
				new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_DEFAULT_ADDED_IN_IMPLEMENTED_INTERFACE)));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
	}

	@Test
	public void testCompatibilityAddMethodAndDefaultInSubInterfacesChecksForExactMatch() {
		JApiClass jApiClass = getJApiClass(jApiClasses, DefaultMethod.UnrelatedDefaultInSubInterface.CClass.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(
			jApiClass.getCompatibilityChanges(),
			containsInAnyOrder(
				new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE),
				new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_DEFAULT_ADDED_IN_IMPLEMENTED_INTERFACE)));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(false));
	}

	@Test
	public void testCompatibilityAddMethodWithDefaultAndOverrideInSubInterfaces() {
		JApiClass jApiClass = getJApiClass(jApiClasses, DefaultMethod.DefaultInParentInterface.CClass.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(
			jApiClass.getCompatibilityChanges(),
			containsInAnyOrder(
				new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_DEFAULT_ADDED_IN_IMPLEMENTED_INTERFACE),
				new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE)));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(false));
	}

	@Test
	public void testCompatibilityAddSuperClass() {
		JApiClass jApiClass = getJApiClass(jApiClasses, DefaultMethod.DefaultInSubInterfaceAddedSuperclass.CClass.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(
			jApiClass.getCompatibilityChanges(),
			containsInAnyOrder(
				new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_DEFAULT_ADDED_IN_IMPLEMENTED_INTERFACE)));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
	}
}
