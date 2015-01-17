package japicmp.test.semver100.a;

public class SemverTestee100a {

	@Deprecated
	public void methodAnnotationDeprecatedAddedAndParameterIntAdded(int value) {

	}

	@Deprecated
	void methodAnnotationDeprecatedAdded() {

	}

	@Deprecated
	protected String methodReturnValueChangesFromVoidToString() {
		return null;
	}

	private void methodAdded() {

	}
}
