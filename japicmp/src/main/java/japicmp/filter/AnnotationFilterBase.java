package japicmp.filter;

import japicmp.util.AnnotationHelper;

import java.util.List;

public class AnnotationFilterBase {
	protected final String annotationClassName;

	public AnnotationFilterBase(String annotationClassName) {
		this.annotationClassName = annotationClassName;
	}

	protected boolean hasAnnotation(List attributes) {
		if (AnnotationHelper.hasAnnotation(attributes, annotationClassName)) {
			return true;
		}
		return false;
	}

	public String getClassName() {
		return annotationClassName;
	}
}
