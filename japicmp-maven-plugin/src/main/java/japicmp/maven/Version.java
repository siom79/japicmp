package japicmp.maven;

public class Version {
	private Dependency dependency;
	private ConfigurationFile file;

	public Dependency getDependency() {
		return dependency;
	}

	public void setDependency(Dependency dependency) {
		this.dependency = dependency;
	}

	public ConfigurationFile getFile() {
		return file;
	}

	public void setFile(ConfigurationFile file) {
		this.file = file;
	}
}
