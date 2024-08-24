package japicmp.output.html;

import java.util.Optional;

public class HtmlOutputGeneratorOptions {
	private Optional<String> title = Optional.empty();
	private String semanticVersioningInformation = "n.a.";

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
