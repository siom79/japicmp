package japicmp.output.markdown;

final class MarkdownStoredReference extends MarkdownReference {
	final int index;
	final String id;

	MarkdownStoredReference(int index, String id, MarkdownReference reference) {
		super(reference.href, reference.title);
		this.index = index;
		this.id = id;
	}

	MarkdownStoredReference(String id, MarkdownReference reference) {
		this(Integer.MAX_VALUE, id, reference);
	}

	MarkdownStoredReference(int index, MarkdownReference reference) {
		this(index, String.valueOf(index), reference);
	}

	int getIndex() {
		return index;
	}

	String getId() {
		return id;
	}

	MarkdownRefImage toImage(String alt) {
		return new MarkdownRefImage(alt, this);
	}

	MarkdownRefLink toLink(String text) {
		return new MarkdownRefLink(text, this);
	}

	@Override
	public String toString() {
		return brackets(id) + COLON + SPACE + super.toString();
	}
}
