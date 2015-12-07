package japicmp.filter;

import japicmp.exception.JApiCmpException;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class AnnotationFilterBase {
	protected final String annotationClassName;
	protected Class<?> annotation;

	protected AnnotationFilterBase(String className) {
		this.annotationClassName = className;
	}

	protected Class resolveAnnotation(ClassPool classPool) {
		if (annotation == null) {
			try {
				CtClass annotationCtClass = classPool.get(annotationClassName);
				try {
					annotation = Class.forName(annotationClassName);
				} catch (ClassNotFoundException e) {
					try {
						annotation = annotationCtClass.toClass();
					} catch (CannotCompileException e1) {
						throw JApiCmpException.forClassLoading(e1, annotationClassName);
					}
				}
			} catch (NotFoundException e) {
				throw JApiCmpException.forClassLoading(e, annotationClassName);
			}
		}
		return annotation;
	}
}
