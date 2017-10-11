package japicmp.model;

import japicmp.util.Optional;
import japicmp.util.OptionalHelper;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

/**
 * This class represents changes regarding the Java Object Serialization Specification
 * (see http://docs.oracle.com/javase/7/docs/platform/serialization/spec/serialTOC.html).
 */
public class JApiSerialVersionUid {
	private final boolean serializableOld;
	private final boolean serializableNew;
	private final Optional<Long> serialVersionUidDefaultOld;
	private final Optional<Long> serialVersionUidDefaultNew;
	private final Optional<Long> serialVersionUidInClassOld;
	private final Optional<Long> serialVersionUidInClassNew;

	public JApiSerialVersionUid(boolean serializableOld, boolean serializableNew, Optional<Long> serialVersionUidDefaultOld, Optional<Long> serialVersionUidDefaultNew, Optional<Long> serialVersionUidInClassOld, Optional<Long> serialVersionUidInClassNew) {
		this.serializableOld = serializableOld;
		this.serializableNew = serializableNew;
		this.serialVersionUidDefaultOld = serialVersionUidDefaultOld;
		this.serialVersionUidDefaultNew = serialVersionUidDefaultNew;
		this.serialVersionUidInClassOld = serialVersionUidInClassOld;
		this.serialVersionUidInClassNew = serialVersionUidInClassNew;
	}

	/**
	 * Returns true if the old version of the class is serializable.
	 *
	 * @return if the old version of the class is serializable
	 */
	@XmlAttribute
	public boolean isSerializableOld() {
		return serializableOld;
	}

	/**
	 * Returns true if the new version of the class is serializable.
	 *
	 * @return if the new version of the class is serializable
	 */
	@XmlAttribute
	public boolean isSerializableNew() {
		return serializableNew;
	}

	/**
	 * Returns the default serialVersionUID value as defined in the Java Object Serialization Specification for the
	 * old version of the class.
	 *
	 * @return the default serialVersionUID
	 */
	@XmlTransient
	public Optional<Long> getSerialVersionUidDefaultOld() {
		return serialVersionUidDefaultOld;
	}

	/**
	 * Returns the default serialVersionUID value as defined in the Java Object Serialization Specification for the
	 * new version of the class.
	 *
	 * @return the default serialVersionUID
	 */
	@XmlTransient
	public Optional<Long> getSerialVersionUidDefaultNew() {
		return serialVersionUidDefaultNew;
	}

	/**
	 * Returns the value of the serialVersionUID field in the old class (if present).
	 *
	 * @return the value of the serialVersionUID field in the old class
	 */
	@XmlTransient
	public Optional<Long> getSerialVersionUidInClassOld() {
		return serialVersionUidInClassOld;
	}

	/**
	 * Returns the value of the serialVersionUID field in the new class (if present).
	 *
	 * @return the value of the serialVersionUID field in the new class
	 */
	@XmlTransient
	public Optional<Long> getSerialVersionUidInClassNew() {
		return serialVersionUidInClassNew;
	}

	@XmlAttribute(name = "serialVersionUidInClassOld")
	public String getSerialVersionUidInClassOldAsString() {
		return OptionalHelper.optionalToString(serialVersionUidInClassOld);
	}

	@XmlAttribute(name = "serialVersionUidInClassNew")
	public String getSerialVersionUidInClassNewAsString() {
		return OptionalHelper.optionalToString(serialVersionUidInClassNew);
	}

	@XmlAttribute(name = "serialVersionUidDefaultOld")
	public String getSerialVersionUidDefaultOldAsString() {
		return OptionalHelper.optionalToString(serialVersionUidDefaultOld);
	}

	@XmlAttribute(name = "serialVersionUidDefaultNew")
	public String getSerialVersionUidDefaultNewAsString() {
		return OptionalHelper.optionalToString(serialVersionUidDefaultNew);
	}
}
