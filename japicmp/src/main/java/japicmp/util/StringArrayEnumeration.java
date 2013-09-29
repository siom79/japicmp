package japicmp.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class StringArrayEnumeration implements Enumeration<String> {
    private final String[] array;
    private int pos = 0;

    public StringArrayEnumeration(String[] array) {
        this.array = array;
    }

    @Override
    public boolean hasMoreElements() {
        return pos < array.length;
    }

    @Override
    public String nextElement() {
        if(hasMoreElements()) {
            return array[pos++];
        } else {
            throw new NoSuchElementException();
        }
    }
}
