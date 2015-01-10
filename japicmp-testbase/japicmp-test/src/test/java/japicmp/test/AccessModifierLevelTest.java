package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AccessModifierLevelTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void testThatChangesBelowPublicDoNotChangeStatus() {
		assertThat(getJApiClass(jApiClasses, AccessModifierLevel.class.getName()).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
	}
}
