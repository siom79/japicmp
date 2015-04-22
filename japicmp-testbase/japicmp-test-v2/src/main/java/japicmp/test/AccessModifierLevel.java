package japicmp.test;

public class AccessModifierLevel {

	public static class AccessModifierChangesBelowPublic {
		int fieldFromPrivateToPackageProtected;
		private int fieldFromPackageProtectedToPrivate;

		private void methodFromPackageProtectedToPrivate() {

		}

		void methodFromPrivateToPackageProtected() {

		}
	}

	public static class AccessModifierChangesFromPrivateToPublic {
		public int fieldFromPrivateToPublic;

		public void methodFromPrivateToPublic() {

		}
	}

	public static class AccessModifierChangesFromPublicToPrivate {
		private int fieldFromPublicToPrivate;

		private void methodFromPublicToPrivate() {

		}
	}

	public static class AccessModifierNotChangesForPrivate {
		private int fieldRemainsPrivate;
		int fieldRemainsPackageProtected;
		protected int fieldRemainsProtected;
		public int fieldRemainsPublic;
	}
}
