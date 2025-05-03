package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class SortedOutputTest {
	private static List<JApiClass> jApiClasses;

	@BeforeAll
	public static void beforeClass() {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void testOutputIsSorted() {
		boolean sorted = true;
		String lastFqn = "";
		for (JApiClass jApiClass : jApiClasses) {
			String currentFqn = jApiClass.getFullyQualifiedName();
			if (lastFqn.compareToIgnoreCase(currentFqn) > 0) {
				sorted = false;
			}
			lastFqn = currentFqn;
			String lastMethodName = "";
			for (JApiMethod method : jApiClass.getMethods()) {
				String currentMethodName = method.getName();
				if (lastMethodName.compareToIgnoreCase(currentMethodName) > 0) {
					sorted = false;
				}
				lastMethodName = currentMethodName;
			}
		}
		assertThat(sorted, is(true));
	}
}
