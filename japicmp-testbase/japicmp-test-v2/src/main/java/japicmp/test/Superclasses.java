package japicmp.test;

public class Superclasses {

	public static class SuperclassA {

	}

	public static class SuperclassB {

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
}
