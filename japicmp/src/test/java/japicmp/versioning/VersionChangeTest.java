package japicmp.versioning;

import japicmp.exception.JApiCmpException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.core.Is.is;

class VersionChangeTest {

	@Test
	void testOneVersionNoChange() {
		VersionChange vc = new VersionChange(Collections.singletonList(new SemanticVersion(1, 2, 3)), Collections.singletonList(new SemanticVersion(1, 2, 3)), false, false);
		MatcherAssert.assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.UNCHANGED));
	}

	@Test
	void testOneVersionPatchChange() {
		VersionChange vc = new VersionChange(Collections.singletonList(new SemanticVersion(1, 2, 3)), Collections.singletonList(new SemanticVersion(1, 2, 4)), false, false);
		MatcherAssert.assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.PATCH));
	}

	@Test
	void testOneVersionMinorChange() {
		VersionChange vc = new VersionChange(Collections.singletonList(new SemanticVersion(1, 2, 3)), Collections.singletonList(new SemanticVersion(1, 3, 0)), false, false);
		MatcherAssert.assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.MINOR));
	}

	@Test
	void testOneVersionMajorChange() {
		VersionChange vc = new VersionChange(Collections.singletonList(new SemanticVersion(1, 2, 3)), Collections.singletonList(new SemanticVersion(2, 0, 0)), false, false);
		MatcherAssert.assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.MAJOR));
	}

	@Test
	void testTwoVersionsNoChange() {
		VersionChange vc = new VersionChange(Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 2, 3)), Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 2, 3)), false, false);
		MatcherAssert.assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.UNCHANGED));
	}

	@Test
	void testTwoVersionsPatchChange() {
		VersionChange vc = new VersionChange(Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 2, 3)), Arrays.asList(new SemanticVersion(1, 2, 4), new SemanticVersion(1, 2, 4)), false, false);
		MatcherAssert.assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.PATCH));
	}

	@Test
	void testTwoVersionsMinorChange() {
		VersionChange vc = new VersionChange(Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 2, 3)), Arrays.asList(new SemanticVersion(1, 3, 0), new SemanticVersion(1, 3, 0)), false, false);
		MatcherAssert.assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.MINOR));
	}

	@Test
	void testTwoVersionsMajorChange() {
		VersionChange vc = new VersionChange(Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 2, 3)), Arrays.asList(new SemanticVersion(2, 0, 0), new SemanticVersion(2, 0, 0)), false, false);
		MatcherAssert.assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.MAJOR));
	}

	@Test
	void testTwoVersionsMajorChangeNotAllVersionsTheSame() {
		VersionChange vc = new VersionChange(Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 2, 3)), Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 3, 0)), false, false);
		MatcherAssert.assertThat(vc.computeChangeType().get(), is(SemanticVersion.ChangeType.MINOR));
	}

	@Test
	void testTwoVersionsMajorChangeNotAllVersionsTheSameAndDifferentNumberofArchives() {
		VersionChange vc = new VersionChange(Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 2, 4)), Arrays.asList(new SemanticVersion(1, 2, 3), new SemanticVersion(1, 3, 0), new SemanticVersion(1, 4, 0)), false, false);
		Assertions.assertThrows(JApiCmpException.class, vc::computeChangeType);
	}

	@Test
	void testMissingOldVersion() {
		VersionChange vc = new VersionChange(Collections.<SemanticVersion>emptyList(), Collections.singletonList(new SemanticVersion(1, 2, 3)), false, false);
		Assertions.assertThrows(JApiCmpException.class, vc::computeChangeType);
	}

	@Test
	void testIgnoreMissingOldVersion() {
		VersionChange vc = new VersionChange(Collections.<SemanticVersion>emptyList(), Collections.singletonList(new SemanticVersion(1, 2, 3)), true, false);
		MatcherAssert.assertThat(vc.computeChangeType().isPresent(), is(false));
	}

	@Test
	void testMissingNewVersion() {
		VersionChange vc = new VersionChange(Collections.singletonList(new SemanticVersion(1, 2, 3)), Collections.<SemanticVersion>emptyList(), false, false);
		Assertions.assertThrows(JApiCmpException.class, vc::computeChangeType);
	}

	@Test
	void testIgnoreMissingNewVersion() {
		VersionChange vc = new VersionChange(Collections.singletonList(new SemanticVersion(1, 2, 3)), Collections.<SemanticVersion>emptyList(), false, true);
		MatcherAssert.assertThat(vc.computeChangeType().isPresent(), is(false));
	}

	@Test
	void testNoParameter() {
		VersionChange vc = new VersionChange(Collections.<SemanticVersion>emptyList(), Collections.<SemanticVersion>emptyList(), false, false);
		Assertions.assertThrows(JApiCmpException.class, vc::computeChangeType);
	}
}
