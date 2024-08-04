package japicmp.output.markdown;

import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.List;

final class MarkdownReferences extends Markdown {

	private final List<MarkdownStoredReference> references = new ArrayList<>();
	private int referenceId;

	MarkdownStoredReference make(String href, String id, String title) {
		final MarkdownReference reference = new MarkdownReference(href, title);
		return references.stream().filter(reference::equals).findAny()
			.orElseGet(() -> storeReference(id, reference));
	}

	MarkdownRefLink link(String href, String text, String title) {
		return make(href, text, title).toLink(text);
	}

	private MarkdownStoredReference storeReference(String id, MarkdownReference reference) {
		final MarkdownStoredReference stored;
		if (isIllegalReferenceId(id) || alreadyUsedReferenceId(id)) {
			stored = new MarkdownStoredReference(++referenceId, reference);
		} else {
			stored = new MarkdownStoredReference(id, reference);
		}
		references.add(stored);
		return stored;
	}

	private boolean isIllegalReferenceId(String text) {
		return text == null || text.isEmpty() || text.contains(BRACKET_OPEN) || text.contains(BRACKET_CLOSE);
	}

	private boolean alreadyUsedReferenceId(String id) {
		return references.stream().map(MarkdownStoredReference::getId).anyMatch(id::equals);
	}

	@Override
	public String toString() {
		return references.stream()
			.sorted(comparing(MarkdownStoredReference::getIndex).thenComparing(MarkdownStoredReference::getId))
			.map(MarkdownStoredReference::toString).collect(LINES);
	}
}
