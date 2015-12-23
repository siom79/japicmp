package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MethodsTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void testReturnValueStringToInt() {
		JApiClass jApiClass = getJApiClass(jApiClasses, Methods.class.getName());
		for (JApiMethod jApiMethod : jApiClass.getMethods()) {
			if (jApiMethod.getName().equals("returnValueStringToInt")) {
				if (jApiMethod.getReturnType().equals("int")) {
					assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
				} else if (jApiMethod.getReturnType().equals("java.lang.String")) {
					assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.REMOVED));
				}
			} else if (jApiMethod.getName().equals("returnValueIntToString")) {
				if (jApiMethod.getReturnType().equals("int")) {
					assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.REMOVED));
				} else if (jApiMethod.getReturnType().equals("java.lang.String")) {
					assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
				}
			} else if (jApiMethod.getName().equals("returnValueRemainsInt")) {
				assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
			}
		}
	}
}
