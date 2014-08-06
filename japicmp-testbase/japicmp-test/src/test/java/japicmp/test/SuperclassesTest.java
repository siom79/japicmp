package japicmp.test;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Optional;

public class SuperclassesTest {

	@Test
	public void test() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setModifierLevel(AccessModifier.PRIVATE);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		assertThat(getJApiClass(jApiClasses, Superclasses.class.getName()).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(getJApiClass(jApiClasses, Superclasses.class.getName()).getSuperclass().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(getJApiClass(jApiClasses, Superclasses.SuperClassChanges.class.getName()).getSuperclass().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(getJApiClass(jApiClasses, Superclasses.SuperClassChanges.class.getName()).getSuperclass().getOldSuperclass(),
				is(Optional.of(Superclasses.SuperclassA.class.getCanonicalName().replace(Superclasses.class.getSimpleName() + ".", Superclasses.class.getSimpleName() + "$"))));
		assertThat(getJApiClass(jApiClasses, Superclasses.SuperClassChanges.class.getName()).getSuperclass().getNewSuperclass(),
				is(Optional.of(Superclasses.SuperclassB.class.getCanonicalName().replace(Superclasses.class.getSimpleName() + ".", Superclasses.class.getSimpleName() + "$"))));
	}
}
