package japicmp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class JApiCmpTest {
	private ByteArrayOutputStream out;
	private ByteArrayOutputStream err;

	@Before
	public void before() {
		out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
		err = new ByteArrayOutputStream();
		System.setErr(new PrintStream(err));
		JApiCmp.systemExit = status -> {};
	}

	@Test
	public void testWithoutArguments() {
		JApiCmp.main(new String[]{});
		Assert.assertTrue(err.toString().contains("E: Required option"));
	}

	private void assertThatUseHelpOptionIsPrinted() {
		assertThat(out.toString(), containsString(JApiCmp.USE_HELP_OR_H_FOR_MORE_INFORMATION));
	}

	private void assertThatHelpIsPrinted() {
		assertThat(out.toString(), containsString("SYNOPSIS"));
		assertThat(out.toString(), containsString("OPTIONS"));
	}

	@Test
	public void testHelp() {
		JApiCmp.main(new String[]{"-h"});
		Assert.assertFalse(err.toString().contains("E: "));
		assertThatHelpIsPrinted();
	}

	@Test
	public void testHelpLongOption() {
		JApiCmp.main(new String[]{"--help"});
		Assert.assertFalse(err.toString().contains("E: "));
		assertThatHelpIsPrinted();
	}

	@Test
	public void testWithNewArchiveOptionButWithoutArgument() {
		JApiCmp.main(new String[]{"-n"});
		Assert.assertTrue(err.toString().contains("E: Missing argument for option '-n, --new'.".trim()));
		assertThatUseHelpOptionIsPrinted();
	}

	@Test
	public void testWithOldArchiveOptionButWithoutArgument() {
		JApiCmp.main(new String[]{"-o"});
		Assert.assertTrue(err.toString().contains("E: Missing argument for option '-o, --old'.".trim()));
		assertThatUseHelpOptionIsPrinted();
	}


	@Test
	public void testWithNewArchiveOptionButWithInvalidArgument() {
		JApiCmp.main(new String[]{"-n", "xyz.jar", "-o", "zyx.jar"});
		Assert.assertTrue(err.toString().contains("E: File".trim()));
		Assert.assertTrue(err.toString().contains("does not exist.".trim()));
		assertThatUseHelpOptionIsPrinted();
	}

	@Test
	public void testWithOldArchiveOptionButWithInvalidArgument() {
		JApiCmp.main(new String[]{"-n", pathTo("new.jar"), "-o", "xyz.jar"});
		Assert.assertTrue(err.toString().contains("E: File".trim()));
		Assert.assertTrue(err.toString().contains("does not exist.".trim()));
		assertThatUseHelpOptionIsPrinted();
	}

	@Test
	public void testWithOldArchiveOptionAndNewArchiveOption() {
		JApiCmp.main(new String[]{"-n", pathTo("new.jar"), "-o", pathTo("old.jar")});
		Assert.assertFalse(err.toString().contains("E: ".trim()));
	}

	private String pathTo(String jarFileName) {
		return Paths.get(System.getProperty("user.dir"), "src", "test", "resources", jarFileName).toString();
	}
}
