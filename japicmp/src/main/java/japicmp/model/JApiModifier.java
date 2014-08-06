package japicmp.model;

import japicmp.util.OptionalHelper;

import javax.xml.bind.annotation.XmlAttribute;

import com.google.common.base.Optional;

public class JApiModifier<T> {
	private final Optional<T> oldModifier;
	private final Optional<T> newModifier;
	private final JApiChangeStatus changeStatus;
	
	public JApiModifier(Optional<T> oldModifier, Optional<T> newModifier, JApiChangeStatus changeStatus) {
		this.oldModifier = oldModifier;
		this.newModifier = newModifier;
		this.changeStatus = changeStatus;
	}

	public Optional<T> getOldModifier() {
		return oldModifier;
	}

	public Optional<T> getNewModifier() {
		return newModifier;
	}

	@XmlAttribute(name = "changeStatus")
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}
	
	@XmlAttribute(name = "oldValue")
	public String getValueOld() {
		return OptionalHelper.optionalToString(this.oldModifier);
	}
	
	@XmlAttribute(name = "newValue")
	public String getValueNew() {
		return OptionalHelper.optionalToString(this.newModifier);
	}
}
