package japicmp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

public class ErrLogRuleTest {

	@Test
	public void testLinesOf() {
		// given
		String in = "A\r\nB";

		// when
		ImmutableList<String> lines = ErrLogRule.linesOf(in);

		// then
		JApiCmpTest.assertListsEquals(ImmutableList.of("A", "B"), lines);
	}

	@Test
	public void testOnlyLineOf_fail() {
		// given
		String in = "A\r\nB";
		try {
			// when
			ErrLogRule.onlyLineOf(in);
			fail();
		} catch (IllegalStateException e) {
			// then
			assertEquals("more then one line was found, but was \"A\nB\"", e.getMessage());
		}
	}

	@Test
	public void testOnlyLineOf_nl() {
		// given
		String in = "A\n";

		// when
		String result = ErrLogRule.onlyLineOf(in);

		// then
		assertEquals("A\n", result);
	}

	@Test
	public void testOnlyLineOf_crnl() {
		// given
		String in = "b\r\n";

		// when
		String result = ErrLogRule.onlyLineOf(in);

		// then
		assertEquals("b\n", result);
	}

}
