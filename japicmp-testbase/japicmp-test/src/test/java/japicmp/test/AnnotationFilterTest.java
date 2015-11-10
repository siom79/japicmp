package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.filter.AnnotationFilter;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.test.annotation.filter.AnnotatedClass;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test to ensure that annotation-based includes and excludes are working.
 */
public class AnnotationFilterTest {

	@Test
	public void testFilterSelection() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new AnnotationFilter("@japicmp.test.annotation.filter.PublicAPI"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		getJApiClass(jApiClasses, AnnotatedClass.class.getName());
		assertThat(jApiClasses.size(), is(1));
	}


	@Test
	public void detectChangeOnAnnotatedClass() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new AnnotationFilter("@japicmp.test.annotation.filter.PublicAPI"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		final List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		Assert.assertEquals(1, jApiClasses.size());
		Assert.assertEquals(JApiChangeStatus.MODIFIED, jApiClasses.get(0).getChangeStatus());
	}


	private interface Callback {
		void callback();
	}

	private void assertThatExceptionIsThrown(Callback callback) {
		boolean exception = false;
		try {
			callback.callback();
		} catch (Exception e) {
			exception = true;
		}
		assertThat(exception, is(true));
	}
}
