package japicmp.util;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.is;

class StringArrayEnumerationTest {

	@Test
	void testEmptyArray() {
		StringArrayEnumeration sae = new StringArrayEnumeration(new String[]{});
		MatcherAssert.assertThat(sae.hasMoreElements(), is(false));
		Assertions.assertThrows(NoSuchElementException.class, sae::nextElement);
	}

	@Test
	void testOneElementArray() {
		StringArrayEnumeration sae = new StringArrayEnumeration(new String[]{"1"});
		MatcherAssert.assertThat(sae.hasMoreElements(), is(true));
		MatcherAssert.assertThat(sae.nextElement(), is("1"));
		MatcherAssert.assertThat(sae.hasMoreElements(), is(false));
	}

	@Test
	void testTwoElementsArray() {
		StringArrayEnumeration sae = new StringArrayEnumeration(new String[]{"1", "2"});
		MatcherAssert.assertThat(sae.hasMoreElements(), is(true));
		MatcherAssert.assertThat(sae.nextElement(), is("1"));
		MatcherAssert.assertThat(sae.hasMoreElements(), is(true));
		MatcherAssert.assertThat(sae.nextElement(), is("2"));
		MatcherAssert.assertThat(sae.hasMoreElements(), is(false));
	}
}
