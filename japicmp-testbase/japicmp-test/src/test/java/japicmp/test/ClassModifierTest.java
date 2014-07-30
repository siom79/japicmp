package japicmp.test;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.test.ClassModifier.FinalStaysFinalInnerClass;
import japicmp.test.ClassModifier.FinalToNonFinalInnerClass;
import japicmp.test.ClassModifier.NonFinalStaysNonFinalInnerClass;
import japicmp.test.ClassModifier.NonFinalToFinalInnerClass;
import japicmp.test.ClassModifier.NonStaticStaysNonStaticInnerClass;
import japicmp.test.ClassModifier.NonStaticToStaticInnerClass;
import japicmp.test.ClassModifier.StaticStaysStaticInnerClass;
import japicmp.test.ClassModifier.StaticToNonStaticInnerClass;

import java.util.List;

import org.junit.Test;

public class ClassModifierTest {

    @Test
    public void testStaticModifierChanges() {
    	JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        options.setModifierLevel(AccessModifier.PRIVATE);
        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
        List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
        assertThat(getJApiClass(jApiClasses, StaticToNonStaticInnerClass.class.getName()).getChangeStatus(), is(JApiChangeStatus.MODIFIED));
        assertThat(getJApiClass(jApiClasses, NonStaticToStaticInnerClass.class.getName()).getChangeStatus(), is(JApiChangeStatus.MODIFIED));
        assertThat(getJApiClass(jApiClasses, NonStaticStaysNonStaticInnerClass.class.getName()).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
        assertThat(getJApiClass(jApiClasses, StaticStaysStaticInnerClass.class.getName()).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
    }
    
    @Test
    public void testFinalModifierChanges() {
    	JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        options.setModifierLevel(AccessModifier.PRIVATE);
        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
        List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
        assertThat(getJApiClass(jApiClasses, FinalToNonFinalInnerClass.class.getName()).getChangeStatus(), is(JApiChangeStatus.MODIFIED));
        assertThat(getJApiClass(jApiClasses, NonFinalToFinalInnerClass.class.getName()).getChangeStatus(), is(JApiChangeStatus.MODIFIED));
        assertThat(getJApiClass(jApiClasses, NonFinalStaysNonFinalInnerClass.class.getName()).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
        assertThat(getJApiClass(jApiClasses, FinalStaysFinalInnerClass.class.getName()).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
    }
}
