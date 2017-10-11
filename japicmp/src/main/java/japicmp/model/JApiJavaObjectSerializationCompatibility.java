package japicmp.model;

/**
 * This interface defines a method to check whether the implementing
 * element has changed in a way that is compatible or incompatible
 * regarding the Java Object Serialization mechanism as defined in
 * http://docs.oracle.com/javase/7/docs/platform/serialization/spec/serialTOC.html.
 */
public interface JApiJavaObjectSerializationCompatibility {

	enum JApiJavaObjectSerializationChangeStatus {
		/**
		 * The class is not serializable.
		 */
		NOT_SERIALIZABLE("not serializable"),
		/**
		 * The class is serializable and changed in a compatible way.
		 */
		SERIALIZABLE_COMPATIBLE("compatible"),
		/**
		 * The serialVersionUID of this class has changed.
		 */
		SERIALIZABLE_INCOMPATIBLE_SERIALVERSIONUID_MODIFIED("serialVersionUID modified"),
		/**
		 * The serialVersionUID has been removed from this class but the new default serialVersionUID does not match
		 * the old one.
		 */
		SERIALIZABLE_INCOMPATIBLE_SERIALVERSIONUID_REMOVED_AND_NOT_MATCHES_NEW_DEFAULT("serialVersionUID removed but not matches new default serialVersionUID"),
		/**
		 * The serialVersionUID has been added but does not match the old default serialVersionUID.
		 */
		SERIALIZABLE_INCOMPATIBLE_SERIALVERSIONUID_ADDED_AND_NOT_MATCHES_OLD_DEFAULT("serialVersionUID added but not matches old default serialVersionUID"),
		/**
		 * The type of the class has changed.
		 */
		SERIALIZABLE_INCOMPATIBLE_CLASS_TYPE_MODIFIED("class type modified"),
		/**
		 * The class now implements Externalizable instead of Serializable.
		 */
		SERIALIZABLE_INCOMPATIBLE_CHANGED_FROM_SERIALIZABLE_TO_EXTERNALIZABLE("changed from Serializable to Externalizable"),
		/**
		 * The class now implements Serializable instead of Externalizable.
		 */
		SERIALIZABLE_INCOMPATIBLE_CHANGED_FROM_EXTERNALIZABLE_TO_SERIALIZABLE("changed from Externalizable to Serializable"),
		/**
		 * The class no longer implements Serializable.
		 */
		SERIALIZABLE_INCOMPATIBLE_SERIALIZABLE_REMOVED("Serializable removed"),
		/**
		 * The class no longer implements Externalizable.
		 */
		SERIALIZABLE_INCOMPATIBLE_EXTERNALIZABLE_REMOVED("Externalizable removed"),
		/**
		 * A field has been removed.
		 */
		SERIALIZABLE_INCOMPATIBLE_FIELD_REMOVED("field removed"),
		/**
		 * A field has been changed from nonstatic to static.
		 */
		SERIALIZABLE_INCOMPATIBLE_FIELD_CHANGED_FROM_NONSTATIC_TO_STATIC("field changed from nonstatic to static"),
		/**
		 * A field has been changed from nontransient to transient.
		 */
		SERIALIZABLE_INCOMPATIBLE_FIELD_CHANGED_FROM_NONTRANSIENT_TO_TRANSIENT("field changed from nontransient to transient"),
		/**
		 * The type of a field has been changed.
		 */
		SERIALIZABLE_INCOMPATIBLE_FIELD_TYPE_MODIFIED("type of field has changed"),
		/**
		 * The class is serializable but changed in an incompatible way
		 * and the serialVersionUID has not been changed.
		 */
		SERIALIZABLE_INCOMPATIBLE_BUT_SUID_EQUAL("incompatible but serialVersionUID equal"),
		/**
		 * The class has been removed.
		 */
		SERIALIZABLE_INCOMPATIBLE_CLASS_REMOVED("class removed"),
		/**
		 * The default serialVersionUID has changed.
		 */
		SERIALIZABLE_INCOMPATIBLE_DEFAULT_SERIALVERSIONUID_CHANGED("default serialVersionUID changed"),
		/**
		 * The superclass has changed.
		 */
		SERIALIZABLE_INCOMPATIBLE_SUPERCLASS_MODIFIED("superclass modified");

		private final String description;

		JApiJavaObjectSerializationChangeStatus(String description) {
			this.description = description;
		}

		public boolean isIncompatible() {
			return !(this == NOT_SERIALIZABLE || this == SERIALIZABLE_COMPATIBLE);
		}

		public String getDescription() {
			return description;
		}
	}

	/**
	 * Returns if this class is serializable and if it has changed in a compatible
	 * or incompatible way.
	 *
	 * @return the type of change
	 */
	JApiJavaObjectSerializationChangeStatus getJavaObjectSerializationCompatible();

	/**
	 * Returns the {@link japicmp.model.JApiSerialVersionUid}
	 *
	 * @return the serialVersionUID
	 */
	JApiSerialVersionUid getSerialVersionUid();
}
