package japicmp.test.serialversion;

import java.io.Serializable;

public class CompatibleChanges {

	public static class FieldAddedClassWithoutSerialVersionUid implements Serializable {
		private int field = 42;
	}

	public static class FieldAddedClassWithSerialVersionUid implements Serializable {
		private static final long serialVersionUID = 1;
		private int field = 42;
	}

	public static class ClassAdded implements Serializable {

	}

	public static class AddedSerializable implements Serializable {

	}

	public static class FieldAccessChangeFromPublicToPrivate implements Serializable {
		private static final long serialVersionUID = 1;
		private int field = 42;
	}

	public static class FieldChangesFromStaticToNonStatic implements Serializable {
		private static final long serialVersionUID = 1;
		public int field = 42;
	}

	public static class FieldChangesFromTransientToNonTransient implements Serializable {
		private static final long serialVersionUID = 1;
		public int field = 42;
	}
}
