package japicmp.filter;

import japicmp.util.AnnotationHelper;

import java.util.List;

public class AnnotationFilterBase {
	protected final String annotationClassName;

	public AnnotationFilterBase(String annotationClassName) {
		this.annotationClassName = annotationClassName;
	}

	protected boolean hasAnnotation(List attributes) {
		return AnnotationHelper.hasAnnotation(attributes, annotationClassName);
	}

	public String getClassName() {
		return annotationClassName;
	}
}
