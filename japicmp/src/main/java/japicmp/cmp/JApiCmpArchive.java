package japicmp.cmp;

import japicmp.versioning.Version;

import java.io.File;
import java.util.Optional;

public class JApiCmpArchive {
	private File file;
	private byte[] bytes;
	private final Version version;
	private String name;

	public JApiCmpArchive(File file, String version) {
		this.file = file;
		this.version = new Version(version);
	}

	public JApiCmpArchive(byte[] bytes, String version, String name) {
		this.bytes = bytes;
		this.version = new Version(version);
        this.name = name;
    }

	public Optional<File> getFile() {
		return Optional.ofNullable(file);
	}

	public Version getVersion() {
		return version;
	}

	public Optional<byte[]> getBytes() {
		return Optional.ofNullable(bytes);
	}

	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("JApiCmpArchive{");
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
		sb.append(", name='").append(name).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
