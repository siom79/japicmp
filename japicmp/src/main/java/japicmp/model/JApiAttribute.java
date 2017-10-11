package japicmp.model;

import japicmp.util.Optional;
import japicmp.util.OptionalHelper;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

public class JApiAttribute<T> {
	private final Optional<T> oldAttribute;
	private final Optional<T> newAttribute;
	private final JApiChangeStatus changeStatus;

	public JApiAttribute(JApiChangeStatus changeStatus, Optional<T> oldAttribute, Optional<T> newAttribute) {
		this.changeStatus = changeStatus;
		this.oldAttribute = oldAttribute;
		this.newAttribute = newAttribute;
	}

	@XmlTransient
	public Optional<T> getOldAttribute() {
		return oldAttribute;
	}

	@XmlTransient
	public Optional<T> getNewAttribute() {
		return newAttribute;
	}

	@XmlAttribute(name = "changeStatus")
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}

	@XmlAttribute(name = "oldValue")
	public String getOldValue() {
		return OptionalHelper.optionalToString(this.oldAttribute);
	}

	@XmlAttribute(name = "newValue")
	public String getNewValue() {
		return OptionalHelper.optionalToString(this.newAttribute);
	}
}
