package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiCompatibilityChange;
import org.hamcrest.MatcherAssert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

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
        MatcherAssert.assertThat(jApiClass.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.METHOD_DEFAULT_ADDED_IN_IMPLEMENTED_INTERFACE));
    }
    
    @Test
    public void testCompatibilityChangeAbstractMethod() {
        JApiClass jApiClass = getJApiClass(jApiClasses, DefaultMethod.CAbstract.class.getName());
        assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
        assertThat(jApiClass.isBinaryCompatible(), is(true));
        assertThat(jApiClass.isSourceCompatible(), is(false));
        MatcherAssert.assertThat(jApiClass.getCompatibilityChanges(), hasItem(JApiCompatibilityChange.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE));
    }

	@Test
	public void testCompatibilityAddMethodAndDefaultInSubInterfaces() {
		JApiClass jApiClass = getJApiClass(jApiClasses, DefaultMethod.DefaultInSubInterface.CClass.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		MatcherAssert.assertThat(
				jApiClass.getCompatibilityChanges(),
				containsInAnyOrder(
						JApiCompatibilityChange.METHOD_DEFAULT_ADDED_IN_IMPLEMENTED_INTERFACE));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
	}

	@Test
	public void testCompatibilityAddMethodAndDefaultInSubInterfacesChecksForExactMatch() {
		JApiClass jApiClass = getJApiClass(jApiClasses, DefaultMethod.UnrelatedDefaultInSubInterface.CClass.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		MatcherAssert.assertThat(
				jApiClass.getCompatibilityChanges(),
				containsInAnyOrder(
						JApiCompatibilityChange.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE,
						JApiCompatibilityChange.METHOD_DEFAULT_ADDED_IN_IMPLEMENTED_INTERFACE));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(false));
	}

	@Test
	public void testCompatibilityAddMethodWithDefaultAndOverrideInSubInterfaces() {
		JApiClass jApiClass = getJApiClass(jApiClasses, DefaultMethod.DefaultInParentInterface.CClass.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		MatcherAssert.assertThat(
				jApiClass.getCompatibilityChanges(),
				containsInAnyOrder(
						JApiCompatibilityChange.METHOD_DEFAULT_ADDED_IN_IMPLEMENTED_INTERFACE,
						JApiCompatibilityChange.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(false));
	}

	@Test
	public void testCompatibilityAddSuperClass() {
		JApiClass jApiClass = getJApiClass(jApiClasses, DefaultMethod.DefaultInSubInterfaceAddedSuperclass.CClass.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		MatcherAssert.assertThat(
				jApiClass.getCompatibilityChanges(),
				containsInAnyOrder(
						JApiCompatibilityChange.METHOD_DEFAULT_ADDED_IN_IMPLEMENTED_INTERFACE));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
	}
}
