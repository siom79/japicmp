package japicmp.model;

import japicmp.util.OptionalHelper;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Optional;

public class JApiType implements JApiHasChangeStatus {
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

	@Override
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
		if (oldTypeOptional.isPresent() && newTypeOptional.isPresent()) {
			return !oldTypeOptional.equals(newTypeOptional);
		}
		return false;
	}
}
