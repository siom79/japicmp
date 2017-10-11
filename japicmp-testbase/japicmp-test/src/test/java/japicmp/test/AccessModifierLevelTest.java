package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;
import japicmp.test.util.Helper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.assertThatExceptionIsThrown;
import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static japicmp.test.util.Helper.getJApiField;
import static japicmp.test.util.Helper.getJApiMethod;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AccessModifierLevelTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setAccessModifier(AccessModifier.PUBLIC);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void testThatChangesBelowPublicDoNotChangeStatus() {
		JApiClass jApiClass = getJApiClass(jApiClasses, AccessModifierLevel.AccessModifierChangesBelowPublic.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getMethods().size(), is(0));
		assertThat(jApiClass.getFields().size(), is(0));
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

	@Test
	public void testPrivateFieldsDoNotAppearForPackageProtectedFilter() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setAccessModifier(AccessModifier.PACKAGE_PROTECTED);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		final JApiClass jApiClass = getJApiClass(jApiClasses, AccessModifierLevel.AccessModifierNotChangesForPrivate.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(getJApiField(jApiClass.getFields(), "fieldRemainsPackageProtected").getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(getJApiField(jApiClass.getFields(), "fieldRemainsProtected").getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(getJApiField(jApiClass.getFields(), "fieldRemainsPublic").getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThatExceptionIsThrown(new Helper.SimpleExceptionVerifier() {
			@Override
			public void execute() {
				getJApiField(jApiClass.getFields(), "fieldRemainsPrivate");
			}
		}, IllegalArgumentException.class);
	}

	@Test
	public void testPackageProtectedFieldsDoNotAppearForProtectedFilter() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setAccessModifier(AccessModifier.PROTECTED);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		final JApiClass jApiClass = getJApiClass(jApiClasses, AccessModifierLevel.AccessModifierNotChangesForPrivate.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(getJApiField(jApiClass.getFields(), "fieldRemainsProtected").getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(getJApiField(jApiClass.getFields(), "fieldRemainsPublic").getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThatExceptionIsThrown(new Helper.SimpleExceptionVerifier() {
			@Override
			public void execute() {
				getJApiField(jApiClass.getFields(), "fieldRemainsPrivate");
			}
		}, IllegalArgumentException.class);
		assertThatExceptionIsThrown(new Helper.SimpleExceptionVerifier() {
			@Override
			public void execute() {
				getJApiField(jApiClass.getFields(), "fieldRemainsPackageProtected");
			}
		}, IllegalArgumentException.class);
	}

	@Test
	public void testProtectedFieldsDoNotAppearForPublicFilter() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setAccessModifier(AccessModifier.PUBLIC);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		final JApiClass jApiClass = getJApiClass(jApiClasses, AccessModifierLevel.AccessModifierNotChangesForPrivate.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(getJApiField(jApiClass.getFields(), "fieldRemainsPublic").getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThatExceptionIsThrown(new Helper.SimpleExceptionVerifier() {
			@Override
			public void execute() {
				getJApiField(jApiClass.getFields(), "fieldRemainsPrivate");
			}
		}, IllegalArgumentException.class);
		assertThatExceptionIsThrown(new Helper.SimpleExceptionVerifier() {
			@Override
			public void execute() {
				getJApiField(jApiClass.getFields(), "fieldRemainsProtected");
			}
		}, IllegalArgumentException.class);
		assertThatExceptionIsThrown(new Helper.SimpleExceptionVerifier() {
			@Override
			public void execute() {
				getJApiField(jApiClass.getFields(), "fieldRemainsPackageProtected");
			}
		}, IllegalArgumentException.class);
	}
}
