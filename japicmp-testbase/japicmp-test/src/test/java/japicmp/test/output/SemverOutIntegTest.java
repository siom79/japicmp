package japicmp.test.output;

import com.google.common.base.Strings;
import japicmp.cmp.JApiCmpArchive;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.filter.JavadocLikePackageFilter;
import japicmp.model.AccessModifier;
import japicmp.model.JApiClass;
import japicmp.output.semver.SemverOut;
import japicmp.test.util.Helper;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SemverOutIntegTest {

	@Test
	public void testSemver001_implementation_of_method_changes() {
		String lastPackage = "semver001.a";
		String string = getSemverDiff(lastPackage);
		assertEquals("0.0.1", string);
	}

	@Test
	public void testSemver010_added_deprecated_annotation() {
		String lastPackage = "semver010.a";
		String string = getSemverDiff(lastPackage);
		assertEquals("0.1.0", string);
	}

	@Test
	public void testSemver100_change_method() {
		String lastPackage = "semver100.a";
		String string = getSemverDiff(lastPackage);
		assertEquals("1.0.0", string);
	}

	@Test
	public void testSemver100_reduce_class_visibility() {
		String lastPackage = "semver100.b";
		String string = getSemverDiff(lastPackage);
		assertEquals("1.0.0", string);
	}

	@Test
	public void testSemver100_reduce_method_visibility() {
		String lastPackage = "semver100.c";
		String string = getSemverDiff(lastPackage);
		assertEquals("1.0.0", string);
	}

	@Test
	public void testSemver100_superclass_with_field() {
		String lastPackage = "semver100.d";
		String string = getSemverDiff(lastPackage);
		assertEquals("1.0.0", string);
	}

	@Test
	public void testSemver_class_with_private_final_field() {
		String lastPackage = "semver.finalfield";
		String string = getSemverDiff(lastPackage);
		assertEquals("0.0.1", string);
	}

	@Test
	public void testSemver_class_with_public_final_method() {
		String lastPackage = "semver.finalpublicmethod";
		String string = getSemverDiff(lastPackage);
		assertEquals("1.0.0", string);
	}

	@Test
	public void testSemver_private_inner_class_changes() {
		String lastPackage = "semver.privateinnerclass";
		String string = getSemverDiff(lastPackage);
		assertEquals("1.0.0", string); // private inner class becomes package protected -> change is binary incompatible
	}

	private String getSemverDiff(String lastPackage) {
		JApiCmpArchive oldFile = Helper.getArchive("japicmp-test-v1.jar");
		JApiCmpArchive newFile = Helper.getArchive("japicmp-test-v2.jar");
		return getSemverDiff(lastPackage, oldFile, newFile);
	}

	private String getSemverDiff(String lastPackage, JApiCmpArchive oldArchive, JApiCmpArchive newArchive) {
		AccessModifier accessModifier = AccessModifier.PRIVATE;
		JarArchiveComparator jarArchiveComparator = newComparator(lastPackage, accessModifier);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(oldArchive, newArchive);
		Options options = Options.newDefault();
		options.setNewArchives(Collections.singletonList(newArchive));
		options.setOldArchives(Collections.singletonList(oldArchive));
		options.setAccessModifier(accessModifier);
		options.setOutputOnlyModifications(false);
		return new SemverOut(options, jApiClasses).generate();
	}

	private JarArchiveComparator newComparator(String lastPackage, AccessModifier accessModifier) {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setAccessModifier(accessModifier);
		if (!Strings.isNullOrEmpty(lastPackage)) {
			String packageName = "japicmp.test." + lastPackage;
			options.getFilters().getIncludes().add(new JavadocLikePackageFilter(packageName, false));
		}
		return new JarArchiveComparator(options);
	}
}
