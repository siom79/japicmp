package japicmp.maven;

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
		VersionChange vc = new VersionChange(Collections.singletonList(new File("lib-1.2.3.jar")), Collections.singletonList(new File("lib-1.2.3.jar")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(VersionChange.ChangeType.UNCHANGED));
	}

	@Test
	public void testOneVersionPatchChange() throws MojoFailureException {
		VersionChange vc = new VersionChange(Collections.singletonList(new File("lib-1.2.3.jar")), Collections.singletonList(new File("lib-1.2.4.jar")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(VersionChange.ChangeType.PATCH));
	}

	@Test
	public void testOneVersionMinorChange() throws MojoFailureException {
		VersionChange vc = new VersionChange(Collections.singletonList(new File("lib-1.2.3.jar")), Collections.singletonList(new File("lib-1.3.0.jar")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(VersionChange.ChangeType.MINOR));
	}

	@Test
	public void testOneVersionMajorChange() throws MojoFailureException {
		VersionChange vc = new VersionChange(Collections.singletonList(new File("lib-1.2.3.jar")), Collections.singletonList(new File("lib-2.0.0.jar")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(VersionChange.ChangeType.MAJOR));
	}

	@Test
	public void testOneVersionMajorChangeWithSnapshot() throws MojoFailureException {
		VersionChange vc = new VersionChange(Collections.singletonList(new File("lib-1.2.3.jar")), Collections.singletonList(new File("lib-2.0.0-SNAPSHOT.jar")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(VersionChange.ChangeType.MAJOR));
	}

	@Test(expected = MojoFailureException.class)
	public void testOneVersionNoVersionInFileName() throws MojoFailureException {
		VersionChange vc = new VersionChange(Collections.singletonList(new File("lib-1.2.3.jar")), Collections.singletonList(new File("lib-2.0.jar")), new Parameter());
		vc.computeChangeType();
	}

	@Test
	public void testTwoVersionsNoChange() throws MojoFailureException {
		VersionChange vc = new VersionChange(Arrays.asList(new File("liba-1.2.3.jar"), new File("libb-1.2.3.jar")), Arrays.asList(new File("liba-1.2.3.jar"), new File("libb-1.2.3.jar")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(VersionChange.ChangeType.UNCHANGED));
	}

	@Test
	public void testTwoVersionsPatchChange() throws MojoFailureException {
		VersionChange vc = new VersionChange(Arrays.asList(new File("liba-1.2.3.jar"), new File("libb-1.2.3.jar")), Arrays.asList(new File("liba-1.2.4.jar"), new File("libb-1.2.4.jar")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(VersionChange.ChangeType.PATCH));
	}

	@Test
	public void testTwoVersionsMinorChange() throws MojoFailureException {
		VersionChange vc = new VersionChange(Arrays.asList(new File("liba-1.2.3.jar"), new File("libb-1.2.3.jar")), Arrays.asList(new File("liba-1.3.0.jar"), new File("libb-1.3.0.jar")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(VersionChange.ChangeType.MINOR));
	}

	@Test
	public void testTwoVersionsMajorChange() throws MojoFailureException {
		VersionChange vc = new VersionChange(Arrays.asList(new File("liba-1.2.3.jar"), new File("libb-1.2.3.jar")), Arrays.asList(new File("liba-2.0.0.jar"), new File("libb-2.0.0.jar")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(VersionChange.ChangeType.MAJOR));
	}

	@Test
	public void testTwoVersionsMajorChangeNotAllVersionsTheSame() throws MojoFailureException {
		VersionChange vc = new VersionChange(Arrays.asList(new File("liba-1.2.3.jar"), new File("libb-1.2.3.jar")), Arrays.asList(new File("liba-1.2.3.jar"), new File("libb-1.3.0.jar")), new Parameter());
		assertThat(vc.computeChangeType().get(), is(VersionChange.ChangeType.MINOR));
	}

	@Test(expected = MojoFailureException.class)
	public void testTwoVersionsMajorChangeNotAllVersionsTheSameAndDifferentNumberofArchives() throws MojoFailureException {
		VersionChange vc = new VersionChange(Arrays.asList(new File("liba-1.2.3.jar"), new File("libb-1.2.4.jar")), Arrays.asList(new File("liba-1.2.3.jar"), new File("libb-1.3.0.jar"), new File("libc-1.4.0.jar")), new Parameter());
		vc.computeChangeType();
		fail();
	}

	@Test
	public void testMissingOldVersion() throws MojoFailureException {
		Parameter parameter = new Parameter();
		parameter.setIgnoreMissingOldVersion("true");
		VersionChange vc = new VersionChange(Collections.singletonList(new File("lib-1.2.3.jar")), Collections.singletonList(new File("lib-1.2.3.jar")), parameter);
		assertThat(vc.computeChangeType().get(), is(VersionChange.ChangeType.UNCHANGED));
	}

	@Test
	public void testMissingNewVersion() throws MojoFailureException {
		Parameter parameter = new Parameter();
		parameter.setIgnoreMissingNewVersion("true");
		VersionChange vc = new VersionChange(Collections.singletonList(new File("lib-1.2.3.jar")), Collections.singletonList(new File("lib-1.2.3.jar")), parameter);
		assertThat(vc.computeChangeType().get(), is(VersionChange.ChangeType.UNCHANGED));
	}

	@Test(expected = MojoFailureException.class)
	public void testNoParameter() throws MojoFailureException {
		VersionChange vc = new VersionChange(new ArrayList<File>(), new ArrayList<File>(), null);
		vc.computeChangeType();
	}
}
