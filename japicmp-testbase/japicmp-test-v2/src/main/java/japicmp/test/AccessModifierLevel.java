package japicmp.test;

public class AccessModifierLevel {
	private int fieldFromPrivateToPackageProtected;
	int fieldFromPackageProtectedToPrivate;

	private void methodFromPackageProtectedToPrivate() {

	}

	void methodFromPrivateToPackageProtected() {

	}
}
