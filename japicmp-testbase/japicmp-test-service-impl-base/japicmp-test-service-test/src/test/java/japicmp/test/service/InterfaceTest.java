package japicmp.test.service;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.service.util.Helper.createClassPath;
import static japicmp.test.service.util.Helper.getArchive;
import static japicmp.test.service.util.Helper.getJApiClass;
import static japicmp.test.service.util.Helper.getJApiMethod;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class InterfaceTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setClassPathMode(JarArchiveComparatorOptions.ClassPathMode.TWO_SEPARATE_CLASSPATHS);
		options.setOldClassPath(createClassPath("v1"));
		options.setNewClassPath(createClassPath("v2"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-service-impl-v1.jar"), getArchive("japicmp-test-service-impl-v2.jar"));
	}

	@Test
	public void testMethodAdded() {
		JApiClass jApiClass = getJApiClass(jApiClasses, InterfaceMethodAddedImpl.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
	}

	@Test
	public void testMethodRemoved() {
		JApiClass jApiClass = getJApiClass(jApiClasses, InterfaceMethodRemovedImpl.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodRemoved");
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}
}
