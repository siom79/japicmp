package japicmp.output.markdown;

final class MarkdownImage extends MarkdownLink {

	MarkdownImage(String alt, String href, String title) {
		super(alt, href, title);
	}

	@Override
	public String toString() {
		return BANG + super.toString();
	}
}
