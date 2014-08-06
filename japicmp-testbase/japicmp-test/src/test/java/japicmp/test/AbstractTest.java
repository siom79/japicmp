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
import static japicmp.test.util.Helper.getJApiMethod;
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
        assertThat(abstractModifierClass.getAbstractModifier().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
        assertThat(abstractModifierClass.getAbstractModifier().getOldModifier(), is(Optional.of(japicmp.model.AbstractModifier.ABSTRACT)));
        assertThat(abstractModifierClass.getAbstractModifier().getNewModifier(), is(Optional.of(japicmp.model.AbstractModifier.ABSTRACT)));
        assertThat(getJApiMethod(abstractModifierClass.getMethods(), "abstractToNonAbstract").getChangeStatus(), is(JApiChangeStatus.MODIFIED));
        assertThat(getJApiMethod(abstractModifierClass.getMethods(), "abstractToNonAbstract").getAbstractModifier().getOldModifier(), is(Optional.of(japicmp.model.AbstractModifier.ABSTRACT)));
        assertThat(getJApiMethod(abstractModifierClass.getMethods(), "abstractToNonAbstract").getAbstractModifier().getNewModifier(), is(Optional.of(japicmp.model.AbstractModifier.NON_ABSTRACT)));
        assertThat(getJApiMethod(abstractModifierClass.getMethods(), "nonAbstractToAbstract").getChangeStatus(), is(JApiChangeStatus.MODIFIED));
        assertThat(getJApiMethod(abstractModifierClass.getMethods(), "nonAbstractToAbstract").getAbstractModifier().getOldModifier(), is(Optional.of(japicmp.model.AbstractModifier.NON_ABSTRACT)));
        assertThat(getJApiMethod(abstractModifierClass.getMethods(), "nonAbstractToAbstract").getAbstractModifier().getNewModifier(), is(Optional.of(japicmp.model.AbstractModifier.ABSTRACT)));
        assertThat(getJApiMethod(abstractModifierClass.getMethods(), "abstractRemainsAbstract").getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
        assertThat(getJApiMethod(abstractModifierClass.getMethods(), "abstractRemainsAbstract").getAbstractModifier().getOldModifier(), is(Optional.of(japicmp.model.AbstractModifier.ABSTRACT)));
        assertThat(getJApiMethod(abstractModifierClass.getMethods(), "abstractRemainsAbstract").getAbstractModifier().getNewModifier(), is(Optional.of(japicmp.model.AbstractModifier.ABSTRACT)));
    }
}
