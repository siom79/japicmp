package japicmp.model;

import com.google.common.base.Optional;
import japicmp.util.OptionalHelper;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

public class JApiSerialVersionUid {
	private final boolean serializableOld;
	private final boolean serializableNew;
	private final long serialVersionUidDefaultOld;
	private final long serialVersionUidDefaultNew;
	private final Optional<Long> serialVersionUidInClassOld;
	private final Optional<Long> serialVersionUidInClassNew;

	public JApiSerialVersionUid(boolean serializableOld, boolean serializableNew, long serialVersionUidDefaultOld, long serialVersionUidDefaultNew, Optional<Long> serialVersionUidInClassOld, Optional<Long> serialVersionUidInClassNew) {
		this.serializableOld = serializableOld;
		this.serializableNew = serializableNew;
		this.serialVersionUidDefaultOld = serialVersionUidDefaultOld;
		this.serialVersionUidDefaultNew = serialVersionUidDefaultNew;
		this.serialVersionUidInClassOld = serialVersionUidInClassOld;
		this.serialVersionUidInClassNew = serialVersionUidInClassNew;
	}

	@XmlAttribute
	public boolean isSerializableOld() {
		return serializableOld;
	}

	@XmlAttribute
	public boolean isSerializableNew() {
		return serializableNew;
	}

	@XmlAttribute
	public long getSerialVersionUidDefaultOld() {
		return serialVersionUidDefaultOld;
	}

	@XmlAttribute
	public long getSerialVersionUidDefaultNew() {
		return serialVersionUidDefaultNew;
	}

	@XmlTransient
	public Optional<Long> getSerialVersionUidInClassOld() {
		return serialVersionUidInClassOld;
	}

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
}
