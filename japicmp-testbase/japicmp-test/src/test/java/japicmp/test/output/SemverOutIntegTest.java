package japicmp.test.output;

import static org.junit.Assert.assertEquals;

import japicmp.output.semver.SemverOut;
import org.junit.Ignore;
import org.junit.Test;

public class SemverOutIntegTest {

	public static final String PATCH = "0.0.1";
	public static final String MINOR = "0.1.0";
	public static final String MAJOR = "1.0.0";

	@Test
	public void testSemver_implementation_of_method_changes() {
		// should be PATCH because implementation details does not affect public api
		// but it could be MINOR or MAJOR, but this is hard to detect automatically
		assertSemverDiffByPackage(PATCH, "implementationChange");
	}

	@Test
	public void testSemver_added_public_source_annotation() {
		// should be PATCH because it is not visible at runtime
		assertSemverDiffByPackage(PATCH, "addedPublicDocAnnotation");
	}

	@Test
	@Ignore("TODO") // TODO
	public void testSemver_removed_interface_from_default_class() {
		// should be PATCH because package protected classes are not part of public API
		// "Package by feature, not layer" -> http://www.javapractices.com/topic/TopicAction.do?Id=205
		assertSemverDiffByPackage(PATCH, "removedInterfaceFromDefault");
	}

	@Test
	public void testSemver_added_public_runtime_annotation() {
		// should be MINOR because it is an new information for public API
		assertSemverDiffByPackage(MINOR, "addedCustomPublicAnnotation");
	}

	@Test
	public void testSemver_increase_visibility_of_class_from_default_to_public() {
		// should be MINOR because a new class will be visible for all
		assertSemverDiffByPackage(MINOR, "changesFromDefaultToPublic");
	}

	@Test
	public void testSemver_change_method() {
		// should be MAJOR because it is an API break
		assertSemverDiffByPackage(MAJOR, "publicMethodParamChange");
	}

	@Test
	public void testSemver_reduce_class_visibility() {
		// should be MAJOR because it is an API break
		assertSemverDiffByPackage(MAJOR, "changesFromPublicToDefault");
	}

	@Test
	public void testSemver_reduce_method_visibility() {
		// should be MAJOR because it is an API break
		assertSemverDiffByPackage(MAJOR, "changesMethodFromPublicToDefault");
	}

	@Test
	public void testSemver_superclass_with_field() {
		// should be MAJOR because it is an API break
		assertSemverDiffByPackage(MAJOR, "superclassWithField");
	}

	@Test
	public void testSemver_removed_interface_from_public_class() {
		// should be MAJOR because it is an API break e.g. instanceof checks
		assertSemverDiffByPackage(MAJOR, "removedInterfaceFromPublic");
	}

	@Test
	public void testSemver_removed_interface_from_public_class_without_methods() {
		// should be MAJOR because it is an API break e.g. instanceof checks
		assertSemverDiffByPackage(MAJOR, "removedInterfaceFromPublicWithoutMethods");
	}

	private void assertSemverDiffByPackage(String expectedSemverDiff, String lastPackageName) {
		assertEquals(expectedSemverDiff, getSemverDiffByPackage(lastPackageName));
	}

	private String getSemverDiffByPackage(String lastPackage) {
		OutputTestHelper.Config config = //
				OutputTestHelper.newTestConfigWithInclude("japicmp.test.semver." + lastPackage);

		return new SemverOut().generate(config.classes()).get();
	}

}
