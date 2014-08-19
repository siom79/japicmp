package japicmp.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Annotations {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Author {
		String name();
		int year();
        String language() default "en";
	}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface FieldAnnotation {
        String value() default "v";
    }

    public int fieldAnnotationRemoved;

    @FieldAnnotation
    public int fieldAnnotationAdded;

    @FieldAnnotation
    public int fieldAnnotationRemains;

    public int fieldAnnotationAbsent;

    @FieldAnnotation(value = "v1")
    public int fieldAnnotationValueNew;

    @FieldAnnotation
    public int fieldAnnotationValueRemoved;

    @FieldAnnotation(value = "v1")
    public int fieldAnnotationValueUnchanged;

    @FieldAnnotation(value = "v2")
    public int fieldAnnotationValueModified;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface MethodAnnotation {
        String value() default "default";
    }

    public void methodAnnotationRemoved() {

    }

    @MethodAnnotation
    public void methodAnnotationAdded() {

    }

    @MethodAnnotation
    public void methodAnnotationRemains() {

    }

    @MethodAnnotation(value = "new")
    public void methodAnnotationValueNew() {

    }

    @MethodAnnotation
    public void methodAnnotationValueRemoved() {

    }

    @MethodAnnotation(value = "unchanged")
    public void methodAnnotationValueUnchanged() {

    }

    @MethodAnnotation(value = "modifiedNew")
    public void methodAnnotationValueModified() {

    }
	
	public class Shakespeare {
		
	}
	
	@Author(name = "Goethe", year = 1749)
	public class Goethe {
		
	}

    @Author(name = "Schiller", year = 1759)
    public class AuthorAnnotationChanges {

    }

    @Author(name = "Brecht", year = 1898, language = "de")
    public class AuthorAnnotationGetsNewValue {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface IntArrayAnnotation {
        int[] values();
    }

    @IntArrayAnnotation(values = {2, 3, 4})
    public int fieldWithIntArrayAnnotation;
}
