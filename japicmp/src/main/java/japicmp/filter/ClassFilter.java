package japicmp.filter;

import javassist.CtClass;

public interface ClassFilter extends Filter {

	boolean matches(CtClass ctClass);
}
