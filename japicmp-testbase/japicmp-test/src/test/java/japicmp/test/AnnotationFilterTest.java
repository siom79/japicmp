package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.filter.AnnotationClassFilter;
import japicmp.filter.AnnotationFieldFilter;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.test.annotation.filter.AnnotatedClass;
import japicmp.test.annotation.filter.ClassWithMembersToExclude;
import org.junit.Assert;
import org.junit.Ignore;
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
		options.getFilters().getIncludes().add(new AnnotationClassFilter("@japicmp.test.annotation.filter.PublicAPI"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		getJApiClass(jApiClasses, AnnotatedClass.class.getName());
		assertThat(jApiClasses.size(), is(1));
	}

	@Test
	public void detectChangeOnAnnotatedClass() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new AnnotationClassFilter("@japicmp.test.annotation.filter.PublicAPI"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		final List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		Assert.assertEquals(1, jApiClasses.size());
		Assert.assertEquals(JApiChangeStatus.MODIFIED, jApiClasses.get(0).getChangeStatus());
	}

	@Test
	public void testElementsOnClassAreExcluded() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new AnnotationClassFilter("@japicmp.test.annotation.filter.Exclude"));
		options.getFilters().getExcludes().add(new AnnotationFieldFilter("@japicmp.test.annotation.filter.Exclude"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		final List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassWithMembersToExclude.class.getName());
		assertThat(jApiClass.getFields().size(), is(0));
	}
}
