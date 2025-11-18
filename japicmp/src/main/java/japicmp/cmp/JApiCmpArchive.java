package japicmp.cmp;

import japicmp.versioning.Version;

import java.io.File;

public class JApiCmpArchive {
	private File file;
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("JApiCmpArchive{");
		sb.append("file=").append(file);
		sb.append(", version=").append(version);
		sb.append('}');
		return sb.toString();
	}
}
