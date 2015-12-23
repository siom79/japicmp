package japicmp.util;

import japicmp.model.JApiClassType;
import javassist.CtClass;

public class ClassHelper {

	private ClassHelper() {

	}

	public static JApiClassType.ClassType getType(CtClass ctClass) {
		if (ctClass.isAnnotation()) {
			return JApiClassType.ClassType.ANNOTATION;
		} else if (ctClass.isEnum()) {
			return JApiClassType.ClassType.ENUM;
		} else if (ctClass.isInterface()) {
			return JApiClassType.ClassType.INTERFACE;
		} else {
			return JApiClassType.ClassType.CLASS;
		}
	}
}
