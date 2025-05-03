package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SuperclassesTest {
	private List<JApiClass> jApiClasses;

	@BeforeEach
	public void before() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void test() {
		assertThat(getJApiClass(jApiClasses, Superclasses.class.getName()).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(getJApiClass(jApiClasses, Superclasses.class.getName()).getSuperclass().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(getJApiClass(jApiClasses, Superclasses.SuperClassChanges.class.getName()).getSuperclass().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(getJApiClass(jApiClasses, Superclasses.SuperClassChanges.class.getName()).getSuperclass().getOldSuperclassName(),
			is(Optional.of(Superclasses.SuperclassA.class.getCanonicalName().replace(Superclasses.class.getSimpleName() + ".", Superclasses.class.getSimpleName() + "$"))));
		assertThat(getJApiClass(jApiClasses, Superclasses.SuperClassChanges.class.getName()).getSuperclass().getNewSuperclassName(),
			is(Optional.of(Superclasses.SuperclassB.class.getCanonicalName().replace(Superclasses.class.getSimpleName() + ".", Superclasses.class.getSimpleName() + "$"))));
	}

	@Test
	public void superclassChanges() {
		JApiClass jApiClass = getJApiClass(jApiClasses, Superclasses.SuperClassChanges.class.getName());
		assertThat(jApiClass.isBinaryCompatible(), is(false));
	}
}
