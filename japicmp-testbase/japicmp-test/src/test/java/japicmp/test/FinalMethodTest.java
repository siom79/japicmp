package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiCompatibilityChange;
import japicmp.model.JApiCompatibilityChangeType;
import japicmp.test.semver.finalpublicmethod.ClassWithFinalPublicMethod;
import japicmp.test.semver.finalpublicmethod.ClassWithFinalPublicMethodInSuperClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

public class FinalMethodTest {
	private static List<JApiClass> jApiClasses;

	@BeforeAll
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
		assertThat(
				getMethodCompatibilityChanges(jApiClass),
				containsInAnyOrder(JApiCompatibilityChangeType.METHOD_NOW_FINAL));
	}

	@Test
	public void testCompatibilityAddFinalToMethodAndMoveToSuperclass() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassWithFinalPublicMethodInSuperClass.SubClass.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.isSourceCompatible(), is(false));
		assertThat(
				getMethodCompatibilityChanges(jApiClass),
				containsInAnyOrder(JApiCompatibilityChangeType.METHOD_NOW_FINAL, JApiCompatibilityChangeType.METHOD_MOVED_TO_SUPERCLASS));
	}

	private static Collection<JApiCompatibilityChangeType> getMethodCompatibilityChanges(JApiClass jApiClass) {
		return jApiClass.getMethods().stream()
			.flatMap(method -> method.getCompatibilityChanges().stream().map(JApiCompatibilityChange::getType).collect(Collectors.toList()).stream())
			.collect(Collectors.toList());
	}
}
