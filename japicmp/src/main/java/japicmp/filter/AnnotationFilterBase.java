package japicmp.filter;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;

import java.util.List;

public class AnnotationFilterBase {
	protected final String annotationClassName;

	public AnnotationFilterBase(String annotationClassName) {
		this.annotationClassName = annotationClassName;
	}

	protected boolean hasAnnotation(List attributes) {
		for (Object obj : attributes) {
			if (obj instanceof AnnotationsAttribute) {
				AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) obj;
				Annotation[] annotations = annotationsAttribute.getAnnotations();
				for (Annotation annotation : annotations) {
					if (annotation.getTypeName().equals(annotationClassName)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
