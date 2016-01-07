package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.filter.AnnotationClassFilter;
import japicmp.filter.AnnotationFieldFilter;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;
import japicmp.test.annotation.filter.AnnotatedClass;
import japicmp.test.annotation.filter.ClassWithInnerClass;
import japicmp.test.annotation.filter.ClassWithMembersToExclude;
import japicmp.test.annotation.filter.ExcludedClass;
import japicmp.test.annotation.filter.exclpckg.Excluded;
import japicmp.test.annotation.filter.inclpckg.Included;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static japicmp.test.util.Helper.getJApiMethod;
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
		assertThat(jApiClasses.size(), is(3));
	}

	@Test
	public void detectChangeOnAnnotatedClass() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new AnnotationClassFilter("@japicmp.test.annotation.filter.PublicAPI"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		final List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		Assert.assertEquals(3, jApiClasses.size());
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

	@Test
	public void testThatNewInnerClassIsDetected() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new AnnotationClassFilter("@japicmp.test.annotation.filter.PublicAPI"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		final List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		Assert.assertEquals(3, jApiClasses.size());
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassWithInnerClass.class.getName() + "$NewInnerClass");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.NEW));
	}

	@Test
	public void testThatNewMethodOfAnnotatedClassIsDetected() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new AnnotationClassFilter("@japicmp.test.annotation.filter.PublicAPI"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		final List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		Assert.assertEquals(3, jApiClasses.size());
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassWithInnerClass.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "newMethod");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
	}

	@Test
	public void testPackagesAreExcluded() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new AnnotationClassFilter("@japicmp.test.annotation.filter.Exclude"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		final List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		boolean exceptionCaught = false;
		try {
			getJApiClass(jApiClasses, Excluded.class.getName());
		} catch (IllegalArgumentException e) {
			exceptionCaught = true;
		}
		assertThat(exceptionCaught, is(true));
	}

	@Test
	public void testPackagesAreIncluded() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new AnnotationClassFilter("@japicmp.test.annotation.filter.Include"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		final List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		assertThat(jApiClasses.size(), is(1));
		JApiClass jApiClass = getJApiClass(jApiClasses, Included.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		JApiMethod methodAdded = getJApiMethod(jApiClass.getMethods(), "methodAdded");
		assertThat(methodAdded.getChangeStatus(), is(JApiChangeStatus.NEW));
	}

	@Test
	public void testClassExcludedWithInnerAnnotation() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new AnnotationClassFilter("@japicmp.test.annotation.filter.InterfaceStability$Exclude"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		final List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		boolean exceptionCaught = false;
		try {
			getJApiClass(jApiClasses, ExcludedClass.class.getName());
		} catch (IllegalArgumentException e) {
			exceptionCaught = true;
		}
		assertThat(exceptionCaught, is(true));
	}
}
