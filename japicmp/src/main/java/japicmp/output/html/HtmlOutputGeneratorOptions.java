package japicmp.output.html;

import japicmp.util.Optional;

public class HtmlOutputGeneratorOptions {
	private Optional<String> title = Optional.absent();
	private String semanticVersioningInformation = "n.a.";

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
