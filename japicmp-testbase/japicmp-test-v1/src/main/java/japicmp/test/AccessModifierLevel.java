package japicmp.test;

public class AccessModifierLevel {
	private int fieldFromPrivateToPackageProtected;
	int fieldFromPackageProtectedToPrivate;

	void methodFromPackageProtectedToPrivate() {

	}

	private void methodFromPrivateToPackageProtected() {

	}
}
