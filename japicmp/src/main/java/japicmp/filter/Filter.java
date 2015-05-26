package japicmp.filter;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;

public interface Filter {

    boolean matches(CtClass ctClass);

    boolean matches(CtBehavior ctBehavior);

    boolean matches(CtField ctField);
}
