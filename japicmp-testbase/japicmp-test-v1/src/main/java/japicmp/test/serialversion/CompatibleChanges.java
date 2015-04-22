package japicmp.test.serialversion;

import java.io.Serializable;

public class CompatibleChanges {

	public static class FieldAddedClassWithoutSerialVersionUid implements Serializable {

	}

	public static class FieldAddedClassWithSerialVersionUid implements Serializable {
		private static final long serialVersionUID = 1;
	}

	public static class ClassRemoved implements Serializable {

	}

	public static class AddedSerializable {

	}

	public static class FieldAccessChangeFromPublicToPrivate implements Serializable {
		private static final long serialVersionUID = 1;
		public int field = 42;
	}

	public static class FieldChangesFromStaticToNonStatic implements Serializable {
		private static final long serialVersionUID = 1;
		public static int field = 42;
	}

	public static class FieldChangesFromTransientToNonTransient implements Serializable {
		private static final long serialVersionUID = 1;
		public transient int field = 42;
	}
}
