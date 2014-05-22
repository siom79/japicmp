package japicmp.cmp;

import japicmp.config.PackageFilter;
import org.junit.Test;

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
}
