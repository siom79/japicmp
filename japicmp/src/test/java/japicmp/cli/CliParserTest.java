package japicmp.cli;

import japicmp.config.Options;
import japicmp.exception.JApiCmpException;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CliParserTest {
    private CliParser subject;

    @Before
    public void before() {
        subject = new CliParser();
    }

    @Test
    public void testAllOptions() {
        Options options = subject.parse(new String[]{"-n", "npath", "-o", "opath", "-m", "-x", "xpath"});
        assertThat(options.getXmlOutputFile().get(), is("xpath"));
        assertThat(options.getNewArchive(), is("npath"));
        assertThat(options.getOldArchive(), is("opath"));
        assertThat(options.isOutputOnlyModifications(), is(true));
    }

    @Test
    public void testMissingArgumentForN() {
    	boolean exceptionCaught = false;
        try {
			subject.parse(new String[]{"-n", "-o", "opath"});
		} catch (JApiCmpException e) {
			exceptionCaught = true;
			assertThat(e.getReason(), is(JApiCmpException.Reason.CliMissingArgumentForOption));
		}
        assertThat(exceptionCaught, is(true));
    }

    @Test
    public void testOnlyNAndO() {
        Options options = subject.parse(new String[]{"-n", "npath", "-o", "opath",});
        assertThat(options.getXmlOutputFile().isPresent(), is(false));
        assertThat(options.getNewArchive(), is("npath"));
        assertThat(options.getOldArchive(), is("opath"));
        assertThat(options.isOutputOnlyModifications(), is(false));
    }
}
