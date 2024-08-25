package japicmp.output.xml;

import java.util.Optional;

public class XmlOutputGeneratorOptions {
	private boolean createSchemaFile = false;
	private Optional<String> title = Optional.empty();
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
		this.title = Optional.ofNullable(title);
	}

	public String getSemanticVersioningInformation() {
		return semanticVersioningInformation;
	}

	public void setSemanticVersioningInformation(String semanticVersioningInformation) {
		this.semanticVersioningInformation = semanticVersioningInformation;
	}
}
