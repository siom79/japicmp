package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;
import japicmp.model.JApiReturnType;
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
		boolean returnValueStringToIntFound = false;
		boolean returnValueIntToStringFound = false;
		boolean returnValueRemainsIntFound = false;
		for (JApiMethod jApiMethod : jApiClass.getMethods()) {
			switch (jApiMethod.getName()) {
				case "returnValueStringToInt": {
					JApiReturnType returnType = jApiMethod.getReturnType();
					assertThat(returnType.getOldReturnType(), is("java.lang.String"));
					assertThat(returnType.getNewReturnType(), is("int"));
					returnValueStringToIntFound = true;
					break;
				}
				case "returnValueIntToString": {
					JApiReturnType returnType = jApiMethod.getReturnType();
					assertThat(returnType.getOldReturnType(), is("int"));
					assertThat(returnType.getNewReturnType(), is("java.lang.String"));
					returnValueIntToStringFound = true;
					break;
				}
				case "returnValueIntRemains": {
					JApiReturnType returnType = jApiMethod.getReturnType();
					assertThat(returnType.getOldReturnType(), is("int"));
					assertThat(returnType.getNewReturnType(), is("int"));
					returnValueRemainsIntFound = true;
					break;
				}
			}
		}
		assertThat(returnValueStringToIntFound, is(true));
		assertThat(returnValueIntToStringFound, is(true));
		assertThat(returnValueRemainsIntFound, is(true));
	}
}
