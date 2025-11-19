package japicmp.util;

import japicmp.cmp.JApiCmpArchive;
import japicmp.versioning.SemanticVersion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileHelper {

	private FileHelper() {
		// intentionally left empty
	}

	public static List<File> toFileList(List<JApiCmpArchive> archives) {
		List<File> files = new ArrayList<>(archives.size());
		for (JApiCmpArchive archive : archives) {
			files.add(archive.getFile());
		}
		return files;
	}

	public static List<String> toVersionList(List<JApiCmpArchive> archives) {
		List<String> versions = new ArrayList<>(archives.size());
		for (JApiCmpArchive archive : archives) {
			versions.add(archive.getVersion().getStringVersion());
		}
		return versions;
	}

	public static List<JApiCmpArchive> createFileList(String option) {
		String[] parts = option.split(";");
		List<JApiCmpArchive> jApiCmpArchives = new ArrayList<>(parts.length);
		for (String part : parts) {
			File file = new File(part);
			JApiCmpArchive jApiCmpArchive = new JApiCmpArchive(file, guessVersion(file));
			jApiCmpArchives.add(jApiCmpArchive);
		}
		return jApiCmpArchives;
	}

	public static String guessVersion(final File file) {
		String name = file.getName();
		Optional<SemanticVersion> semanticVersion = japicmp.versioning.Version.getSemanticVersion(name);
		String version = semanticVersion.isPresent() ? semanticVersion.get().toString() : "n.a.";
		if (name.contains("SNAPSHOT")) {
			version += "-SNAPSHOT";
		}
		return version;
	}
}
