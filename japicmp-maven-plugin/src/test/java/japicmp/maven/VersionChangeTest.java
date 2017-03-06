package japicmp.maven;

import japicmp.cmp.JApiCmpArchive;
import japicmp.versioning.SemanticVersion;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class VersionChangeTest {

	@Test
	public void testOneVersionNoChange() throws MojoFailureException {
		VersionChange vc = new VersionChange(Collections.singletonList(new JApiCmpArchive(new File("lib-1.2.3.jar"), "1.2.3")), Collections.singletonList(new JApiCmpArchive(new File("lib-1.2.3.jar"), "1.2.3")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.UNCHANGED));
	}

	@Test
	public void testOneVersionPatchChange() throws MojoFailureException {
		VersionChange vc = new VersionChange(Collections.singletonList(new JApiCmpArchive(new File("lib-1.2.3.jar"), "1.2.3")), Collections.singletonList(new JApiCmpArchive(new File("lib-1.2.4.jar"), "1.2.4")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.PATCH));
	}

	@Test
	public void testOneVersionMinorChange() throws MojoFailureException {
		VersionChange vc = new VersionChange(Collections.singletonList(new JApiCmpArchive(new File("lib-1.2.3.jar"), "1.2.3")), Collections.singletonList(new JApiCmpArchive(new File("lib-1.3.0.jar"), "1.3.0")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.MINOR));
	}

	@Test
	public void testOneVersionMajorChange() throws MojoFailureException {
		VersionChange vc = new VersionChange(Collections.singletonList(new JApiCmpArchive(new File("lib-1.2.3.jar"), "1.2.3")), Collections.singletonList(new JApiCmpArchive(new File("lib-2.0.0.jar"), "2.0.0")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.MAJOR));
	}

	@Test
	public void testOneVersionMajorChangeWithSnapshot() throws MojoFailureException {
		VersionChange vc = new VersionChange(Collections.singletonList(new JApiCmpArchive(new File("lib-1.2.3.jar"), "1.2.3")), Collections.singletonList(new JApiCmpArchive(new File("lib-2.0.0-SNAPSHOT.jar"), "2.0.0-SNAPSHOT")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.MAJOR));
	}

	@Test(expected = MojoFailureException.class)
	public void testOneVersionNoVersionInFileName() throws MojoFailureException {
		VersionChange vc = new VersionChange(Collections.singletonList(new JApiCmpArchive(new File("lib-1.2.3.jar"), "1.2.3")), Collections.singletonList(new JApiCmpArchive(new File("lib-2.0.jar"), "2.0")), new Parameter());
		vc.computeChangeType();
	}

	@Test
	public void testTwoVersionsNoChange() throws MojoFailureException {
		VersionChange vc = new VersionChange(Arrays.asList(new JApiCmpArchive(new File("liba-1.2.3.jar"), "1.2.3"), new JApiCmpArchive(new File("libb-1.2.3.jar"), "1.2.3")), Arrays.asList(new JApiCmpArchive(new File("liba-1.2.3.jar"), "1.2.3"), new JApiCmpArchive(new File("libb-1.2.3.jar"), "1.2.3")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.UNCHANGED));
	}

	@Test
	public void testTwoVersionsPatchChange() throws MojoFailureException {
		VersionChange vc = new VersionChange(Arrays.asList(new JApiCmpArchive(new File("liba-1.2.3.jar"), "1.2.3"), new JApiCmpArchive(new File("libb-1.2.3.jar"), "1.2.3")), Arrays.asList(new JApiCmpArchive(new File("liba-1.2.4.jar"), "1.2.4"), new JApiCmpArchive(new File("libb-1.2.4.jar"), "1.2.4")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.PATCH));
	}

	@Test
	public void testTwoVersionsMinorChange() throws MojoFailureException {
		VersionChange vc = new VersionChange(Arrays.asList(new JApiCmpArchive(new File("liba-1.2.3.jar"), "1.2.3"), new JApiCmpArchive(new File("libb-1.2.3.jar"), "1.2.3")), Arrays.asList(new JApiCmpArchive(new File("liba-1.3.0.jar"), "1.3.0"), new JApiCmpArchive(new File("libb-1.3.0.jar"), "1.3.0")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.MINOR));
	}

	@Test
	public void testTwoVersionsMajorChange() throws MojoFailureException {
		VersionChange vc = new VersionChange(Arrays.asList(new JApiCmpArchive(new File("liba-1.2.3.jar"), "1.2.3"), new JApiCmpArchive(new File("libb-1.2.3.jar"), "1.2.3")), Arrays.asList(new JApiCmpArchive(new File("liba-2.0.0.jar"), "2.0.0"), new JApiCmpArchive(new File("libb-2.0.0.jar"), "2.0.0")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.MAJOR));
	}

	@Test
	public void testTwoVersionsMajorChangeNotAllVersionsTheSame() throws MojoFailureException {
		VersionChange vc = new VersionChange(Arrays.asList(new JApiCmpArchive(new File("liba-1.2.3.jar"), "1.2.3"), new JApiCmpArchive(new File("libb-1.2.3.jar"), "1.2.3")), Arrays.asList(new JApiCmpArchive(new File("liba-1.2.3.jar"), "1.2.3"), new JApiCmpArchive(new File("libb-1.3.0.jar"), "1.3.0")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.MINOR));
	}

	@Test(expected = MojoFailureException.class)
	public void testTwoVersionsMajorChangeNotAllVersionsTheSameAndDifferentNumberofArchives() throws MojoFailureException {
		VersionChange vc = new VersionChange(Arrays.asList(new JApiCmpArchive(new File("liba-1.2.3.jar"), "1.2.3"), new JApiCmpArchive(new File("libb-1.2.4.jar"), "1.2.4")), Arrays.asList(new JApiCmpArchive(new File("liba-1.2.3.jar"), "1.2.3"), new JApiCmpArchive(new File("libb-1.3.0.jar"), "1.3.0"), new JApiCmpArchive(new File("libc-1.4.0.jar"), "1.4.0")), new Parameter());
		vc.computeChangeType();
		fail();
	}

	@Test
	public void testMissingOldVersion() throws MojoFailureException {
		Parameter parameter = new Parameter();
		parameter.setIgnoreMissingOldVersion("true");
		VersionChange vc = new VersionChange(Collections.singletonList(new JApiCmpArchive(new File("lib-1.2.3.jar"), "1.2.3")), Collections.singletonList(new JApiCmpArchive(new File("lib-1.2.3.jar"), "1.2.3")), parameter);
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.UNCHANGED));
	}

	@Test
	public void testMissingNewVersion() throws MojoFailureException {
		Parameter parameter = new Parameter();
		parameter.setIgnoreMissingNewVersion("true");
		VersionChange vc = new VersionChange(Collections.singletonList(new JApiCmpArchive(new File("lib-1.2.3.jar"), "1.2.3")), Collections.singletonList(new JApiCmpArchive(new File("lib-1.2.3.jar"), "1.2.3")), parameter);
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.UNCHANGED));
	}

	@Test(expected = MojoFailureException.class)
	public void testNoParameter() throws MojoFailureException {
		VersionChange vc = new VersionChange(new ArrayList<JApiCmpArchive>(), new ArrayList<JApiCmpArchive>(), null);
		vc.computeChangeType();
	}
}
