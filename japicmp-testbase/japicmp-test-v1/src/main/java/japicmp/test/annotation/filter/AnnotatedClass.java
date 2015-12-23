package japicmp.test.annotation.filter;

/**
 * Annotated class which will contain changes.
 */
@PublicAPI
public class AnnotatedClass {
	public int f;

	public AnnotatedClass(int a, String removed) {
		System.out.println("a = " + a);
		this.f = a;
	}

	public int getF() {
		return f;
	}
}
