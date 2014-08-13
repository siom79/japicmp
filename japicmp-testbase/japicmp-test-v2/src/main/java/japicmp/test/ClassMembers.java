package japicmp.test;

public class ClassMembers {

	public class ClassLosesField {

	}

	public class ClassLosesMethod {

	}

	public class ClassLosesConstructor {

	}

	public class ClassFieldChangesAccessibility {
		int field;
	}

	public class ClassMethodChangesAccessibility {
		void method() {

		}
	}

	public class ClassConstructorChangesAccessibility {
		ClassConstructorChangesAccessibility() {

		}
	}

	public class SuperclassWithField {
		public int field;
	}

	public class SubclassExtendsSuperclassWithField extends SuperclassWithField {
		protected int field;
	}

	public class SuperclassWithMethod {

		public void method(int i, String s) {

		}
	}

	public class SubclassWithMethodToBeRemovedButContainedInSuperclass extends SuperclassWithMethod {

	}
}
