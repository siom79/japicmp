package japicmp.versioning;

import japicmp.exception.JApiCmpException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class SemanticVersionTest {
	
	@Test
	public void testToStringReturnsSourceDescriptionStringIfCorrectlyFormatted() {
		assertThat(new SemanticVersion("1.0.0-alpha+test").toString(), is("1.0.0-alpha+test"));
		assertThat(new SemanticVersion("1.0.0").toString(), is("1.0.0"));
	}
	
	/**
		Validates that the "compareTo" implementation matches the specification for main version numbers.
	*/
	@Test
	public void testVersionOrderMatches() {
		String[] orderedExampleVersions = new String[] {
			"1.0.0", "2.0.0", "2.1.0", "2.1.1"
		};
		
		SemanticVersion leftVersion = new SemanticVersion(orderedExampleVersions[0]);
		
		for(int i = 1; i < orderedExampleVersions.length; i++) {
			SemanticVersion rightVersion = new SemanticVersion(orderedExampleVersions[i]);
			
			assertThat(leftVersion, lessThan(rightVersion));
			
			leftVersion = rightVersion;
		}
	}

	/**
		Validates that the "compareTo" implementation matches the order of the example provided in the SemVer specification for build identifiers.
		cf. http://semver.org/spec/v2.0.0.html#spec-item-11
	*/
	@Test
	public void testBuildIdentifierOrderMatchesSpecExample() {
		String[] orderedExampleVersions = new String[] {
			"1.0.0-alpha",
			"1.0.0-alpha.1",
			"1.0.0-alpha.beta",
			"1.0.0-beta",
			"1.0.0-beta.2",
			"1.0.0-beta.11",
			"1.0.0-rc.1",
			"1.0.0"
		};
		
		SemanticVersion leftVersion = new SemanticVersion(orderedExampleVersions[0]);
		
		for(int i = 1; i < orderedExampleVersions.length; i++) {
			SemanticVersion rightVersion = new SemanticVersion(orderedExampleVersions[i]);
			
			assertThat(leftVersion, lessThan(rightVersion));
			
			leftVersion = rightVersion;
		}
	}
	
	@Test
	public void testBuildMetadataDoesNotAffectOrdering() {
		assertThat(new SemanticVersion("1.0.0-alpha+test"), comparesEqualTo(new SemanticVersion("1.0.0-alpha")));
		assertThat(new SemanticVersion("1.0.0-alpha+test1"), comparesEqualTo(new SemanticVersion("1.0.0-alpha+test2")));
	}
}
