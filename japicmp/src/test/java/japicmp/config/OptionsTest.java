package japicmp.config;

import com.google.common.base.Optional;
import japicmp.exception.JApiCmpException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OptionsTest {

	@Test
	public void testVerify() {
		// GIVEN
		Options options = Options.newDefault();
		// WHEN
		options.verify();
		// THEN
		// -- no Exception
	}

	@Test
	public void testVerifyNotExistingHtmlStylesheet() {
		// GIVEN
		Options options = Options.newDefault();
		options.setHtmlStylesheet(Optional.of("none.css"));
		options.setHtmlOutputFile(Optional.of("test.html"));
		try {
			// WHEN
			options.verify();
			fail();
		} catch (JApiCmpException e) {
			// THEN
			assertEquals("HTML stylesheet 'none.css' does not exist.", e.getMessage());
		}
	}

	@Test
	public void testVerifyCssFileWithoutHtmlOutput() {
		// GIVEN
		Options options = Options.newDefault();
		options.setHtmlStylesheet(Optional.of("none.css"));
		try {
			// WHEN
			options.verify();
			fail();
		} catch (JApiCmpException e) {
			// THEN
			assertEquals("Define a HTML output file, if you want to apply a stylesheet.", e.getMessage());
		}
	}
}
