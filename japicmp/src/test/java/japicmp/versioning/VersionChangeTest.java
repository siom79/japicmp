package japicmp.versioning;

import japicmp.exception.JApiCmpException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class VersionChangeTest {

	@Test
	public void testOneVersionNoChange() {
		VersionChange vc = new VersionChange(Collections.singletonList(new SemanticVersion(1, 2, 3)), Collections.singletonList(new SemanticVersion(1, 2, 3)), false, false);
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.UNCHANGED));
	}

	@Test
	public void testOneVersionPatchChange() {
		VersionChange vc = new VersionChange(Collections.singletonList(new SemanticVersion(1, 2, 3)), Collections.singletonList(new SemanticVersion(1, 2, 4)), false, false);
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.PATCH));
	}

	@Test
	public void testOneVersionMinorChange() {
		VersionChange vc = new VersionChange(Collections.singletonList(new SemanticVersion(1, 2, 3)), Collections.singletonList(new SemanticVersion(1, 3, 0)), false, false);
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.MINOR));
	}

	@Test
	public void testOneVersionMajorChange() {
		VersionChange vc = new VersionChange(Collections.singletonList(new SemanticVersion(1, 2, 3)), Collections.singletonList(new SemanticVersion(2, 0, 0)), false, false);
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.MAJOR));
	}

	@Test
	public void testTwoVersionsNoChange() {
		VersionChange vc = new VersionChange(Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 2, 3)), Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 2, 3)), false, false);
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.UNCHANGED));
	}

	@Test
	public void testTwoVersionsPatchChange() {
		VersionChange vc = new VersionChange(Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 2, 3)), Arrays.asList(new SemanticVersion(1, 2, 4), new SemanticVersion(1, 2, 4)), false, false);
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.PATCH));
	}

	@Test
	public void testTwoVersionsMinorChange() {
		VersionChange vc = new VersionChange(Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 2, 3)), Arrays.asList(new SemanticVersion(1, 3, 0), new SemanticVersion(1, 3, 0)), false, false);
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.MINOR));
	}

	@Test
	public void testTwoVersionsMajorChange() {
		VersionChange vc = new VersionChange(Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 2, 3)), Arrays.asList(new SemanticVersion(2, 0, 0), new SemanticVersion(2, 0, 0)), false, false);
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.MAJOR));
	}

	@Test
	public void testTwoVersionsMajorChangeNotAllVersionsTheSame() {
		VersionChange vc = new VersionChange(Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 2, 3)), Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 3, 0)), false, false);
		assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.MINOR));
	}

	@Test(expected = JApiCmpException.class)
	public void testTwoVersionsMajorChangeNotAllVersionsTheSameAndDifferentNumberofArchives() {
		VersionChange vc = new VersionChange(Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 2, 4)), Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 3, 0), new SemanticVersion(1, 4, 0)), false, false);
		vc.computeChangeType();
		fail();
	}

	@Test(expected = JApiCmpException.class)
	public void testMissingOldVersion() {
		VersionChange vc = new VersionChange(Collections.<SemanticVersion>emptyList(), Collections.singletonList(new SemanticVersion(1, 2, 3)), false, false);
		vc.computeChangeType();
		fail();
	}

	@Test
	public void testIgnoreMissingOldVersion() {
		VersionChange vc = new VersionChange(Collections.<SemanticVersion>emptyList(), Collections.singletonList(new SemanticVersion(1, 2, 3)), true, false);
		assertThat(vc.computeChangeType().isPresent(), is(false));
	}

	@Test(expected = JApiCmpException.class)
	public void testMissingNewVersion() {
		VersionChange vc = new VersionChange(Collections.singletonList(new SemanticVersion(1, 2, 3)), Collections.<SemanticVersion>emptyList(), false, false);
		vc.computeChangeType();
		fail();
	}

	@Test
	public void testIgnoreMissingNewVersion() {
		VersionChange vc = new VersionChange(Collections.singletonList(new SemanticVersion(1, 2, 3)), Collections.<SemanticVersion>emptyList(), false, true);
		assertThat(vc.computeChangeType().isPresent(), is(false));
	}

	@Test(expected = JApiCmpException.class)
	public void testNoParameter() {
		VersionChange vc = new VersionChange(Collections.<SemanticVersion>emptyList(), Collections.<SemanticVersion>emptyList(), false, false);
		vc.computeChangeType();
		fail();
	}
}
