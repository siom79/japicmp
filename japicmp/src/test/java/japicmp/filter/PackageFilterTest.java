package japicmp.filter;

import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiClass;
import japicmp.util.CtClassBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static japicmp.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;

class PackageFilterTest {

	@Test
	void testWithoutWildcard() {
		JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package", false);
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(true));
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne")), is(true));
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.")), is(false));
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.packag")), is(false));
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test")), is(false));
	}

        @Test
		void testWithoutWildcardExclusive() {
                JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package", true);
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(true));
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne")), is(false));
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.")), is(false));
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.packag")), is(false));
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test")), is(false));
        }

	@Test
	void testWithWildcardAfterDot() {
		JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package.*", false);
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(false));
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne")), is(true));
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.p2")), is(true));
	}

        @Test
		void testWithWildcardAfterDotExclusive() {
                JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package.*", true);
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(false));
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne")), is(true));
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.p2")), is(true));
        }

	@Test
	void testWithWildcardWithoutDot() {
		JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package*", false);
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(true));
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne")), is(true));
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.p2")), is(true));
	}

        @Test
		void testWithWildcardWithoutDotExclusive() {
                JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package*", true);
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(true));
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne")), is(true));
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.p2")), is(true));
        }

	@Test
	void testWithWildcardAndFollowingPackagename() {
		JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package.*.test", false);
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(false));
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.p.test")), is(true));
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test")), is(true));
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test2")), is(false));
	}

        @Test
		void testWithWildcardAndFollowingPackagenameExclusive() {
                JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package.*.test", true);
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(false));
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.p.test")), is(true));
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.p.test.test2")), is(false));
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test")), is(true));
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test.test2")), is(false));
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test2")), is(false));
        }

	@Test
	void testWithOnlyWildcard() {
		JavadocLikePackageFilter pf = new JavadocLikePackageFilter("*", false);
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(true));
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.p.test")), is(true));
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test")), is(true));
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test2")), is(true));
	}

        @Test
		void testWithOnlyWildcardExclusive() {
                JavadocLikePackageFilter pf = new JavadocLikePackageFilter("*", true);
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(true));
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.p.test")), is(true));
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test")), is(true));
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test2")), is(true));
        }

	@Test
	void testMatchAgainstDefaultPackage() {
		JavadocLikePackageFilter pf = new JavadocLikePackageFilter("foo", false);
		MatcherAssert.assertThat(pf.matches(createCtClassForPackage("")), is(false));
	}

        @Test
		void testMatchAgainstDefaultPackageExclusive() {
                JavadocLikePackageFilter pf = new JavadocLikePackageFilter("foo", true);
                MatcherAssert.assertThat(pf.matches(createCtClassForPackage("")), is(false));
        }

	private CtClass createCtClassForPackage(String packageName) {
		String className = packageName + (packageName.isEmpty() ? "" : ".") + "Test";
		return CtClassBuilder.create().name(className).addToClassPool(new ClassPool());
	}

	@Test
	void testExcludeInnerPackageWhenOuterPackageIsIncluded() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new JavadocLikePackageFilter("include", false));
		options.getFilters().getExcludes().add(new JavadocLikePackageFilter("include.exclude", false));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass toInclude = CtClassBuilder.create().name("include.ToInclude").addToClassPool(classPool);
				CtClass toExclude = CtClassBuilder.create().name("include.exclude.ToExclude").addToClassPool(classPool);
				return Arrays.asList(toInclude, toExclude);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass toInclude = CtClassBuilder.create().name("include.ToInclude").addToClassPool(classPool);
				CtClass toExclude = CtClassBuilder.create().name("include.exclude.ToExclude").addToClassPool(classPool);
				return Arrays.asList(toInclude, toExclude);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(1));
		JApiClass jApiClass = getJApiClass(jApiClasses, "include.ToInclude");
		MatcherAssert.assertThat(jApiClass.isBinaryCompatible(), is(true));
		MatcherAssert.assertThat(jApiClass.isSourceCompatible(), is(true));
	}

        @Test
		void testIncludeOuterPackageWhenInnerPackageIsExcludedExclusive() throws Exception {
                JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
                options.getFilters().getIncludes().add(new JavadocLikePackageFilter("exclude.include", true));
                List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
                        @Override
                        public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
                                CtClass toExclude1 = CtClassBuilder.create().name("exclude.ToExclude").addToClassPool(classPool);
                                CtClass toInclude = CtClassBuilder.create().name("exclude.include.ToInclude").addToClassPool(classPool);
                                CtClass toExclude2 = CtClassBuilder.create().name("exclude.include.exclude.ToExclude").addToClassPool(classPool);
                                return Arrays.asList(toExclude1, toInclude, toExclude2);
                        }

                        @Override
                        public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
                                CtClass toExclude1 = CtClassBuilder.create().name("exclude.ToExclude").addToClassPool(classPool);
                                CtClass toInclude = CtClassBuilder.create().name("exclude.include.ToInclude").addToClassPool(classPool);
                                CtClass toExclude2 = CtClassBuilder.create().name("exclude.include.exclude.ToExclude").addToClassPool(classPool);
                                return Arrays.asList(toExclude1, toInclude, toExclude2);
                        }
                });
                MatcherAssert.assertThat(jApiClasses.size(), is(1));
                JApiClass jApiClass = getJApiClass(jApiClasses, "exclude.include.ToInclude");
                MatcherAssert.assertThat(jApiClass.isBinaryCompatible(), is(true));
                MatcherAssert.assertThat(jApiClass.isSourceCompatible(), is(true));
        }
}
