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
		try {
			// WHEN
			options.verify();
			fail();
		} catch (JApiCmpException e) {
			// THEN
			assertEquals("HTML stylesheet 'none.css' does not exist.", e.getMessage());
		}
	}
}
