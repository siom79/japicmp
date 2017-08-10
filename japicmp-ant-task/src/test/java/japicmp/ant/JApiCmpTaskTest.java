package japicmp.ant;

import org.apache.tools.ant.BuildFileRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class JApiCmpTaskTest {
	@Rule
	public BuildFileRule rule = new BuildFileRule();

	@Before
	public void setUp() {
		rule.configureProject(System.getProperty("user.dir") + "/src/test/resources/japicmp/japicmptask.xml");
	}

	//@Test
	public void testTask() {
		rule.executeTarget("compare");
		String logContents = rule.getLog();
		assertTrue("Incorrect log message (expected modified class)",
			logContents.contains("***! MODIFIED CLASS: PUBLIC japicmp.cmp.JarArchiveComparator  (not serializable)"));
		assertTrue("Incorrect log message (expected removed method)",
			logContents.contains("---! REMOVED METHOD: PUBLIC(-) java.util.List compare(java.io.File, java.io.File)"));
	}
}
