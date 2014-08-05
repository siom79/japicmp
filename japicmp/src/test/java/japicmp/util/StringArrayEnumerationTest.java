package japicmp.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.NoSuchElementException;

import org.junit.Test;

public class StringArrayEnumerationTest {

    @Test(expected = NoSuchElementException.class)
    public void testEmptyArray() {
        StringArrayEnumeration sae = new StringArrayEnumeration(new String[]{});
        assertThat(sae.hasMoreElements(), is(false));
        sae.nextElement();
    }

    @Test
    public void testOneElementArray() {
        StringArrayEnumeration sae = new StringArrayEnumeration(new String[]{"1"});
        assertThat(sae.hasMoreElements(), is(true));
        assertThat(sae.nextElement(), is("1"));
        assertThat(sae.hasMoreElements(), is(false));
    }

    @Test
    public void testTwoElementsArray() {
        StringArrayEnumeration sae = new StringArrayEnumeration(new String[]{"1","2"});
        assertThat(sae.hasMoreElements(), is(true));
        assertThat(sae.nextElement(), is("1"));
        assertThat(sae.hasMoreElements(), is(true));
        assertThat(sae.nextElement(), is("2"));
        assertThat(sae.hasMoreElements(), is(false));
    }
}
