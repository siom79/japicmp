package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;
import japicmp.model.JApiReturnType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static japicmp.test.util.Helper.getJApiMethod;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class MethodReturnTypeTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void testMethodReturnTypeUnchanged() {
		JApiClass jApiClass = getJApiClass(jApiClasses, MethodReturnType.class.getName());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodReturnTypeUnchanged");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		JApiReturnType jApiReturnType = jApiMethod.getReturnType();
		assertThat(jApiReturnType.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiReturnType.getOldReturnType(), is("int"));
		assertThat(jApiReturnType.getNewReturnType(), is("int"));
	}

	@Test
	public void testMethodReturnTypeChangesFromIntToString() {
		JApiClass jApiClass = getJApiClass(jApiClasses, MethodReturnType.class.getName());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodReturnTypeChangesFromIntToString");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		JApiReturnType jApiReturnType = jApiMethod.getReturnType();
		assertThat(jApiReturnType.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiReturnType.getOldReturnType(), is("int"));
		assertThat(jApiReturnType.getNewReturnType(), is("java.lang.String"));
	}

	@Test
	public void testMethodReturnTypeChangesFromStringToInt() {
		JApiClass jApiClass = getJApiClass(jApiClasses, MethodReturnType.class.getName());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodReturnTypeChangesFromStringToInt");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		JApiReturnType jApiReturnType = jApiMethod.getReturnType();
		assertThat(jApiReturnType.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiReturnType.getOldReturnType(), is("java.lang.String"));
		assertThat(jApiReturnType.getNewReturnType(), is("int"));
	}

	@Test
	public void testMethodReturnTypeChangesFromListToMap() {
		JApiClass jApiClass = getJApiClass(jApiClasses, MethodReturnType.class.getName());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodReturnTypeChangesFromListToMap");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		JApiReturnType jApiReturnType = jApiMethod.getReturnType();
		assertThat(jApiReturnType.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiReturnType.getOldReturnType(), is("java.util.List"));
		assertThat(jApiReturnType.getNewReturnType(), is("java.util.Map"));
	}

	@Test
	public void testMethodReturnTypeChangesFromVoidToInt() {
		JApiClass jApiClass = getJApiClass(jApiClasses, MethodReturnType.class.getName());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodReturnTypeChangesFromVoidToInt");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		JApiReturnType jApiReturnType = jApiMethod.getReturnType();
		assertThat(jApiReturnType.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiReturnType.getOldReturnType(), is("void"));
		assertThat(jApiReturnType.getNewReturnType(), is("int"));
	}
}
