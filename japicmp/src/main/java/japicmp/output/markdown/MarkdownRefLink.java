package japicmp.output.markdown;

class MarkdownRefLink extends Markdown {
	private final String text;
	private final MarkdownStoredReference reference;

	MarkdownRefLink(String text, MarkdownStoredReference reference) {
		this.text = text != null ? text : EMPTY;
		this.reference = reference;
	}

	@Override
	public String toString() {
		return brackets(text) +
			(text.equals(reference.id) ? EMPTY : brackets(reference.id));
	}
}
