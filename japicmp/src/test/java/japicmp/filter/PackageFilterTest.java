package japicmp.filter;

import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiClass;
import japicmp.util.CtClassBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static japicmp.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PackageFilterTest {

	@Test
	public void testWithoutWildcard() {
		JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package", false);
		assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.")), is(false));
		assertThat(pf.matches(createCtClassForPackage("de.test.packag")), is(false));
		assertThat(pf.matches(createCtClassForPackage("de.test")), is(false));
	}
	
        @Test
        public void testWithoutWildcardExclusive() {
                JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package", true);
                assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(true));
                assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne")), is(false));
                assertThat(pf.matches(createCtClassForPackage("de.test.package.")), is(false));
                assertThat(pf.matches(createCtClassForPackage("de.test.packag")), is(false));
                assertThat(pf.matches(createCtClassForPackage("de.test")), is(false));
        }	

	@Test
	public void testWithWildcardAfterDot() {
		JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package.*", false);
		assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(false));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.p2")), is(true));
	}
	
        @Test
        public void testWithWildcardAfterDotExclusive() {
                JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package.*", true);
                assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(false));
                assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne")), is(true));
                assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.p2")), is(true));
        }	

	@Test
	public void testWithWildcardWithoutDot() {
		JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package*", false);
		assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.p2")), is(true));
	}
	
        @Test
        public void testWithWildcardWithoutDotExclusive() {
                JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package*", true);
                assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(true));
                assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne")), is(true));
                assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.p2")), is(true));
        }	

	@Test
	public void testWithWildcardAndFollowingPackagename() {
		JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package.*.test", false);
		assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(false));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.p.test")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test2")), is(false));
	}
	
        @Test
        public void testWithWildcardAndFollowingPackagenameExclusive() {
                JavadocLikePackageFilter pf = new JavadocLikePackageFilter("de.test.package.*.test", true);
                assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(false));
                assertThat(pf.matches(createCtClassForPackage("de.test.package.p.test")), is(true));
                assertThat(pf.matches(createCtClassForPackage("de.test.package.p.test.test2")), is(false));
                assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test")), is(true));
                assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test.test2")), is(false));
                assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test2")), is(false));
        }	

	@Test
	public void testWithOnlyWildcard() {
		JavadocLikePackageFilter pf = new JavadocLikePackageFilter("*", false);
		assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.p.test")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test")), is(true));
		assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test2")), is(true));
	}
	
        @Test
        public void testWithOnlyWildcardExclusive() {
                JavadocLikePackageFilter pf = new JavadocLikePackageFilter("*", true);
                assertThat(pf.matches(createCtClassForPackage("de.test.package")), is(true));
                assertThat(pf.matches(createCtClassForPackage("de.test.package.p.test")), is(true));
                assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test")), is(true));
                assertThat(pf.matches(createCtClassForPackage("de.test.package.packageOne.test2")), is(true));
        }

	@Test
	public void testMatchAgainstDefaultPackage() {
		JavadocLikePackageFilter pf = new JavadocLikePackageFilter("foo", false);
		assertThat(pf.matches(createCtClassForPackage("")), is(false));
	}
	
        @Test
        public void testMatchAgainstDefaultPackageExclusive() {
                JavadocLikePackageFilter pf = new JavadocLikePackageFilter("foo", true);
                assertThat(pf.matches(createCtClassForPackage("")), is(false));
        }

	private CtClass createCtClassForPackage(String packageName) {
		String className = packageName + (packageName.isEmpty() ? "" : ".") + "Test";
		return CtClassBuilder.create().name(className).addToClassPool(new ClassPool());
	}

	@Test
	public void testExcludeInnerPackageWhenOuterPackageIsIncluded() throws Exception {
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
		assertThat(jApiClasses.size(), is(1));
		JApiClass jApiClass = getJApiClass(jApiClasses, "include.ToInclude");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
	}
	
        @Test
        public void testIncludeOuterPackageWhenInnerPackageIsExcludedExclusive() throws Exception {
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
                assertThat(jApiClasses.size(), is(1));
                JApiClass jApiClass = getJApiClass(jApiClasses, "exclude.include.ToInclude");
                assertThat(jApiClass.isBinaryCompatible(), is(true));
                assertThat(jApiClass.isSourceCompatible(), is(true));
        }
}
