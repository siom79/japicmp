package japicmp.test.annotation.filter;

/**
 * Annotated class which will contain changes.
 */
@PublicAPI
public class AnnotatedClass {
	public AnnotatedClass(int a) {
		System.out.println("a = " + a);
	}
}
