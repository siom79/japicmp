package japicmp.cmp;

import japicmp.versioning.Version;

import java.io.File;

public class JApiCmpArchive {
	private File file;
	private byte[] bytes;
	private final Version version;

	public JApiCmpArchive(File file, String version) {
		this.file = file;
		this.version = new Version(version);
	}

	public JApiCmpArchive(byte[] bytes, String version) {
		this.bytes = bytes;
		this.version = new Version(version);
	}

	public File getFile() {
		return file;
	}

	public Version getVersion() {
		return version;
	}

	public byte[] getBytes() {
		return bytes;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("JApiCmpArchive{");
		sb.append("file=").append(file);
		sb.append(", bytes=");
		if (bytes == null) sb.append("null");
		else {
			sb.append('[');
			for (int i = 0; i < bytes.length; ++i)
				sb.append(i == 0 ? "" : ", ").append(bytes[i]);
			sb.append(']');
		}
		sb.append(", version=").append(version);
		sb.append('}');
		return sb.toString();
	}
}
