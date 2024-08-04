package japicmp.output.markdown;

class MarkdownLink extends MarkdownReference {

	final String text;

	MarkdownLink(String text, String href, String title) {
		super(href, title);
		this.text = text == null ? EMPTY : text;
	}

	@Override
	public String toString() {
		return brackets(text) + parenthesis(super.toString());
	}
}
