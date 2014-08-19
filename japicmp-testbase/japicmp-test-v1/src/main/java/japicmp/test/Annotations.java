package japicmp.test;

import javax.xml.bind.annotation.XmlRootElement;
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
        String value() default "default";
    }

    @FieldAnnotation
    public int fieldAnnotationRemoved;

    public int fieldAnnotationAdded;

    @FieldAnnotation
    public int fieldAnnotationRemains;

    public int fieldAnnotationAbsent;

    @FieldAnnotation
    public int fieldAnnotationValueNew;

    @FieldAnnotation(value = "v1")
    public int fieldAnnotationValueRemoved;

    @FieldAnnotation(value = "v1")
    public int fieldAnnotationValueUnchanged;

    @FieldAnnotation(value = "v1")
    public int fieldAnnotationValueModified;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface MethodAnnotation {
        String value() default "default";
    }

    @MethodAnnotation
    public void methodAnnotationRemoved() {

    }

    public void methodAnnotationAdded() {

    }

    @MethodAnnotation
    public void methodAnnotationRemains() {

    }

    @MethodAnnotation
    public void methodAnnotationValueNew() {

    }

    @MethodAnnotation(value = "removed")
    public void methodAnnotationValueRemoved() {

    }

    @MethodAnnotation(value = "unchanged")
    public void methodAnnotationValueUnchanged() {

    }

    @MethodAnnotation(value = "modified")
    public void methodAnnotationValueModified() {

    }

    @XmlRootElement
    @Author(name = "Shakespeare", year = 1564)
    public class Shakespeare {

    }

    public class Goethe {

    }

    @Author(name = "Brecht", year = 1898)
    public class AuthorAnnotationChanges {

    }

    @Author(name = "Brecht", year = 1898)
    public class AuthorAnnotationGetsNewValue {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface IntArrayAnnotation {
        int[] values();
    }

    @IntArrayAnnotation(values = {1, 2, 3})
    public int fieldWithIntArrayAnnotation;
}
