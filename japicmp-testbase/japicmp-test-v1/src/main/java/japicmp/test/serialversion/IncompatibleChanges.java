package japicmp.test.serialversion;

import java.io.*;

public class IncompatibleChanges {

	public static class FieldRemoved implements Serializable {
		public int fieldRemoved;
	}

	public static class NonStaticFieldToStaticField implements Serializable {
		public int fieldNonStaticToStatic;
	}

	public static class NonTransientFieldToTransientField implements Serializable {
		public int fieldNonTransientToTransient;
	}

	public static class TypeOfFieldChanges implements Serializable {
		public String fieldFromStringToInteger;
	}

	public static class SerializableToExternalizable implements Serializable {

	}

	public static class ExternalizableToSerializable implements Externalizable {

		@Override
		public void writeExternal(ObjectOutput out) throws IOException {

		}

		@Override
		public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

		}
	}

	public static class NonEnumToEnum implements Serializable {

	}

	public enum EnumToNonEnum implements Serializable {

	}

	public static class SerializableRemoved implements Serializable {

	}

	public static class ExternalizableRemoved implements Externalizable {

		@Override
		public void writeExternal(ObjectOutput out) throws IOException {

		}

		@Override
		public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

		}
	}
}
