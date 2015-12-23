package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static japicmp.test.util.Helper.getJApiMethod;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class BasicTest {

	@Test
	public void test() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setAccessModifier(AccessModifier.PACKAGE_PROTECTED);
		options.setIncludeSynthetic(true);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		JApiClass jApiClassRemoved = getJApiClass(jApiClasses, "japicmp.test.Removed");
		JApiClass jApiClassAdded = getJApiClass(jApiClasses, Added.class.getName());
		JApiClass jApiClassUnchanged = getJApiClass(jApiClasses, Unchanged.class.getName());
		assertThat(jApiClassRemoved, is(notNullValue()));
		assertThat(jApiClassAdded, is(notNullValue()));
		assertThat(jApiClassUnchanged, is(notNullValue()));
		assertThat(jApiClassRemoved.getChangeStatus(), is(JApiChangeStatus.REMOVED));
		assertThat(jApiClassAdded.getChangeStatus(), is(JApiChangeStatus.NEW));
		assertThat(jApiClassUnchanged.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(getJApiMethod(jApiClassUnchanged.getMethods(), "unchangedMethod"), is(notNullValue()));
	}
}
