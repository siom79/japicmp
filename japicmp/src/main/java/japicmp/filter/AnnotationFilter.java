package japicmp.filter;

import japicmp.exception.JApiCmpException;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;

public class AnnotationFilter implements Filter {
	private final Class annotation;

	public AnnotationFilter(String filterString) {
		String clazz = filterString.substring(1);
		try {
			annotation = Class.forName(clazz);
		} catch (ClassNotFoundException e) {
			throw new JApiCmpException(JApiCmpException.Reason.ClassLoading, "Unable to load specified annotation filter: " + clazz, e);
		}
	}

	@Override
	public boolean matches(CtClass ctClass) {
		return ctClass.hasAnnotation(annotation);
	}

	@Override
	public boolean matches(CtBehavior ctBehavior) {
		return ctBehavior.hasAnnotation(annotation) || ctBehavior.getDeclaringClass().hasAnnotation(annotation);
	}

	@Override
	public boolean matches(CtField ctField) {
		return ctField.hasAnnotation(annotation) || ctField.getDeclaringClass().hasAnnotation(annotation);
	}

	@Override
	public String toString() {
		return "Annotation filter: @" + annotation.getName();
	}
}
