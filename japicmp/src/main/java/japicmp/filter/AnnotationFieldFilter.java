package japicmp.filter;

import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnnotationFieldFilter extends AnnotationFilterBase implements FieldFilter {
	private static final Logger LOGGER = Logger.getLogger(AnnotationBehaviorFilter.class.getName());

	public AnnotationFieldFilter(String filterString) {
		super(filterString.substring(1));
	}

	@Override
	public boolean matches(CtField ctField) {
		List attributes = ctField.getFieldInfo().getAttributes();
		boolean hasAnnotation = hasAnnotation(attributes);
		if (!hasAnnotation) {
			CtClass declaringClass = ctField.getDeclaringClass();
			hasAnnotation = hasAnnotation(declaringClass.getClassFile().getAttributes());
			if (!hasAnnotation) {
				try {
					declaringClass = declaringClass.getDeclaringClass();
					if (declaringClass != null) {
						hasAnnotation = hasAnnotation(declaringClass.getClassFile().getAttributes());
					}
				} catch (NotFoundException e) {
					LOGGER.log(Level.FINE, "Failed to load class '" + declaringClass.getName() + "': " + e.getLocalizedMessage(), e);
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
