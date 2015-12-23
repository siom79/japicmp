package japicmp.filter;

import japicmp.util.CtClassBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PackageFilterTest {

	@Test
	public void testWithoutWildcard() {
		JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package");
		assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.")), is(false));
		assertThat(pf.matches(createCtClassForPackage("de.test.packag")), is(false));
		assertThat(pf.matches(createCtClassForPackage("de.test")), is(false));
	}

	@Test
	public void testWithWildcardAfterDot() {
		JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package.*");
		assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(false));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.p2")), is(true));
	}

	@Test
	public void testWithWildcardWithoutDot() {
		JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package*");
		assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.p2")), is(true));
	}

	@Test
	public void testWithWildcardAndFollowingPackagename() {
		JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package.*.test");
		assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(false));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.p.test")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test2")), is(false));
	}

	@Test
	public void testWithOnlyWildcard() {
		JavadocLikePackageFilter pf = new JavadocLikePackageFilter("*");
		assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.p.test")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test2")), is(true));
	}

	private CtClass createCtClassForPackage(String packageName) {
		return CtClassBuilder.create().name(packageName + ".Test").addToClassPool(new ClassPool());
	}
}
