package japicmp.model;

import japicmp.util.Optional;
import japicmp.util.OptionalHelper;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

public class JApiType {
	private final Optional<String> oldTypeOptional;
	private final Optional<String> newTypeOptional;
	private final JApiChangeStatus changeStatus;

	public JApiType(Optional<String> oldTypeOptional, Optional<String> newTypeOptional, JApiChangeStatus changeStatus) {
		this.oldTypeOptional = oldTypeOptional;
		this.newTypeOptional = newTypeOptional;
		this.changeStatus = changeStatus;
	}

	@XmlTransient
	public Optional<String> getOldTypeOptional() {
		return oldTypeOptional;
	}

	@XmlTransient
	public Optional<String> getNewTypeOptional() {
		return newTypeOptional;
	}

	@XmlAttribute(name = "changeStatus")
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}

	@XmlAttribute(name = "oldValue")
	public String getOldValue() {
		return OptionalHelper.optionalToString(this.oldTypeOptional);
	}

	@XmlAttribute(name = "newValue")
	public String getNewValue() {
		return OptionalHelper.optionalToString(this.newTypeOptional);
	}

	public boolean hasChanged() {
		boolean hasChanged = false;
		if (oldTypeOptional.isPresent() && newTypeOptional.isPresent()) {
			if (!oldTypeOptional.get().equals(newTypeOptional.get())) {
				hasChanged = true;
			}
		}
		return hasChanged;
	}
}
