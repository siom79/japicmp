package japicmp.maven;

import japicmp.cmp.JApiCmpArchive;
import japicmp.util.Optional;
import japicmp.versioning.SemanticVersion;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionChange {
	private static final Pattern versionPattern = Pattern.compile(".*([0-9]+)\\.([0-9]+)\\.([0-9]+).*");
	private final List<JApiCmpArchive> oldArchives;
	private final List<JApiCmpArchive> newArchives;
	private final Parameter parameter;

	public VersionChange(List<JApiCmpArchive> oldArchives, List<JApiCmpArchive> newArchives, Parameter parameter) {
		this.oldArchives = oldArchives;
		this.newArchives = newArchives;
		this.parameter = parameter;
	}

	public Optional<SemanticVersion.ChangeType> computeChangeType() throws MojoFailureException {
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
		for (JApiCmpArchive file : this.oldArchives) {
			Optional<SemanticVersion> semanticVersion = file.getVersion().getSemanticVersion();
			if (semanticVersion.isPresent()) {
				oldVersions.add(semanticVersion.get());
			}
		}
		for (JApiCmpArchive file : this.newArchives) {
			Optional<SemanticVersion> semanticVersion = file.getVersion().getSemanticVersion();
			if (semanticVersion.isPresent()) {
				newVersions.add(semanticVersion.get());
			}
		}
		if (oldVersions.isEmpty()) {
			throw new MojoFailureException("Could not extract semantic version for at least one old version. Please " +
				"follow the rules for semantic versioning.");
		}
		if (newVersions.isEmpty()) {
			throw new MojoFailureException("Could not extract semantic version for at least one new version. Please " +
				"follow the rules for semantic versioning.");
		}
		if (allVersionsTheSame(oldVersions) && allVersionsTheSame(newVersions)) {
			SemanticVersion oldVersion = oldVersions.get(0);
			SemanticVersion newVersion = newVersions.get(0);
			return oldVersion.computeChangeType(newVersion);
		} else {
			if (oldVersions.size() != newVersions.size()) {
				throw new MojoFailureException("Cannot compare versions because the number of old versions is different than the number of new versions.");
			} else {
				List<SemanticVersion.ChangeType> changeTypes = new ArrayList<>();
				for (int i=0; i<oldVersions.size(); i++) {
					SemanticVersion oldVersion = oldVersions.get(i);
					SemanticVersion newVersion = newVersions.get(i);
					Optional<SemanticVersion.ChangeType> changeTypeOptional = oldVersion.computeChangeType(newVersion);
					if (changeTypeOptional.isPresent()) {
						changeTypes.add(changeTypeOptional.get());
					}
				}
				SemanticVersion.ChangeType maxRank = SemanticVersion.ChangeType.UNCHANGED;
				for (SemanticVersion.ChangeType changeType : changeTypes) {
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
