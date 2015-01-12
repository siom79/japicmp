package japicmp;

import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
				assertEquals("E: no valid new archive found", errLog.getLog().trim());
				assertShortHelp(outLog.getLog());
			}
		});
		JApiCmp.main(new String[] { });
	}

	@Test
	public void testMain_with_unknown_arguments() {
		exit.expectSystemExitWithStatus(2);
		exit.checkAssertionAfterwards(new Assertion() {
			public void checkAssertion() {
				assertEquals("E: Found unexpected parameters: [--no]", errLog.getLog().trim());
				assertShortHelp(outLog.getLog());
			}
		});
		JApiCmp.main(new String[] { "--no" });
	}

	@Test
	public void testMain_with_help_arguments() {
		exit.expectSystemExitWithStatus(0);
		exit.checkAssertionAfterwards(new Assertion() {
			public void checkAssertion() {
				assertFullHelp(outLog.getLog());
			}
		});
		JApiCmp.main(new String[] { "--help" });
	}

	private static void assertShortHelp(String msg) {
		Matcher matcher = Pattern.compile("[-]+[\\w-]+").matcher(msg);
		ImmutableList.Builder<String> actualBuilder = ImmutableList.builder();
		while (matcher.find()) {
			actualBuilder.add(matcher.group());
		}
		ImmutableList.Builder<String> expectedBuilder = ImmutableList.<String>builder() //
				.add("-jar") //
				.add("-a") //
				.add("-b") //
				.add("--only-incompatible") //
				.add("-e") //
				.add("--exclude") //
				.add("-h") //
				.add("--help") //
				.add("--html-file") //
				.add("-i") //
				.add("--include") //
				.add("-m") //
				.add("--only-modified") //
				.add("-n") //
				.add("--new") //
				.add("-o") //
				.add("--old") //
				.add("-x") //
				.add("--xml-file") //
				;

		assertListsEquals(expectedBuilder.build(), actualBuilder.build());
	}

	private static void assertFullHelp(String msg) {
		FluentIterable<String> lines = FluentIterable.from(Splitter.on("\n").split(msg));

		ImmutableList<String> optionLines = lines.filter(new Predicate<String>() {
			@Override
			public boolean apply(String s) {
				return s.matches("^\\s+\\-.*");
			}
		}).transform(new TrimFunction()).toList();

		ImmutableList<String> topicLines = lines.filter(new Predicate<String>() {
			@Override
			public boolean apply(String s) {
				return s.matches("^\\w.*");
			}
		}).transform(new TrimFunction()).toList();

		ImmutableList<String> expected = ImmutableList.<String>builder() //
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
		assertListsEquals(expected, optionLines);
		assertListsEquals(ImmutableList.of("NAME", "SYNOPSIS", "OPTIONS"), topicLines);
	}

	private static void assertListsEquals(ImmutableList<String> expected,
			ImmutableList<String> actual) {
		Joiner nlJoiner = Joiner.on("\n");
		assertEquals(nlJoiner.join(expected), nlJoiner.join(actual));
	}

	private static class TrimFunction implements Function<String, String> {
		@Override
		public String apply(String s) {
			return s.trim();
		}
	}
}
