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
}
