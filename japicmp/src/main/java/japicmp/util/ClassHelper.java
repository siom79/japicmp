package japicmp.util;

import japicmp.model.JApiClass;
import javassist.CtClass;

public class ClassHelper {

	private ClassHelper() {

	}

    public static JApiClass.Type getType(CtClass ctClass) {
        if(ctClass.isAnnotation()) {
            return JApiClass.Type.ANNOTATION;
        } else if(ctClass.isEnum()) {
            return JApiClass.Type.ENUM;
        } else if(ctClass.isInterface()) {
            return JApiClass.Type.INTERFACE;
        } else {
            return JApiClass.Type.CLASS;
        }
    }
}
