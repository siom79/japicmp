package japicmp.filter;

import japicmp.util.AnnotationHelper;
import javassist.bytecode.AttributeInfo;

import java.util.List;

public class AnnotationFilterBase {
	protected final String annotationClassName;

	public AnnotationFilterBase(String annotationClassName) {
		this.annotationClassName = annotationClassName;
	}

	protected boolean hasAnnotation(List<? extends AttributeInfo> attributes) {
		return AnnotationHelper.hasAnnotation(attributes, annotationClassName);
	}

	public String getClassName() {
		return annotationClassName;
	}
}
