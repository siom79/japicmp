package japicmp.maven;

public class ConfigurationFile implements DependencyDescriptor {
	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
