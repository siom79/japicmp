package japicmp.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class JApiCmpTaskTest {

	public BuildFileRule rule;

	@BeforeEach
	public void setUp() {
		rule =  new BuildFileRule();
		rule.configureProject(System.getProperty("user.dir") + "/src/test/resources/japicmp/japicmptask.xml");
	}

	@Test
	public void testCompare() {
		rule.executeTarget("compare");
		String logContents = rule.getLog();
		assertThat("Incorrect log message (expected modified class)", logContents,
			containsString("***! MODIFIED CLASS: PUBLIC japicmp.cmp.JarArchiveComparator  (not serializable)"));
		assertThat("Incorrect log message (expected removed method)", logContents,
			containsString("---! REMOVED METHOD: PUBLIC(-) java.util.List<japicmp.model.JApiClass> compare(java.io.File, java.io.File)"));
	}

	@Test
	public void testReportOnlySummary() {
		rule.executeTarget("summary");
		String logContents = rule.getLog();
		assertThat("Incorrect log message (expected modified class)", logContents,
			containsString("***! MODIFIED CLASS: PUBLIC japicmp.cmp.JarArchiveComparator  (not serializable)"));
		assertThat("Incorrect log message (expected removed method)", logContents,
			not(containsString("---! REMOVED METHOD: PUBLIC(-) java.util.List<japicmp.model.JApiClass> compare(java.io.File, java.io.File)")));
	}

	@Test
	public void testBreakOnBinaryIncompatibility() {
		Assertions.assertThrows(BuildException.class, () -> rule.executeTarget("binary"));
	}

	@Test
	public void testBreakOnSourceIncompatibility() {
		Assertions.assertThrows(BuildException.class, () -> rule.executeTarget("source"));
	}

	@Test
	public void testBreakOnExclusionIncompatibility() {
		rule.executeTarget("exclusion");
	}

	@Test
	public void testBreakOnSemanticIncompatibility() {
		rule.executeTarget("semantic");
	}

	@Test
	public void testBreakOnModifications() {
		Assertions.assertThrows(BuildException.class, () -> rule.executeTarget("modifications"));
	}
}
