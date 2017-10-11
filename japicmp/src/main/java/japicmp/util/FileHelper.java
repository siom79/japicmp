package japicmp.util;

import japicmp.cmp.JApiCmpArchive;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
			JApiCmpArchive jApiCmpArchive = new JApiCmpArchive(file, "n.a.");
			jApiCmpArchives.add(jApiCmpArchive);
		}
		return jApiCmpArchives;
	}
}
