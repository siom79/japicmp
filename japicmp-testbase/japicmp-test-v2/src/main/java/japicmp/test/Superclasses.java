package japicmp.test;

public class Superclasses {

	public static class SuperclassA {
		public void method() {

		}
	}

	public static class SuperclassB {
		public void method() {

		}
	}

	public static class SuperclassC {
		public void method() {

		}
	}

	public static class SuperclassToNoSuperclass {

	}

	public static class NoSuperclassToSuperclass extends SuperclassA {

	}

	public static class SuperclassRemainsSuperclass extends SuperclassA {

	}

	public static class NoSuperclassRemainsNoSuperclass {

	}

	public static class SuperClassChanges extends SuperclassB {

	}

	public static class AddedWithSuperclass extends SuperclassA {

	}

	public static class SuperclassToNewSuperclass extends SuperclassC {

	}
}
