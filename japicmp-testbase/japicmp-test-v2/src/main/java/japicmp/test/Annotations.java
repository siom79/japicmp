package japicmp.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Annotations.OuterAnnotation2(outer = {
	@Annotations.InnerAnnotation2(inner = {@Annotations.InnerInnerAnnotation(innerInner = {1, 2}, number = 42)}),
	@Annotations.InnerAnnotation2(inner = {@Annotations.InnerInnerAnnotation(innerInner = {1, 2}, number = 42),
		@Annotations.InnerInnerAnnotation(innerInner = {1, 2}, number = 42)})
})
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

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface StringArrayAnnotation {
		String[] values();
	}

	@StringArrayAnnotation(values = {"b", "c", "d"})
	public int fieldWithStringArrayAnnotation;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface ClassArrayAnnotation {
		Class<?>[] values();
	}

	@ClassArrayAnnotation(values = {Integer.class, Short.class, Double.class})
	public int fieldWithClassArrayAnnotation;

	public enum AnnotationEnum {
		ONE, TWO, THREE, FOUR
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface EnumArrayAnnotation {
		AnnotationEnum[] values();
	}

	@EnumArrayAnnotation(values = {AnnotationEnum.TWO, AnnotationEnum.THREE, AnnotationEnum.FOUR})
	public int fieldWithEnumArrayAnnotation;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface InnerAnnotation {
		int[] values();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface OuterAnnotation {
		InnerAnnotation[] values();
	}

	@OuterAnnotation(values = {@InnerAnnotation(values = {2, 3, 4})})
	public int fieldWithOuterAnnotation;

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.TYPE})
	public @interface InnerInnerAnnotation {
		int[] innerInner();

		int number();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.TYPE})
	public @interface InnerAnnotation2 {
		InnerInnerAnnotation[] inner();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.TYPE})
	public @interface OuterAnnotation2 {
		InnerAnnotation2[] outer();
	}

	@OuterAnnotation2(outer = {@InnerAnnotation2(inner = {@InnerInnerAnnotation(innerInner = {2, 3}, number = 42)}),
		@InnerAnnotation2(inner = {@InnerInnerAnnotation(innerInner = {2, 3}, number = 42), @InnerInnerAnnotation(innerInner = {2, 3}, number = 42)})})
	public int fieldWithInnerInnerAnnotation;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface FromIntArrayToIntAnnotation {
		int fromIntArrayToInt();
	}

	@FromIntArrayToIntAnnotation(fromIntArrayToInt = 1)
	public int fromIntArrayToIntField;
}
