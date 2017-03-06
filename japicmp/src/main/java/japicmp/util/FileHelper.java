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
}
