package japicmp.maven;

import com.google.common.base.Optional;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionChange {
	private static final Pattern versionPattern = Pattern.compile(".*([0-9]+)\\.([0-9]+)\\.([0-9]+).*");
	private final List<File> oldArchives;
	private final List<File> newArchives;
	private final Parameter parameter;

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

	private static class SemanticVersion {
		private final int major;
		private final int minor;
		private final int patch;

		public SemanticVersion(int major, int minor, int patch) {
			this.major = major;
			this.minor = minor;
			this.patch = patch;
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
	}

	public VersionChange(List<File> oldArchives, List<File> newArchives, Parameter parameter) {
		this.oldArchives = oldArchives;
		this.newArchives = newArchives;
		this.parameter = parameter;
	}

	public Optional<ChangeType> computeChangeType() throws MojoFailureException {
		if (this.oldArchives.isEmpty()) {
			if (!"true".equalsIgnoreCase(this.parameter != null ? this.parameter.getIgnoreMissingOldVersion() : "false")) {
				throw new MojoFailureException("Please provide at least one old version.");
			} else {
				return Optional.absent();
			}
		}
		if (this.newArchives.isEmpty()) {
			if (!"true".equalsIgnoreCase(this.parameter != null ? this.parameter.getIgnoreMissingNewVersion() : "false")) {
				throw new MojoFailureException("Please provide at least one new version.");
			} else {
				return Optional.absent();
			}
		}
		List<SemanticVersion> oldVersions = new ArrayList<>();
		List<SemanticVersion> newVersions = new ArrayList<>();
		for (File file : this.oldArchives) {
			oldVersions.add(getVersion(file));
		}
		for (File file : this.newArchives) {
			newVersions.add(getVersion(file));
		}
		if (allVersionsTheSame(oldVersions) && allVersionsTheSame(newVersions)) {
			SemanticVersion oldVersion = oldVersions.get(0);
			SemanticVersion newVersion = newVersions.get(0);
			return oldVersion.computeChangeType(newVersion);
		} else {
			if (oldVersions.size() != newVersions.size()) {
				throw new MojoFailureException("Cannot compare versions because the number of old versions is different than the number of new versions.");
			} else {
				List<ChangeType> changeTypes = new ArrayList<>();
				for (int i=0; i<oldVersions.size(); i++) {
					SemanticVersion oldVersion = oldVersions.get(i);
					SemanticVersion newVersion = newVersions.get(i);
					Optional<ChangeType> changeTypeOptional = oldVersion.computeChangeType(newVersion);
					if (changeTypeOptional.isPresent()) {
						changeTypes.add(changeTypeOptional.get());
					}
				}
				ChangeType maxRank = ChangeType.UNCHANGED;
				for (ChangeType changeType : changeTypes) {
					if (changeType.getRank() > maxRank.getRank()) {
						maxRank = changeType;
					}
				}
				return Optional.fromNullable(maxRank);
			}
		}
	}

	private boolean allVersionsTheSame(List<SemanticVersion> versions) {
		boolean allVersionsTheSame = true;
		if (versions.size() > 1) {
			SemanticVersion firstVersion = versions.get(0);
			for (int i = 1; i < versions.size(); i++) {
				SemanticVersion version = versions.get(i);
				if (!firstVersion.equals(version)) {
					allVersionsTheSame = false;
					break;
				}
			}
		}
		return allVersionsTheSame;
	}

	private SemanticVersion getVersion(File file) throws MojoFailureException {
		String name = file.getName();
		Matcher matcher = versionPattern.matcher(name);
		if (matcher.matches()) {
			if (matcher.groupCount() >= 3) {
				try {
					int major = Integer.parseInt(matcher.group(1));
					int minor = Integer.parseInt(matcher.group(2));
					int patch = Integer.parseInt(matcher.group(3));
					return new SemanticVersion(major, minor, patch);
				} catch (NumberFormatException e) {
					throw new MojoFailureException(String.format("Could not convert version into three digits for file name: %s", name), e);
				}
			} else {
				throw new MojoFailureException(String.format("Could not find three digits separated by a point in file name: %s", name));
			}
		} else {
			throw new MojoFailureException(String.format("Could not find three digits separated by a point in file name: %s", name));
		}
	}
}
