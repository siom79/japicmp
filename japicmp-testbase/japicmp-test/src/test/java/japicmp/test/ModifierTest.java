package japicmp.test;

import japicmp.model.AccessModifier;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.test.util.Helper;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ModifierTest {

    @Test
    public void testOptionPublicModifier() {
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        options.setModifierLevel(AccessModifier.PUBLIC);
        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
        List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
        JApiClass modifierInnerClass = getJApiClass(jApiClasses, Modifier.ModifierPublicToProtected.class.getName());
        JApiClass modifierClass = getJApiClass(jApiClasses, Modifier.class.getName());
        assertThat(modifierInnerClass.getChangeStatus(), is(JApiChangeStatus.REMOVED));
        assertThat(Helper.getJApiMethod(modifierClass.getMethods(), "publicToPrivateMethod").getChangeStatus(), is(JApiChangeStatus.REMOVED));
    }

    @Test
    public void testOptionPrivateModifier() {
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        options.setModifierLevel(AccessModifier.PRIVATE);
        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
        List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
        JApiClass modifierInnerClass = getJApiClass(jApiClasses, Modifier.ModifierPublicToProtected.class.getName());
        JApiClass modifierClass = getJApiClass(jApiClasses, Modifier.class.getName());
        assertThat(modifierInnerClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
        assertThat(Helper.getJApiMethod(modifierClass.getMethods(), "publicToPrivateMethod").getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
    }
}
