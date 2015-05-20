package japicmp.filter;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;

import java.util.regex.Pattern;

public class ClassFilter implements Filter {
    private final Pattern pattern;
    private final String className;

    public ClassFilter(String className) {
        this.className = className;
        String regEx = className.replace(".", "\\.");
        regEx = regEx.replace("*", ".*");
        pattern = Pattern.compile(regEx);
    }

    @Override
    public String toString() {
        return this.className;
    }

    @Override
    public boolean matches(CtClass ctClass) {
        String name = ctClass.getName();
        return pattern.matcher(name).matches();
    }

    @Override
    public boolean matches(CtBehavior ctBehavior) {
        CtClass declaringClass = ctBehavior.getDeclaringClass();
        return matches(declaringClass);
    }

    @Override
    public boolean matches(CtField ctField) {
        CtClass declaringClass = ctField.getDeclaringClass();
        return matches(declaringClass);
    }
}
