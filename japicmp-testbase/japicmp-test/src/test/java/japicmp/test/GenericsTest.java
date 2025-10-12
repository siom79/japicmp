package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiClass;
import japicmp.model.JApiCompatibilityChange;
import japicmp.model.JApiCompatibilityChangeType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GenericsTest {

	private static List<JApiClass> jApiClasses;

	@BeforeAll
	public static void beforeClass() {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	void testNewImplementsSupplier() {
		JApiClass jApiClass = getJApiClass(jApiClasses, Generics.NewImplementsSupplier.class.getName());
		assertThat(jApiClass.getCompatibilityChanges().size(), is(1));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.INTERFACE_ADDED)));
	}

	@Test
	void testNewImplementsConsumer() {
		JApiClass jApiClass = getJApiClass(jApiClasses, Generics.NewImplementsConsumer.class.getName());
		assertThat(jApiClass.getCompatibilityChanges().size(), is(2));
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.INTERFACE_ADDED)));
		// Consumer.andThen()
		assertThat(jApiClass.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_DEFAULT_ADDED_IN_IMPLEMENTED_INTERFACE)));
	}
}
