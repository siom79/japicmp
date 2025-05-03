package japicmp;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.containsString;

class JApiCmpTest {
	private ByteArrayOutputStream out;
	private ByteArrayOutputStream err;

	@BeforeEach
	public void before() {
		out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
		err = new ByteArrayOutputStream();
		System.setErr(new PrintStream(err));
		JApiCmp.systemExit = status -> {};
	}

	@Test
	void testWithoutArguments() {
		JApiCmp.main(new String[]{});
		Assertions.assertTrue(err.toString().contains("E: Required option"));
	}

	private void assertThatUseHelpOptionIsPrinted() {
		MatcherAssert.assertThat(out.toString(), containsString(JApiCmp.USE_HELP_OR_H_FOR_MORE_INFORMATION));
	}

	private void assertThatHelpIsPrinted() {
		MatcherAssert.assertThat(out.toString(), containsString("SYNOPSIS"));
		MatcherAssert.assertThat(out.toString(), containsString("OPTIONS"));
	}

	@Test
	void testHelp() {
		JApiCmp.main(new String[]{"-h"});
		Assertions.assertFalse(err.toString().contains("E: "));
		assertThatHelpIsPrinted();
	}

	@Test
	void testHelpLongOption() {
		JApiCmp.main(new String[]{"--help"});
		Assertions.assertFalse(err.toString().contains("E: "));
		assertThatHelpIsPrinted();
	}

	@Test
	void testWithNewArchiveOptionButWithoutArgument() {
		JApiCmp.main(new String[]{"-n"});
		Assertions.assertTrue(err.toString().contains("E: Missing argument for option '-n, --new'.".trim()));
		assertThatUseHelpOptionIsPrinted();
	}

	@Test
	void testWithOldArchiveOptionButWithoutArgument() {
		JApiCmp.main(new String[]{"-o"});
		Assertions.assertTrue(err.toString().contains("E: Missing argument for option '-o, --old'.".trim()));
		assertThatUseHelpOptionIsPrinted();
	}


	@Test
	void testWithNewArchiveOptionButWithInvalidArgument() {
		JApiCmp.main(new String[]{"-n", "xyz.jar", "-o", "zyx.jar"});
		Assertions.assertTrue(err.toString().contains("E: File".trim()));
		Assertions.assertTrue(err.toString().contains("does not exist.".trim()));
		assertThatUseHelpOptionIsPrinted();
	}

	@Test
	void testWithOldArchiveOptionButWithInvalidArgument() {
		JApiCmp.main(new String[]{"-n", pathTo("new.jar"), "-o", "xyz.jar"});
		Assertions.assertTrue(err.toString().contains("E: File".trim()));
		Assertions.assertTrue(err.toString().contains("does not exist.".trim()));
		assertThatUseHelpOptionIsPrinted();
	}

	@Test
	void testWithOldArchiveOptionAndNewArchiveOption() {
		JApiCmp.main(new String[]{"-n", pathTo("new.jar"), "-o", pathTo("old.jar")});
		Assertions.assertFalse(err.toString().contains("E: ".trim()));
	}

	private String pathTo(String jarFileName) {
		return Paths.get(System.getProperty("user.dir"), "src", "test", "resources", jarFileName).toString();
	}
}
