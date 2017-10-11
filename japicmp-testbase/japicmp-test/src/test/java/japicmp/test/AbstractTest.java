package japicmp.test;

import japicmp.util.Optional;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static japicmp.test.util.Helper.getJApiMethod;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AbstractTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void testAbstractToNonAbstract() {
		JApiClass abstractModifierClass = getJApiClass(jApiClasses, AbstractModifier.class.getName());
		assertThat(abstractModifierClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(abstractModifierClass.getAbstractModifier().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(abstractModifierClass.getAbstractModifier().getOldModifier(), is(Optional.of(japicmp.model.AbstractModifier.ABSTRACT)));
		assertThat(abstractModifierClass.getAbstractModifier().getNewModifier(), is(Optional.of(japicmp.model.AbstractModifier.ABSTRACT)));
		assertThat(getJApiMethod(abstractModifierClass.getMethods(), "abstractToNonAbstract").getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(getJApiMethod(abstractModifierClass.getMethods(), "abstractToNonAbstract").getAbstractModifier().getOldModifier(), is(Optional.of(japicmp.model.AbstractModifier.ABSTRACT)));
		assertThat(getJApiMethod(abstractModifierClass.getMethods(), "abstractToNonAbstract").getAbstractModifier().getNewModifier(), is(Optional.of(japicmp.model.AbstractModifier.NON_ABSTRACT)));
	}

	@Test
	public void testNonAbstractToAbstract() {
		JApiClass abstractModifierClass = getJApiClass(jApiClasses, AbstractModifier.class.getName());
		assertThat(abstractModifierClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(abstractModifierClass.getAbstractModifier().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(abstractModifierClass.getAbstractModifier().getOldModifier(), is(Optional.of(japicmp.model.AbstractModifier.ABSTRACT)));
		assertThat(abstractModifierClass.getAbstractModifier().getNewModifier(), is(Optional.of(japicmp.model.AbstractModifier.ABSTRACT)));
		assertThat(getJApiMethod(abstractModifierClass.getMethods(), "nonAbstractToAbstract").getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(getJApiMethod(abstractModifierClass.getMethods(), "nonAbstractToAbstract").getAbstractModifier().getOldModifier(), is(Optional.of(japicmp.model.AbstractModifier.NON_ABSTRACT)));
		assertThat(getJApiMethod(abstractModifierClass.getMethods(), "nonAbstractToAbstract").getAbstractModifier().getNewModifier(), is(Optional.of(japicmp.model.AbstractModifier.ABSTRACT)));
	}

	@Test
	public void testAbstractRemainsAbstract() {
		JApiClass abstractModifierClass = getJApiClass(jApiClasses, AbstractModifier.class.getName());
		assertThat(abstractModifierClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(abstractModifierClass.getAbstractModifier().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(abstractModifierClass.getAbstractModifier().getOldModifier(), is(Optional.of(japicmp.model.AbstractModifier.ABSTRACT)));
		assertThat(abstractModifierClass.getAbstractModifier().getNewModifier(), is(Optional.of(japicmp.model.AbstractModifier.ABSTRACT)));
		assertThat(getJApiMethod(abstractModifierClass.getMethods(), "abstractRemainsAbstract").getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(getJApiMethod(abstractModifierClass.getMethods(), "abstractRemainsAbstract").getAbstractModifier().getOldModifier(), is(Optional.of(japicmp.model.AbstractModifier.ABSTRACT)));
		assertThat(getJApiMethod(abstractModifierClass.getMethods(), "abstractRemainsAbstract").getAbstractModifier().getNewModifier(), is(Optional.of(japicmp.model.AbstractModifier.ABSTRACT)));
	}
}
