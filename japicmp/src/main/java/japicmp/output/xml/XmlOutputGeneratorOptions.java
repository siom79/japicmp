package japicmp.output.xml;

import com.google.common.base.Optional;

public class XmlOutputGeneratorOptions {
	private boolean createSchemaFile = false;
	private Optional<String> title = Optional.absent();

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
}
