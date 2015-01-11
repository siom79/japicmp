package japicmp;

import static org.junit.Assert.assertEquals;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.StandardErrorStreamLog;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;

public class JApiCmpTest {
	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	@Rule
	public final StandardErrorStreamLog errLog = new StandardErrorStreamLog();
	@Rule
	public final StandardOutputStreamLog outLog = new StandardOutputStreamLog();

	@Test
	public void testMain_without_arguments() {
		exit.expectSystemExitWithStatus(128);
		exit.checkAssertionAfterwards(new Assertion() {
			public void checkAssertion() {
				assertEquals("E: no valid new archive found".trim(), errLog.getLog().trim());
				assertFullHelp(outLog.getLog());
			}
		});
		JApiCmp.main(new String[]{});
	}

	@Test
	public void testMain_with_unknown_arguments() {
		exit.expectSystemExitWithStatus(2);
		exit.checkAssertionAfterwards(new Assertion() {
			public void checkAssertion() {
				assertEquals("E: Found unexpected parameters: [--no]".trim(), errLog.getLog().trim());
				assertFullHelp(outLog.getLog());
			}
		});
		JApiCmp.main(new String[]{"--no"});
	}

	@Test
	public void testMain_with_help_arguments() {
		exit.expectSystemExitWithStatus(0);
		exit.checkAssertionAfterwards(new Assertion() {
			public void checkAssertion() {
				assertFullHelp(outLog.getLog());
			}
		});
		JApiCmp.main(new String[]{"--help"});
	}

	private static void assertFullHelp(String msg) {
		Iterable<String> lines = Splitter.on("\n").split(msg);
		ImmutableList<String> optionLines = FluentIterable.from(lines).filter(new Predicate<String>() {
			@Override
			public boolean apply(String s) {
				return s.matches("^\\s+\\-.*");
			}
		}).transform(new Function<String, String>() {
			@Override
			public String apply(String s) {
				return s.trim();
			}
		}).toList();
		ImmutableList<Object> expected = ImmutableList.builder() //
				.add("-a <accessModifier>") //
				.add("-b, --only-incompatible") //
				.add("-e <packagesToExclude>, --exclude <packagesToExclude>") //
				.add("-h, --help") //
				.add("--html-file <pathToHtmlOutputFile>") //
				.add("-i <packagesToInclude>, --include <packagesToInclude>") //
				.add("-m, --only-modified") //
				.add("-n <pathToNewVersionJar>, --new <pathToNewVersionJar>") //
				.add("-o <pathToOldVersionJar>, --old <pathToOldVersionJar>") //
				.add("-x <pathToXmlOutputFile>, --xml-file <pathToXmlOutputFile>") //
				.build();
		Joiner nlJoiner = Joiner.on("\n");
		assertEquals(nlJoiner.join(expected), nlJoiner.join(optionLines));
	}
}
