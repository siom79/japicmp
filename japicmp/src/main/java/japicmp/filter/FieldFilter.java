package japicmp.filter;

import javassist.CtField;

public interface FieldFilter extends ClassFilter {

	boolean matches(CtField ctField);
}
