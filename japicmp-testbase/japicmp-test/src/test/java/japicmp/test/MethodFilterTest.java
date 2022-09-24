package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.filter.JavadocLikeBehaviorFilter;
import japicmp.model.JApiClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MethodFilterTest {

	@Test
	public void testMethodIsExcluded() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new JavadocLikeBehaviorFilter(MethodFilter.class.getName() + "#methodToExclude()"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		JApiClass jApiClass = getJApiClass(jApiClasses, MethodFilter.class.getName());
		assertThat(jApiClass, hasJApiMethodWithName("methodToInclude"));
		assertThat(jApiClass, hasNoJApiMethodWithName("methodToExclude"));
	}

	@Test
	public void testMethodIsIncluded() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new JavadocLikeBehaviorFilter(MethodFilter.class.getName() + "#methodToInclude()"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		assertThat(jApiClasses.size(), is(1));
		JApiClass jApiClass = getJApiClass(jApiClasses, MethodFilter.class.getName());
		assertThat(jApiClass, hasJApiMethodWithName("methodToInclude"));
		assertThat(jApiClass, hasNoJApiMethodWithName("methodToExclude"));
	}
}
