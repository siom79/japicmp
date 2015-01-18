package japicmp.test.output;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.config.PackageFilter;
import japicmp.model.AccessModifier;
import japicmp.model.JApiClass;
import japicmp.output.semver.SemverOut;
import japicmp.test.util.Helper;
import org.junit.Test;

public class SemverOutIntegTest {

	public static final String PATCH = "0.0.1";
	public static final String MINOR = "0.1.0";
	public static final String MAJOR = "1.0.0";

	@Test
	public void testSemver001_implementation_of_method_changes() {
		assertSemverDiffByPackage(PATCH, "implementationChange");
	}

	@Test
	public void testSemver010_added_deprecated_annotation() {
		assertSemverDiffByPackage(MINOR, "addedPublicDeprecation"); // TODO is this MINOR?
	}

	@Test
	public void testSemver010_increase_visibility_of_class_from_default_to_public() {
		assertSemverDiffByPackage(MINOR, "changesFromDefaultToPublic");
	}

	@Test
	public void testSemver100_change_method() {
		assertSemverDiffByPackage(MAJOR, "publicMethodParamChange");
	}

	@Test
	public void testSemver100_reduce_class_visibility() {
		assertSemverDiffByPackage(MAJOR, "changesFromPublicToDefault");
	}

	@Test
	public void testSemver100_reduce_method_visibility() {
		assertSemverDiffByPackage(MAJOR, "changesMethodFromPublicToDefault");
	}

	@Test
	public void testSemver100_superclass_with_field() {
		assertSemverDiffByPackage(MAJOR, "superclassWithField");
	}

	private void assertSemverDiffByPackage(String expectedSemverDiff, String lastPackageName) {
		assertEquals(expectedSemverDiff, getSemverDiffByPackage(lastPackageName));
	}

	private String getSemverDiffByPackage(String lastPackage) {
		File oldFile = Helper.getArchive("japicmp-test-v1.jar");
		File newFile = Helper.getArchive("japicmp-test-v2.jar");

		List<JApiClass> jApiClasses = newComparatorFor(lastPackage).compare(oldFile, newFile);
		Options options = new Options();
		options.setNewArchive(newFile);
		options.setOldArchive(oldFile);
		options.setAccessModifier(AccessModifier.PRIVATE);
		options.setOutputOnlyModifications(false);
		return new SemverOut(options, jApiClasses).value();
	}

	private JarArchiveComparator newComparatorFor(String lastPackage) {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		String packageName = "japicmp.test.semver." + lastPackage;
		options.getPackagesInclude().add(new PackageFilter(packageName));
		return new JarArchiveComparator(options);
	}
}
