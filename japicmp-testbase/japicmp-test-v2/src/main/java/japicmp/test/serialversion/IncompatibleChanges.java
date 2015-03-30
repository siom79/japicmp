package japicmp.test.serialversion;

import java.io.*;

public class IncompatibleChanges {

	public static class FieldRemoved implements Serializable {

	}

	public static class NonStaticFieldToStaticField implements Serializable {
		public static int fieldNonStaticToStatic;
	}

	public static class NonTransientFieldToTransientField implements Serializable {
		public transient int fieldNonTransientToTransient;
	}

	public static class TypeOfFieldChanges implements Serializable {
		public Integer fieldFromStringToInteger;
	}

	public static class SerializableToExternalizable implements Externalizable {

		@Override
		public void writeExternal(ObjectOutput out) throws IOException {

		}

		@Override
		public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

		}
	}

	public static class ExternalizableToSerializable implements Serializable {

		public void writeExternal(ObjectOutput out) throws IOException {

		}

		public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

		}
	}

	public static enum NonEnumToEnum implements Serializable {

	}

	public class EnumToNonEnum implements Serializable {

	}

	public static class SerializableRemoved implements Serializable {

	}

	public static class ExternalizableRemoved {

		public void writeExternal(ObjectOutput out) throws IOException {
			
		}

		public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

		}
	}
}
