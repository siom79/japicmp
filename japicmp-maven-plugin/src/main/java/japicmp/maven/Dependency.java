package japicmp.maven;

public class Dependency implements DependencyDescriptor {
	private String groupId;
	private String artifactId;
	private String version;
	private String scope;
	private String systemPath;
	private String classifier;
	private String type = "jar";

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getSystemPath() {
		return systemPath;
	}

	public void setSystemPath(String systemPath) {
		this.systemPath = systemPath;
	}

	public String getClassifier() {
		return classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Dependency{" +
			"groupId='" + groupId + '\'' +
			", artifactId='" + artifactId + '\'' +
			", version='" + version + '\'' +
			", scope='" + scope + '\'' +
			", systemPath='" + systemPath + '\'' +
			", classifier='" + classifier + '\'' +
			", type='" + type + '\'' +
			'}';
	}

}
