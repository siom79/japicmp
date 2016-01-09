package japicmp.filter;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;

import java.util.List;

public class AnnotationBehaviorFilter extends AnnotationFilterBase implements BehaviorFilter {

	public AnnotationBehaviorFilter(String filterString) {
		super(filterString.substring(1));
	}

	@Override
	public boolean matches(CtBehavior ctBehavior) {
		List attributes = ctBehavior.getMethodInfo().getAttributes();
		boolean hasAnnotation = hasAnnotation(attributes);
		if (!hasAnnotation) {
			CtClass declaringClass = ctBehavior.getDeclaringClass();
			hasAnnotation = hasAnnotation(declaringClass.getClassFile().getAttributes());
			if (!hasAnnotation) {
				try {
					declaringClass = declaringClass.getDeclaringClass();
					if (declaringClass != null) {
						hasAnnotation = hasAnnotation(declaringClass.getClassFile().getAttributes());
					}
				} catch (NotFoundException ignored) {
				}
			}
		}
		return hasAnnotation;
	}

	@Override
	public String toString() {
		return "@" + annotationClassName;
	}
}
