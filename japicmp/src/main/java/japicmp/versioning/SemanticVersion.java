package japicmp.versioning;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.ArrayList;

public class SemanticVersion implements Comparable<SemanticVersion> {
	private final int major;
	private final int minor;
	private final int patch;
	private final List<String> preReleaseIdentifiers;
	private final String buildMetadata;

	public enum ChangeType {
		MAJOR(3),
		MINOR(2),
		PATCH(1),
		UNCHANGED(0);

		private final int rank;

		ChangeType(int rank) {
			this.rank = rank;
		}

		public int getRank() {
			return rank;
		}
	}

	public SemanticVersion(int major, int minor, int patch) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.preReleaseIdentifiers = new ArrayList<String>();
		this.buildMetadata = null;
	}
	
	/**
		Parses a semantic version string formatted as per http://semver.org/spec/v2.0.0.html
	*/
	public SemanticVersion(String versionDescription) {
		
		int firstDotPos = versionDescription.indexOf('.');
		int secondDotPos = versionDescription.indexOf('.', firstDotPos + 1);
		int hyphenPos = versionDescription.indexOf('-', secondDotPos + 1);
		int plusSignPos = -1;
		
		int patchEndPos = -1;
		int idListEndPos = -1;
		
		this.major = Integer.valueOf(versionDescription.substring(0, firstDotPos));
		this.minor = Integer.valueOf(versionDescription.substring(firstDotPos + 1, secondDotPos));
		
		if (hyphenPos != -1) {
			patchEndPos = hyphenPos;
			plusSignPos = versionDescription.indexOf('+', hyphenPos + 1);
		}
		else {
			plusSignPos = versionDescription.indexOf('+', secondDotPos + 1);
		}
		
		if (plusSignPos != -1) {
			if (hyphenPos != -1) {
				idListEndPos = plusSignPos;
			}
			else {
				patchEndPos = plusSignPos;
			}
		}
		else {
			if (hyphenPos != -1) {
				idListEndPos = versionDescription.length();
			}
			else {
				patchEndPos = versionDescription.length();
			}
		}
		
		this.patch = Integer.valueOf(versionDescription.substring(secondDotPos + 1, patchEndPos));
		this.preReleaseIdentifiers = (hyphenPos == -1) ? new ArrayList<String>() : Splitter.on('.').splitToList(versionDescription.substring(hyphenPos + 1, idListEndPos));
		this.buildMetadata = (plusSignPos == -1) ? null : versionDescription.substring(plusSignPos + 1);
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getPatch() {
		return patch;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SemanticVersion that = (SemanticVersion) o;
		return major == that.major && minor == that.minor && patch == that.patch;
	}

	public Optional<ChangeType> computeChangeType(SemanticVersion version) {
		if (this.major != version.major) {
			return Optional.of(ChangeType.MAJOR);
		}
		if (this.minor != version.minor) {
			return Optional.of(ChangeType.MINOR);
		}
		if (this.patch != version.patch) {
			return Optional.of(ChangeType.PATCH);
		}
		return Optional.of(ChangeType.UNCHANGED);
	}

	@Override
	public int hashCode() {
		int result = major;
		result = 31 * result + minor;
		result = 31 * result + patch;
		return result;
	}

	@Override
	public String toString() {
		return major + "." + minor + "." + patch + 
			(preReleaseIdentifiers.size() > 0 ? "-" + Joiner.on('.').join(preReleaseIdentifiers) : "") + 
			(buildMetadata != null ? "+" + buildMetadata : "");
	}
	
	private boolean isInteger(String str) {
		for(int i = 0; i < str.length(); i++)
		{
			if(!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
		Compares two semantic version following the specifications at http://semver.org/spec/v2.0.0.html#spec-item-11
	*/
	@Override
	public int compareTo(SemanticVersion other) {
		if(this.major == other.major) {
			if(this.minor == other.minor) {
				if(this.patch == other.patch) {
					// "When major, minor, and patch are equal, a pre-release version has lower precedence than a normal version" as per spec.
					if(this.preReleaseIdentifiers.size() == 0 && other.preReleaseIdentifiers.size() > 0) {
						return 1;
					}
					else if(this.preReleaseIdentifiers.size() > 0 && other.preReleaseIdentifiers.size() == 0) {
						return -1;
					}
					
					// Compare all ids field by field, stopping at first difference.
					for(int i = 0; i < Math.min(this.preReleaseIdentifiers.size(), other.preReleaseIdentifiers.size()); i++) {
						String thisId = this.preReleaseIdentifiers.get(i);
						String otherId = other.preReleaseIdentifiers.get(i);
						
						if (isInteger(thisId)) {
							if (isInteger(otherId)) {
								// Two integer identifiers : compare them.
								int difference = Integer.valueOf(thisId) - Integer.valueOf(otherId);
								if (difference != 0) {
									return difference;
								}
							}
							else {
								// "Numeric identifiers always have lower precedence than non-numeric identifiers" as per spec.
								return -1;
							}
						}
						else {
							if (isInteger(otherId)) {
								// Same as above : int greater than string.
								return 1;
							}
							else {
								// Compare strings in lexicographic order.
								int difference = thisId.compareTo(otherId);
								if (difference != 0) {
									return difference;
								}
							}
						}
					}
					
					// "A larger set of pre-release fields has a higher precedence than a smaller set, if all of the preceding identifiers are equal", as per spec.
					return this.preReleaseIdentifiers.size() - other.preReleaseIdentifiers.size();
				}
				else {
					return this.patch - other.patch;
				}
			}
			else {
				return this.minor - other.minor;
			}
		}
		else {
			return this.major - other.major;
		}
	}
}
