package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;

class AbstractClassesTest {
	private static List<JApiClass> jApiClasses;

	@BeforeAll
	public static void beforeClass() {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	void testThatNewInheritedAbstractMethodIsSourceIncompatible() {
		JApiClass jApiClass = getJApiClass(jApiClasses, AbstractClasses.AbstractClassGetsNewSuperclass.class.getName());
		MatcherAssert.assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		MatcherAssert.assertThat(jApiClass.isBinaryCompatible(), is(true));
		MatcherAssert.assertThat(jApiClass.isSourceCompatible(), is(false));
	}
}
