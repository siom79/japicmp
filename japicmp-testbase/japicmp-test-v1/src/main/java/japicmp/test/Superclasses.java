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

	public static class SuperclassToNoSuperclass extends SuperclassA {

	}

	public static class NoSuperclassToSuperclass {

	}

	public static class SuperclassRemainsSuperclass extends SuperclassA {

	}

	public static class NoSuperclassRemainsNoSuperclass {

	}

	public static class SuperClassChanges extends SuperclassA {

	}

	public static class RemovedWithSuperclass extends SuperclassA {

	}

	public static class SuperclassToNewSuperclass extends SuperclassA {

	}
}
