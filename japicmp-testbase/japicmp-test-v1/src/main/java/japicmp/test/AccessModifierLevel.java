package japicmp.test;

public class AccessModifierLevel {

	public static class AccessModifierChangesBelowPublic {
		private int fieldFromPrivateToPackageProtected;
		int fieldFromPackageProtectedToPrivate;

		void methodFromPackageProtectedToPrivate() {

		}

		private void methodFromPrivateToPackageProtected() {

		}
	}

	public static class AccessModifierChangesFromPrivateToPublic {
		private int fieldFromPrivateToPublic;

		private void methodFromPrivateToPublic() {

		}
	}

	public static class AccessModifierChangesFromPublicToPrivate {
		public int fieldFromPublicToPrivate;

		public void methodFromPublicToPrivate() {

		}
	}

	public static class AccessModifierNotChangesForPrivate {
		private int fieldRemainsPrivate;
		int fieldRemainsPackageProtected;
		protected int fieldRemainsProtected;
		public int fieldRemainsPublic;
	}
}
