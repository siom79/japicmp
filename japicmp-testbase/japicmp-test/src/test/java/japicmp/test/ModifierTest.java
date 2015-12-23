package japicmp.test;

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
import static org.junit.Assert.assertThat;

public class ModifierTest {

	@Test
	public void testOptionPublicModifier() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		JApiClass modifierInnerClass = getJApiClass(jApiClasses, Modifier.ModifierPublicToProtected.class.getName());
		JApiClass modifierClass = getJApiClass(jApiClasses, Modifier.class.getName());
		assertThat(modifierInnerClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(Helper.getJApiMethod(modifierClass.getMethods(), "publicToPrivateMethod").getChangeStatus(), is(JApiChangeStatus.MODIFIED));
	}

	@Test
	public void testOptionPrivateModifier() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		JApiClass modifierInnerClass = getJApiClass(jApiClasses, Modifier.ModifierPublicToProtected.class.getName());
		JApiClass modifierClass = getJApiClass(jApiClasses, Modifier.class.getName());
		assertThat(modifierInnerClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(Helper.getJApiMethod(modifierClass.getMethods(), "publicToPrivateMethod").getChangeStatus(), is(JApiChangeStatus.MODIFIED));
	}

	@Test
	public void testFinalModifierChanges() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		JApiClass modifierClass = getJApiClass(jApiClasses, Modifier.class.getName());
		assertThat(Helper.getJApiMethod(modifierClass.getMethods(), "nonFinalToFinalMethod").getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(Helper.getJApiMethod(modifierClass.getMethods(), "finalToNonFinalMethod").getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(Helper.getJApiMethod(modifierClass.getMethods(), "finalStaysFinalMethod").getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(Helper.getJApiMethod(modifierClass.getMethods(), "nonFinalStaysNonFinalMethod").getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
	}

	@Test
	public void testStaticModifierChanges() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		JApiClass modifierClass = getJApiClass(jApiClasses, Modifier.class.getName());
		assertThat(Helper.getJApiMethod(modifierClass.getMethods(), "nonStaticToStaticMethod").getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(Helper.getJApiMethod(modifierClass.getMethods(), "StaticToNonStaticMethod").getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(Helper.getJApiMethod(modifierClass.getMethods(), "staticStaysStaticMethod").getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(Helper.getJApiMethod(modifierClass.getMethods(), "nonStaticStaysNonStaticMethod").getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
	}
}
