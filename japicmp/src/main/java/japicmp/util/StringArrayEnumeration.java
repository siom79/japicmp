package japicmp.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class StringArrayEnumeration implements Enumeration<String> {
	private final String[] array;
	private int pos = 0;

	public StringArrayEnumeration(String[] array) {
		this.array = new String[array.length];
		System.arraycopy(array, 0, this.array, 0, array.length);
	}

	public boolean hasMoreElements() {
		return pos < array.length;
	}

	public String nextElement() {
		if (hasMoreElements()) {
			return array[pos++];
		} else {
			throw new NoSuchElementException();
		}
	}

	public String inspectNextElement() {
		if (hasMoreElements()) {
			return array[pos];
		} else {
			throw new NoSuchElementException();
		}
	}
}
