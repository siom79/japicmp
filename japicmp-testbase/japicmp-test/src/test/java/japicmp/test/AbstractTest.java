package japicmp.test;

import com.google.common.base.Optional;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AbstractTest {

    @Test
    public void testAbstractModifierChanges() {
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        options.setModifierLevel(AccessModifier.PRIVATE);
        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
        List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
        JApiClass abstractModifierClass = getJApiClass(jApiClasses, AbstractModifier.class.getName());
        assertThat(abstractModifierClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
        assertThat(abstractModifierClass.getAbstractModifier().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
        assertThat(abstractModifierClass.getAbstractModifier().getNewModifier(), is(Optional.of(japicmp.model.AbstractModifier.NON_ABSTRACT)));
    }
}
