package japicmp.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class JApiCmpTaskTest {
	@Rule
	public final BuildFileRule rule = new BuildFileRule();

	@Before
	public void setUp() {
		rule.configureProject(System.getProperty("user.dir") + "/src/test/resources/japicmp/japicmptask.xml");
	}

	@Test
	public void testCompare() {
		rule.executeTarget("compare");
		String logContents = rule.getLog();
		assertThat("Incorrect log message (expected modified class)", logContents,
			containsString("***! MODIFIED CLASS: PUBLIC japicmp.cmp.JarArchiveComparator  (not serializable)"));
		assertThat("Incorrect log message (expected removed method)", logContents,
			containsString("---! REMOVED METHOD: PUBLIC(-) java.util.List compare(java.io.File, java.io.File)"));
	}

	@Test(expected = BuildException.class)
	public void testBreakOnBinaryIncompatibility() {
		rule.executeTarget("binary");
	}

	@Test(expected = BuildException.class)
	public void testBreakOnSourceIncompatibility() {
		rule.executeTarget("source");
	}

	@Test
	public void testBreakOnExclusionIncompatibility() {
		rule.executeTarget("exclusion");
	}

	@Test
	public void testBreakOnSemanticIncompatibility() {
		rule.executeTarget("semantic");
	}

	@Test(expected = BuildException.class)
	public void testBreakOnModifications() {
		rule.executeTarget("modifications");
	}
}
