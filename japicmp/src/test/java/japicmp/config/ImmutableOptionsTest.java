package japicmp.config;

import static org.junit.Assert.assertEquals;

import java.io.File;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import japicmp.model.AccessModifier;
import org.junit.Test;

public class ImmutableOptionsTest {

	@Test
	public void testEquals() {
		File fileOld = new File("old.jar");
		File fileNew = new File("new.jar");
		ImmutableList<PackageFilter> include = ImmutableList.of(new PackageFilter("**.inc.**"));
		ImmutableList<PackageFilter> exclude = ImmutableList.of(new PackageFilter("**.ex.**"));

		Options optionsA = new Options();
		optionsA.setOldArchive(fileOld);
		optionsA.setNewArchive(fileNew);
		optionsA.setXmlOutputFile(Optional.of("y.xml"));
		optionsA.setHtmlOutputFile(Optional.of("h.html"));
		optionsA.setAccessModifier(AccessModifier.PACKAGE_PROTECTED);
		optionsA.setPackagesInclude(include);
		optionsA.setPackagesExclude(exclude);
		optionsA.setOutputOnlyBinaryIncompatibleModifications(true);
		optionsA.setShowOnlySemverDiff(true);
		optionsA.setOutputOnlyModifications(true);

		ImmutableOptions c = ImmutableOptions.builder() //
				.withOldArchive(fileOld) //
				.withNewArchive(fileNew) //
				.withXmlOutputFileName("y.xml") //
				.withHtmlOutputFileName("h.html") //
				.withAccessModifier(AccessModifier.PACKAGE_PROTECTED) //
				.withPackagesInclude("**.inc.**") //
				.withPackagesExclude("**.ex.**") //
				.withOnlyBinaryIncompatible(true) //
				.withOnlySemverDiff(true) //
				.withOnlyModifications(true) //
				.build();

		ImmutableOptions a = ImmutableOptions.toImmutable(optionsA);

		assertEquals(c, a);
		assertEquals(c.hashCode(), a.hashCode());
	}

	@Test
	public void buildMinimal() {
		File fileOld = new File("old.jar");
		File fileNew = new File("new.jar");
		ImmutableOptions opt = ImmutableOptions.builder().withOldArchive(fileOld) //
				.withNewArchive(fileNew) //
				.withAccessModifier(AccessModifier.PROTECTED) //
				.withoutXmlOutputFileName() //
				.withoutHtmlOutputFileName() //
				.build();

		assertEquals(AccessModifier.PROTECTED, opt.getAccessModifier());
		assertEquals(fileNew, opt.getNewArchive());
		assertEquals(fileOld, opt.getOldArchive());
		assertEquals(ImmutableList.<PackageFilter>of(), opt.getPackagesExclude());
		assertEquals(ImmutableList.<PackageFilter>of(), opt.getPackagesInclude());
		assertEquals(false, opt.isOutputOnlyBinaryIncompatibleModifications());
		assertEquals(false, opt.isOutputOnlyModifications());
		assertEquals(false, opt.isOnlySemverDiff());
	}

	@Test
	public void testParams() {

		File fileOld = new File("old.jar");
		File fileNew = new File("new.jar");
		ImmutableList<PackageFilter> include = ImmutableList.of(new PackageFilter("a"));
		ImmutableList<PackageFilter> exclude = ImmutableList.of(new PackageFilter("b"));
		ImmutableOptions opt = ImmutableOptions.builder() //
				.withOldArchive(fileOld) //
				.withNewArchive(fileNew) //
				.withXmlOutputFileName("a.xml") //
				.withHtmlOutputFileName("b.html") //
				.withAccessModifier(AccessModifier.PUBLIC) //
				.withPackagesInclude(include) //
				.withPackagesExclude(exclude) //
				.build();

		assertEquals(AccessModifier.PUBLIC, opt.getAccessModifier());
		assertEquals(fileNew, opt.getNewArchive());
		assertEquals(fileOld, opt.getOldArchive());
		assertEquals(Optional.of("a.xml"), opt.getXmlOutputFile());
		assertEquals(Optional.of("b.html"), opt.getHtmlOutputFile());
		assertEquals(exclude, opt.getPackagesExclude());
		assertEquals(include, opt.getPackagesInclude());
		assertEquals(false, opt.isOutputOnlyBinaryIncompatibleModifications());
		assertEquals(false, opt.isOutputOnlyModifications());
		assertEquals(false, opt.isOnlySemverDiff());
	}

	@Test
	public void testCopy() {
		// GIVEN
		File fileOld = new File("old");
		File fileNew = new File("new");
		ImmutableList<PackageFilter> include = ImmutableList.of(new PackageFilter("c"));
		ImmutableList<PackageFilter> exclude = ImmutableList.of(new PackageFilter("d"));
		ImmutableOptions iOpt = ImmutableOptions.builder() //
				.withOldArchive(fileOld) //
				.withNewArchive(fileNew) //
				.withXmlOutputFileName("x.xml") //
				.withHtmlOutputFileName("y.html") //
				.withAccessModifier(AccessModifier.PRIVATE) //
				.withPackagesInclude(include) //
				.withPackagesExclude(exclude) //
				.withOnlyBinaryIncompatible(true) //
				.withOnlySemverDiff(true) //
				.withOnlyModifications(true) //
				.build();

		// WHEN
		Options opt = iOpt.copyToOptions();

		// THEN
		assertEquals(AccessModifier.PRIVATE, opt.getAccessModifier());
		assertEquals(fileNew, opt.getNewArchive());
		assertEquals(fileOld, opt.getOldArchive());
		assertEquals(Optional.of("x.xml"), opt.getXmlOutputFile());
		assertEquals(Optional.of("y.html"), opt.getHtmlOutputFile());
		assertEquals(exclude, opt.getPackagesExclude());
		assertEquals(include, opt.getPackagesInclude());
		assertEquals(true, opt.isOutputOnlyBinaryIncompatibleModifications());
		assertEquals(true, opt.isOutputOnlyModifications());
		assertEquals(true, opt.isOnlySemverDiff());

	}

}
