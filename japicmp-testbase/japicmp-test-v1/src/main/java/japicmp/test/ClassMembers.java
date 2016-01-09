package japicmp.test;

public class ClassMembers {

	public class ClassLosesField {
		protected int field;
	}

	public class ClassLosesMethod {

		protected void method() {

		}
	}

	public class ClassLosesConstructor {

		protected ClassLosesConstructor(int value) {

		}
	}

	public class ClassFieldChangesAccessibility {
		public int field;
	}

	public class ClassMethodChangesAccessibility {
		public void method() {

		}
	}

	public class ClassConstructorChangesAccessibility {
		public ClassConstructorChangesAccessibility() {

		}
	}

	public class SuperclassWithMethod {

		public void method(int i, String s) {

		}
	}

	public class SubclassWithMethodToBeRemovedButContainedInSuperclass extends SuperclassWithMethod {

		public void method(int i, String s) {

		}
	}
}
