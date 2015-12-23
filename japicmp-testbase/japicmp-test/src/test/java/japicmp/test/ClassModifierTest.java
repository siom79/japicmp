package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
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
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClassModifierTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void testStaticModifierChanges() {
		assertThat(getJApiClass(jApiClasses, StaticToNonStaticInnerClass.class.getName()).getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(getJApiClass(jApiClasses, NonStaticToStaticInnerClass.class.getName()).getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(getJApiClass(jApiClasses, NonStaticStaysNonStaticInnerClass.class.getName()).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(getJApiClass(jApiClasses, StaticStaysStaticInnerClass.class.getName()).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
	}

	@Test
	public void testFinalModifierChanges() {
		assertThat(getJApiClass(jApiClasses, FinalToNonFinalInnerClass.class.getName()).getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(getJApiClass(jApiClasses, NonFinalToFinalInnerClass.class.getName()).getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(getJApiClass(jApiClasses, NonFinalStaysNonFinalInnerClass.class.getName()).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(getJApiClass(jApiClasses, FinalStaysFinalInnerClass.class.getName()).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
	}
}
