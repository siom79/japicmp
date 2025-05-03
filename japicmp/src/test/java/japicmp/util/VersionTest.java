package japicmp.util;

import japicmp.versioning.SemanticVersion;
import japicmp.versioning.Version;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;

class VersionTest {

	@Test
	void testSingleDigitSemanticVersionFromString() {
		Optional<SemanticVersion> semver = Version.getSemanticVersion("1.2.3");
		MatcherAssert.assertThat(semver.get().getMajor(), is(1));
		MatcherAssert.assertThat(semver.get().getMinor(), is(2));
		MatcherAssert.assertThat(semver.get().getPatch(), is(3));
	}

	@Test
	void testMultidigitSemanticVersionFromString() {
		Optional<SemanticVersion> semver = Version.getSemanticVersion("11.22.33");
		MatcherAssert.assertThat(semver.get().getMajor(), is(11));
		MatcherAssert.assertThat(semver.get().getMinor(), is(22));
		MatcherAssert.assertThat(semver.get().getPatch(), is(33));
	}

	@Test
	void testEmbeddedSemanticVersionFromString() {
		Optional<SemanticVersion> semver = Version.getSemanticVersion("filename11.22.33filename");
		MatcherAssert.assertThat(semver.get().getMajor(), is(11));
		MatcherAssert.assertThat(semver.get().getMinor(), is(22));
		MatcherAssert.assertThat(semver.get().getPatch(), is(33));
	}

}
