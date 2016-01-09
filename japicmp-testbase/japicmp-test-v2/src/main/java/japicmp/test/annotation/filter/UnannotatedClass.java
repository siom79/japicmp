package japicmp.test.annotation.filter;

/**
 * Unannotated class which will contain changes.
 * <p/>
 * --> test should not fail on this one, since class is not annotated
 */
public class UnannotatedClass {
	public UnannotatedClass(int a /* CHANGE , String b */) {

	}
}
