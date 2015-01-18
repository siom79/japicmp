package japicmp;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.LogMode;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class JApiCmpTest {
	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	@Rule
	public final ErrLogRule errLog = new ErrLogRule(LogMode.LOG_ONLY);
	@Rule
	public final OutLogRule outLog = new OutLogRule(LogMode.LOG_ONLY);

	@Test
	public void testWithoutArguments() {
		exit.expectSystemExitWithStatus(-1);
		exit.checkAssertionAfterwards(new Assertion() {
			public void checkAssertion() {
				assertThat(errLog.getLog().trim(), containsString("E: Required option".trim()));
				assertThat(outLog.getLog(), containsString("NAME"));
				assertThat(outLog.getLog(), containsString("SYNOPSIS"));
				assertThat(outLog.getLog(), containsString("OPTIONS"));
			}
		});
		JApiCmp.main(new String[]{});
	}

	@Test
	public void testHelp() {
		exit.expectSystemExitWithStatus(-1);
		exit.checkAssertionAfterwards(new Assertion() {
			public void checkAssertion() {
				assertThat(errLog.getLog().trim(), containsString("E: Required option".trim()));
				assertThat(outLog.getLog(), containsString("NAME"));
				assertThat(outLog.getLog(), containsString("SYNOPSIS"));
				assertThat(outLog.getLog(), containsString("OPTIONS"));
			}
		});
		JApiCmp.main(new String[]{"-h"});
	}

	static void assertListsEquals(ImmutableList<String> expected, ImmutableList<String> actual) {
		Joiner nlJoiner = Joiner.on("\n");
		assertEquals(nlJoiner.join(expected), nlJoiner.join(actual));
	}
}