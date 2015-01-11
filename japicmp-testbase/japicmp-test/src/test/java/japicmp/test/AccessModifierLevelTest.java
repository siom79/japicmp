package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.*;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.Access;
import java.util.List;

import static japicmp.test.util.Helper.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AccessModifierLevelTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setModifierLevel(AccessModifier.PUBLIC);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void testThatChangesBelowPublicDoNotChangeStatus() {
		JApiClass jApiClass = getJApiClass(jApiClasses, AccessModifierLevel.AccessModifierChangesBelowPublic.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getMethods().size(), is(2));
		for (JApiMethod method : jApiClass.getMethods()) {
			assertThat(method.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		}
		assertThat(jApiClass.getFields().size(), is(2));
		for (JApiField field : jApiClass.getFields()) {
			assertThat(field.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		}
	}

	@Test
	public void testChangesFromPrivateToPublic() {
		JApiClass jApiClass = getJApiClass(jApiClasses, AccessModifierLevel.AccessModifierChangesFromPrivateToPublic.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodFromPrivateToPublic");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiMethod.getAccessModifier().getOldModifier().get(), is(AccessModifier.PRIVATE));
		assertThat(jApiMethod.getAccessModifier().getNewModifier().get(), is(AccessModifier.PUBLIC));
	}

	@Test
	public void testChangesFromPublicToPrivate() {
		JApiClass jApiClass = getJApiClass(jApiClasses, AccessModifierLevel.AccessModifierChangesFromPublicToPrivate.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodFromPublicToPrivate");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiMethod.getAccessModifier().getOldModifier().get(), is(AccessModifier.PUBLIC));
		assertThat(jApiMethod.getAccessModifier().getNewModifier().get(), is(AccessModifier.PRIVATE));
	}
}
