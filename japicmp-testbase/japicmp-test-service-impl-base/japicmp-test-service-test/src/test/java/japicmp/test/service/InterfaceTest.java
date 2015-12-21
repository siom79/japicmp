package japicmp.test.service;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.service.util.Helper.*;
import static japicmp.test.service.util.Helper.getArchive;
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
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-service-v1.jar"), getArchive("japicmp-test-service-v2.jar"));
	}

	@Test
	public void  testMethodRemovedFromInterface()
	{
		JApiClass jApiClass = getJApiClass(jApiClasses, InterfaceMethodAdded.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(false));
	}
}
