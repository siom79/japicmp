package japicmp.model;

/**
 * This interface defines a method to check whether the implementing
 * element has changed in a way that is compatible or incompatible
 * regarding the Java Object Serialization mechanism as defined in
 * http://docs.oracle.com/javase/7/docs/platform/serialization/spec/serialTOC.html.
 */
public interface JApiJavaObjectSerializationCompatibility {

	public enum JApiJavaObjectSerializationChangeStatus {
		/**
		 * The class is not serializable.
		 */
		NOT_SERIALIZABLE,
		/**
		 * The class is serializable and changed in a compatible way.
		 */
		SERIALIZABLE_COMPATIBLE,
		/**
		 * The class is serializable but changed in an incompatible way.
		 */
		SERIALIZABLE_INCOMPATIBLE,
		/**
		 * The class is serializable but changed in an incompatible way
		 * and the serialVersionUID has not been changed.
		 */
		SERIALIZABLE_INCOMPATIBLE_BUT_SUID_EQUAL
	}

	/**
	 * Returns if this class is serializable and if it has changed in a compatible
	 * or incompatible way.
	 * @return the type of change
	 */
	JApiJavaObjectSerializationChangeStatus getJavaObjectSerializationCompatible();

	/**
	 * Sets the {@link JApiSerialVersionUid} for this class.
	 * @param jApiSerialVersionUid the serialVersionUID
	 */
	void setSerialVersionUid(JApiSerialVersionUid jApiSerialVersionUid);

	/**
	 * Returns the {@link japicmp.model.JApiSerialVersionUid}
	 * @return the serialVersionUID
	 */
	JApiSerialVersionUid getSerialVersionUid();
}
