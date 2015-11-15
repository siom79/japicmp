package japicmp.filter;

import javassist.CtField;

public interface FieldFilter extends Filter {

	boolean matches(CtField ctField);
}
