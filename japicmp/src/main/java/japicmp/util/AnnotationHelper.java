package japicmp.util;

import japicmp.util.Optional;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiAnnotation;
import japicmp.model.JApiChangeStatus;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationHelper {

	public interface AnnotationsAttributeCallback<T> {
		AnnotationsAttribute getAnnotationsAttribute(T t);
	}

	public static <T> void computeAnnotationChanges(List<JApiAnnotation> annotations, Optional<T> oldClassOptional, Optional<T> newClassOptional,
													JarArchiveComparatorOptions options, AnnotationsAttributeCallback<T> annotationsAttributeCallback) {
		if (!options.isNoAnnotations()) {
			if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
				T oldClass = oldClassOptional.get();
				T newClass = newClassOptional.get();
				AnnotationsAttribute oldAnnotationsAttribute = annotationsAttributeCallback.getAnnotationsAttribute(oldClass);
				AnnotationsAttribute newAnnotationsAttribute = annotationsAttributeCallback.getAnnotationsAttribute(newClass);
				Map<String, Annotation> oldAnnotationMap;
				Map<String, Annotation> newAnnotationMap;
				if (oldAnnotationsAttribute != null) {
					oldAnnotationMap = buildAnnotationMap(oldAnnotationsAttribute.getAnnotations());
				} else {
					oldAnnotationMap = new HashMap<>();
				}
				if (newAnnotationsAttribute != null) {
					newAnnotationMap = buildAnnotationMap(newAnnotationsAttribute.getAnnotations());
				} else {
					newAnnotationMap = new HashMap<>();
				}
				for (Annotation annotation : oldAnnotationMap.values()) {
					Annotation foundAnnotation = newAnnotationMap.get(annotation.getTypeName());
					if (foundAnnotation != null) {
						JApiAnnotation jApiAnnotation = new JApiAnnotation(annotation.getTypeName(), Optional.of(annotation), Optional.of(foundAnnotation), JApiChangeStatus.UNCHANGED);
						annotations.add(jApiAnnotation);
					} else {
						JApiAnnotation jApiAnnotation = new JApiAnnotation(annotation.getTypeName(), Optional.of(annotation), Optional.<Annotation>absent(), JApiChangeStatus.REMOVED);
						annotations.add(jApiAnnotation);
					}
				}
				for (Annotation annotation : newAnnotationMap.values()) {
					Annotation foundAnnotation = oldAnnotationMap.get(annotation.getTypeName());
					if (foundAnnotation == null) {
						JApiAnnotation jApiAnnotation = new JApiAnnotation(annotation.getTypeName(), Optional.<Annotation>absent(), Optional.of(annotation), JApiChangeStatus.NEW);
						annotations.add(jApiAnnotation);
					}
				}
			} else {
				if (oldClassOptional.isPresent()) {
					T oldClass = oldClassOptional.get();
					AnnotationsAttribute oldAnnotationsAttribute = annotationsAttributeCallback.getAnnotationsAttribute(oldClass);
					if (oldAnnotationsAttribute != null) {
						Map<String, Annotation> oldAnnotationMap = buildAnnotationMap(oldAnnotationsAttribute.getAnnotations());
						for (Annotation annotation : oldAnnotationMap.values()) {
							JApiAnnotation jApiAnnotation = new JApiAnnotation(annotation.getTypeName(), Optional.of(annotation), Optional.<Annotation>absent(),
								JApiChangeStatus.REMOVED);
							annotations.add(jApiAnnotation);
						}
					}
				}
				if (newClassOptional.isPresent()) {
					T newClass = newClassOptional.get();
					AnnotationsAttribute newAnnotationsAttribute = annotationsAttributeCallback.getAnnotationsAttribute(newClass);
					if (newAnnotationsAttribute != null) {
						Map<String, Annotation> newAnnotationMap = buildAnnotationMap(newAnnotationsAttribute.getAnnotations());
						for (Annotation annotation : newAnnotationMap.values()) {
							JApiAnnotation jApiAnnotation = new JApiAnnotation(annotation.getTypeName(), Optional.<Annotation>absent(), Optional.of(annotation), JApiChangeStatus.NEW);
							annotations.add(jApiAnnotation);
						}
					}
				}
			}
		}
	}

	private static Map<String, Annotation> buildAnnotationMap(Annotation[] annotations) {
		Map<String, Annotation> map = new HashMap<>();
		for (Annotation annotation : annotations) {
			map.put(annotation.getTypeName(), annotation);
		}
		return map;
	}

	public static boolean hasAnnotation(ClassFile classFile, String annotationClassName) {
		List attributes = classFile.getAttributes();
		return hasAnnotation(attributes, annotationClassName);
	}

	public static boolean hasAnnotation(List attributes, String annotationClassName) {
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
