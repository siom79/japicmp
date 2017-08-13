package japicmp.test;

import javax.xml.bind.annotation.XmlRootElement;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Annotations.OuterAnnotation2(outer = {@Annotations.InnerAnnotation2(inner = {@Annotations.InnerInnerAnnotation(innerInner = {1, 2}, number = 42)}),
	@Annotations.InnerAnnotation2(inner = {@Annotations.InnerInnerAnnotation(innerInner = {1, 2}, number = 42), @Annotations.InnerInnerAnnotation(innerInner = {1, 2}, number = 42)})})
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

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface StringArrayAnnotation {
		String[] values();
	}

	@StringArrayAnnotation(values = {"a", "b", "c"})
	public int fieldWithStringArrayAnnotation;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface ClassArrayAnnotation {
		Class<?>[] values();
	}

	@ClassArrayAnnotation(values = {String.class, Integer.class, Short.class})
	public int fieldWithClassArrayAnnotation;

	public enum AnnotationEnum {
		ONE, TWO, THREE, FOUR
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface EnumArrayAnnotation {
		AnnotationEnum[] values();
	}

	@EnumArrayAnnotation(values = {AnnotationEnum.ONE, AnnotationEnum.TWO, AnnotationEnum.THREE})
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

	@OuterAnnotation(values = {@InnerAnnotation(values = {1, 2, 3}), @InnerAnnotation(values = {1, 2, 3, 4})})
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

	@OuterAnnotation2(outer = {@InnerAnnotation2(inner = {@InnerInnerAnnotation(innerInner = {1, 2}, number = 42)}),
		@InnerAnnotation2(inner = {@InnerInnerAnnotation(innerInner = {1, 2}, number = 42), @InnerInnerAnnotation(innerInner = {1, 2}, number = 42)})})
	public int fieldWithInnerInnerAnnotation;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface FromIntArrayToIntAnnotation {
		int[] fromIntArrayToInt();
	}

	@FromIntArrayToIntAnnotation(fromIntArrayToInt = {1, 2, 3})
	public int fromIntArrayToIntField;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface IllegalCharacterAnnotation {
		String value() default "default";
	}

	@IllegalCharacterAnnotation(value = "\u0006")
	public void methodWithIllegalCharacterAnnotation() {

	}
}
