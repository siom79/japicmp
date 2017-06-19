package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.filter.JavadocLikePackageFilter;
import japicmp.model.JApiClass;
import japicmp.test.packageOne.PackageOne;
import japicmp.test.packageTwo.PackageTwo;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class PackageFilterTest {

	@Test
	public void onlyIncludeOnePackage() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new JavadocLikePackageFilter("japicmp.test.packageOne", false));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		getJApiClass(jApiClasses, PackageOne.class.getName());
		assertThat(jApiClasses.size(), is(1));
	}

	@Test
	public void onlyIncludeTwoPackages() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new JavadocLikePackageFilter("japicmp.test.packageOne", false));
		options.getFilters().getIncludes().add(new JavadocLikePackageFilter("japicmp.test.packageTwo", false));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		getJApiClass(jApiClasses, PackageOne.class.getName());
		getJApiClass(jApiClasses, PackageTwo.class.getName());
		assertThat(jApiClasses.size(), is(2));
	}

	@Test
	public void onlyExcludeOnePackage() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new JavadocLikePackageFilter("japicmp.test.packageOne", false));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		final List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		assertThatExceptionIsThrown(new Callback() {
			public void callback() {
				getJApiClass(jApiClasses, PackageOne.class.getName());
			}
		});
	}

	@Test
	public void excludePackageJapicmpTest() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new JavadocLikePackageFilter("japicmp.test", false));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		final List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		assertThat(jApiClasses.size(), is(0));
	}

	@Test
	public void includePackageJapicmpTest() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new JavadocLikePackageFilter("japicmp.test", false));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		final List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		assertThat(getJApiClass(jApiClasses, PackageOne.class.getName()), is(notNullValue()));
	}

	@Test
	public void includePackageOneWithWildcard() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new JavadocLikePackageFilter("japicmp.*.packageOne", false));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		final List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		assertThat(getJApiClass(jApiClasses, PackageOne.class.getName()), is(notNullValue()));
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
