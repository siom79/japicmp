package japicmp.maven;

import japicmp.versioning.SemanticVersion;
import org.apache.maven.artifact.versioning.ArtifactVersion;

public class SemanticArtifactVersion implements ArtifactVersion {
	private SemanticVersion comparable;
	
	public SemanticArtifactVersion(String version) {
		parseVersion(version);
	}
	
	public SemanticArtifactVersion(ArtifactVersion otherVersion) {
		this(otherVersion.toString());
	}
	
	public int compareTo(ArtifactVersion otherVersion) {
		if(otherVersion instanceof SemanticArtifactVersion) {
			return this.comparable.compareTo(((SemanticArtifactVersion) otherVersion).comparable);
		}
		else {
			try {
				// If comparing to a non-semantic version, try to transform it into semantic and compare.
				SemanticArtifactVersion otherSemVer = new SemanticArtifactVersion(otherVersion.toString());
				return this.comparable.compareTo(otherSemVer.comparable);
			}
			catch(Exception e) {
				// Else, other version not semantic. Consider it older by default.
				return 1;
			}
		}
	}
	
	public int getMajorVersion() {
		return comparable.getMajor();
	}

	public int getMinorVersion() {
		return comparable.getMinor();
	}

	public int getIncrementalVersion() {
		return comparable.getPatch();
	}

	public int getBuildNumber() {
		return 0;
	}

	public String getQualifier() {
		return comparable.getPreReleaseIdentifier();
	}

	public final void parseVersion(String version) {
		comparable = new SemanticVersion(version);
	}
	
	@Override
	public String toString() {
		return comparable.toString();
	}
}