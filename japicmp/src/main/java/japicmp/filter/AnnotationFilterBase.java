package japicmp.filter;

import japicmp.exception.JApiCmpException;
import japicmp.util.AnnotationHelper;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;

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
