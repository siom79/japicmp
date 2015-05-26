package japicmp.filter;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;

import java.util.regex.Pattern;

public class PackageFilter implements Filter {
    private final Pattern pattern;
    private final String packageName;

    public PackageFilter(String packageName) {
        this.packageName = packageName;
        String regEx = packageName.replace(".", "\\.");
        regEx = regEx.replace("*", ".*");
        regEx = regEx + "(\\.[^\\.]+)*";
        pattern = Pattern.compile(regEx);
    }

    @Override
    public String toString() {
        return this.packageName;
    }

    @Override
    public boolean matches(CtClass ctClass) {
        String name = ctClass.getPackageName();
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
