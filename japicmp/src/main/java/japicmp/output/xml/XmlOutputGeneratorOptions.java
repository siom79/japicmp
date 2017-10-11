package japicmp.output.xml;

import japicmp.util.Optional;

public class XmlOutputGeneratorOptions {
	private boolean createSchemaFile = false;
	private Optional<String> title = Optional.absent();
	private String semanticVersioningInformation = "n.a.";

	public boolean isCreateSchemaFile() {
		return createSchemaFile;
	}

	public void setCreateSchemaFile(boolean createSchemaFile) {
		this.createSchemaFile = createSchemaFile;
	}

	public Optional<String> getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = Optional.fromNullable(title);
	}

	public String getSemanticVersioningInformation() {
		return semanticVersioningInformation;
	}

	public void setSemanticVersioningInformation(String semanticVersioningInformation) {
		this.semanticVersioningInformation = semanticVersioningInformation;
	}
}
