package japicmp.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import japicmp.versioning.SemanticVersion;
import japicmp.versioning.Version;

public class VersionTest {

	@Test
	public void testSingleDigitSemanticVersionFromString() {
		Optional<SemanticVersion> semver = Version.getSemanticVersion("1.2.3");
		assertThat(semver.get().getMajor(), is(1));
		assertThat(semver.get().getMinor(), is(2));
		assertThat(semver.get().getPatch(), is(3));
	}
	
	@Test
	public void testMultidigitSemanticVersionFromString() {
		Optional<SemanticVersion> semver = Version.getSemanticVersion("11.22.33");
		assertThat(semver.get().getMajor(), is(11));
		assertThat(semver.get().getMinor(), is(22));
		assertThat(semver.get().getPatch(), is(33));
	}
	
	@Test
	public void testEmbeddedSemanticVersionFromString() {
		Optional<SemanticVersion> semver = Version.getSemanticVersion("filename11.22.33filename");
		assertThat(semver.get().getMajor(), is(11));
		assertThat(semver.get().getMinor(), is(22));
		assertThat(semver.get().getPatch(), is(33));
	}

}
