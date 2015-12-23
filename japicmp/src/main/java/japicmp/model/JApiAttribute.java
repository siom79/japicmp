package japicmp.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import com.google.common.base.Optional;
import japicmp.util.OptionalHelper;

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
