package japicmp.test.util;

import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;

import java.io.File;
import java.util.List;

public class Helper {

    public static File getArchive(String filename) {
        return new File("target" + File.separator + filename);
    }

    public static JApiClass getJApiClass(List<JApiClass> jApiClasses, String fqn) {
        for (JApiClass jApiClass : jApiClasses) {
            if (jApiClass.getFullyQualifiedName().equals(fqn)) {
                return jApiClass;
            }
        }
        throw new IllegalArgumentException("No class found with name " + fqn + ".");
    }

    public static JApiMethod getJApiMethod(List<JApiMethod> jApiMethods, String name) {
        for(JApiMethod jApiMethod : jApiMethods) {
            if(jApiMethod.getName().equals(name)) {
                return jApiMethod;
            }
        }
        throw new IllegalArgumentException("No method found with name " + name + ".");
    }
}
