package japicmp.maven;

/**
 * Class for storing Maven dependency information. This includes the {@code groupId},
 * {@code artifactId}, {@code version}, {@code scope}, {@code systemPath}, {@code classifier} and
 * {@code type}.
 */
public class Dependency implements DependencyDescriptor {

  /* Dependency group ID. */
  private String groupId;
  /* Dependency artifact ID. */
  private String artifactId;
  /* Dependency version. */
  private String version;
  /* Dependency scope. */
  private String scope;
  /* Dependency system path. */
  private String systemPath;
  /* Dependency classifier. */
  private String classifier;
  /* Dependency type. */
  private String type = "jar";

  /**
   * Default constructor.
   */
  public Dependency() {
    /* Intentionally left blank. */
  }

  /**
   * Returns the dependency group ID.
   *
   * @return the dependency group ID
   */
  public String getGroupId() {
    return groupId;
  }

  /**
   * Sets the dependency group ID.
   *
   * @param groupId the new dependency group ID
   */
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  /**
   * Returns the dependency artifact ID.
   *
   * @return the dependency artifact ID
   */
  public String getArtifactId() {
    return artifactId;
  }

  /**
   * Sets the dependency artifact ID.
   *
   * @param artifactId the new dependency artifact ID
   */
  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  /**
   * Returns the dependency version.
   *
   * @return the dependency version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets the dependency version.
   *
   * @param version the new dependency version
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Returns the dependency scope.
   *
   * @return the dependency scope
   */
  public String getScope() {
    return scope;
  }

  /**
   * Sets the dependency scope.
   *
   * @param scope the new dependency scope
   */
  public void setScope(String scope) {
    this.scope = scope;
  }

  /**
   * Returns the dependency system path.
   *
   * @return the dependency system path
   */
  public String getSystemPath() {
    return systemPath;
  }

  /**
   * Sets the dependency system path
   *
   * @param systemPath the new dependency system path
   */
  public void setSystemPath(String systemPath) {
    this.systemPath = systemPath;
  }

  /**
   * Returns the dependency classifier.
   *
   * @return the dependency classifier
   */
  public String getClassifier() {
    return classifier;
  }

  /**
   * Sets the dependency classifier.
   *
   * @param classifier the new dependency classifier
   */
  public void setClassifier(String classifier) {
    this.classifier = classifier;
  }

  /**
   * Returns the dependency type.
   *
   * @return the dependency type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the dependency type.
   *
   * @param type the new dependency type
   */
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
