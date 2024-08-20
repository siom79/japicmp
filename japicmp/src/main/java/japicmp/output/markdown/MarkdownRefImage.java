package japicmp.output.markdown;

final class MarkdownRefImage extends MarkdownRefLink {

	MarkdownRefImage(String alt, MarkdownStoredReference reference) {
		super(alt, reference);
	}

	@Override
	public String toString() {
		return BANG + super.toString();
	}
}
