package japicmp.cmp;

import japicmp.config.PackageFilter;
import japicmp.model.JApiClass;
import japicmp.util.ClassesHelper;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static japicmp.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PackageFilterTest {

    @Test
    public void testWithoutWildcard() {
        PackageFilter pf = new PackageFilter("de.test.package");
        assertThat(pf.matches("de.test.package"), is(true));
        assertThat(pf.matches("de.test.package.packageOne"), is(true));
        assertThat(pf.matches("de.test.package."), is(false));
        assertThat(pf.matches("de.test.packag"), is(false));
        assertThat(pf.matches("de.test"), is(false));
    }

    @Test
    public void testWithWildcardAfterDot() {
        PackageFilter pf = new PackageFilter("de.test.package.*");
        assertThat(pf.matches("de.test.package"), is(false));
        assertThat(pf.matches("de.test.package.packageOne"), is(true));
        assertThat(pf.matches("de.test.package.packageOne.p2"), is(true));
    }

    @Test
    public void testWithWildcardWithoutDot() {
        PackageFilter pf = new PackageFilter("de.test.package*");
        assertThat(pf.matches("de.test.package"), is(true));
        assertThat(pf.matches("de.test.package.packageOne"), is(true));
        assertThat(pf.matches("de.test.package.packageOne.p2"), is(true));
    }

    @Test
    public void testWithWildcardAndFollowingPackagename() {
        PackageFilter pf = new PackageFilter("de.test.package.*.test");
        assertThat(pf.matches("de.test.package"), is(false));
        assertThat(pf.matches("de.test.package.p.test"), is(true));
        assertThat(pf.matches("de.test.package.packageOne.test"), is(true));
        assertThat(pf.matches("de.test.package.packageOne.test2"), is(false));
    }

    @Test
    public void testWithOnlyWildcard() {
        PackageFilter pf = new PackageFilter("*");
        assertThat(pf.matches("de.test.package"), is(true));
        assertThat(pf.matches("de.test.package.p.test"), is(true));
        assertThat(pf.matches("de.test.package.packageOne.test"), is(true));
        assertThat(pf.matches("de.test.package.packageOne.test2"), is(true));
    }
}
