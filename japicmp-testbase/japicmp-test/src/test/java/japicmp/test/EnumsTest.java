package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiClass;
import japicmp.test.Enums.AbcToAb;
import japicmp.test.Enums.AbcToAbcd;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EnumsTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void testAddedEnumConstant() {
		JApiClass abcToAbcd = getJApiClass(jApiClasses, AbcToAbcd.class.getName());
		assertThat(abcToAbcd.isBinaryCompatible(), is(true));
	}

	@Test
	public void testRemovedEnumConstant() {
		JApiClass abcToAb = getJApiClass(jApiClasses, AbcToAb.class.getName());
		assertThat(abcToAb.isBinaryCompatible(), is(false));
	}
}
