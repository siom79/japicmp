package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiCompatibilityChange;
import japicmp.test.semver.finalpublicmethod.ClassWithFinalPublicMethod;
import japicmp.test.semver.finalpublicmethod.ClassWithFinalPublicMethodInSuperClass;
import org.hamcrest.MatcherAssert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class FinalMethodTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void testCompatibilityAddFinalToMethod() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassWithFinalPublicMethod.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.isSourceCompatible(), is(false));
		MatcherAssert.assertThat(
				getMethodCompatibilityChanges(jApiClass),
				containsInAnyOrder(JApiCompatibilityChange.METHOD_NOW_FINAL));
	}

	@Test
	public void testCompatibilityAddFinalToMethodAndMoveToSuperclass() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassWithFinalPublicMethodInSuperClass.SubClass.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.isSourceCompatible(), is(false));
		MatcherAssert.assertThat(
				getMethodCompatibilityChanges(jApiClass),
				containsInAnyOrder(JApiCompatibilityChange.METHOD_NOW_FINAL, JApiCompatibilityChange.METHOD_MOVED_TO_SUPERCLASS));
	}

	private static Collection<JApiCompatibilityChange> getMethodCompatibilityChanges(JApiClass jApiClass) {
		return jApiClass.getMethods().stream().flatMap(method -> method.getCompatibilityChanges().stream()).collect(Collectors.toList());
	}
}
