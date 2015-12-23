package japicmp.filter;

import javassist.CtClass;
import javassist.NotFoundException;

import java.util.List;

public class AnnotationClassFilter extends AnnotationFilterBase implements ClassFilter {

	public AnnotationClassFilter(String filterString) {
		super(filterString.substring(1));
	}

	@Override
	public boolean matches(CtClass ctClass) {
		List attributes = ctClass.getClassFile().getAttributes();
		boolean hasAnnotation = hasAnnotation(attributes);
		if (!hasAnnotation) {
			try {
				CtClass declaringClass = ctClass.getDeclaringClass();
				if (declaringClass != null) {
					attributes = declaringClass.getClassFile().getAttributes();
					hasAnnotation = hasAnnotation(attributes);
				}
			} catch (NotFoundException ignored) {
			}
		}
		return hasAnnotation;
	}

	@Override
	public String toString() {
		return "@" + annotationClassName;
	}
}
