package japicmp.cmp;

import japicmp.versioning.Version;

import java.io.File;

public class JApiCmpArchive {
	private final File file;
	private final Version version;

	public JApiCmpArchive(File file, String version) {
		this.file = file;
		this.version = new Version(version);
	}

	public File getFile() {
		return file;
	}

	public Version getVersion() {
		return version;
	}
}
