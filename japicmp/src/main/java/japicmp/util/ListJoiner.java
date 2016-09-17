package japicmp.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListJoiner<T> {
	private String separator = ";";
	private Comparator<T> comparator = null;
	private ListJoinerToString<T> toStringBuilder = null;

	public ListJoiner<T> on(String separator) {
		this.separator = separator;
		return this;
	}

	public ListJoiner<T> sort(Comparator<T> comparator) {
		this.comparator = comparator;
		return this;
	}

	public ListJoiner<T> toStringBuilder(ListJoinerToString<T> listJoinerToString) {
		this.toStringBuilder = listJoinerToString;
		return this;
	}

	public interface ListJoinerToString<T> {
		String toString(T t);
	}

	public String join(List<T> items) {
		return join(new StringBuilder(), items).toString();
	}

	public StringBuilder join(StringBuilder sb, List<T> items) {
		if (items.size() == 0) {
			sb.append("n.a.");
		} else {
			if (this.comparator != null) {
				Collections.sort(items, this.comparator);
			}
			int counter = 0;
			for (T item : items) {
				if (item == null) {
					continue;
				}
				if (counter > 0) {
					sb.append(this.separator);
				}
				if (this.toStringBuilder != null) {
					sb.append(this.toStringBuilder.toString(item));
				} else {
					sb.append(item.toString());
				}
				counter++;
			}
		}
		return sb;
	}
}
