package japicmp.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PACKAGE})
public @interface TestAnnotation {

	@Retention(RetentionPolicy.RUNTIME)
	@Target({})
	@interface Type {
		String label();
	}

	String name() default "default-name";
	@TestAnnotation(name = "recursion", list = {"r", "r2"}, type = @TestAnnotation.Type(label = "test-recursion"))
	String[] list();
	Type type();
}
