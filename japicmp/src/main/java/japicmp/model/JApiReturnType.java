package japicmp.model;

import japicmp.util.Optional;
import japicmp.util.OptionalHelper;

import javax.xml.bind.annotation.XmlAttribute;

public class JApiReturnType {
	private final Optional<String> oldReturnTypeOptional;
	private final Optional<String> newReturnTypeOptional;
	private final JApiChangeStatus changeStatus;

	public JApiReturnType(JApiChangeStatus changeStatus, Optional<String> oldReturnTypeOptional, Optional<String> newReturnTypeOptional) {
		this.changeStatus = changeStatus;
		this.oldReturnTypeOptional = oldReturnTypeOptional;
		this.newReturnTypeOptional = newReturnTypeOptional;
	}

	@XmlAttribute(name = "changeStatus")
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}

	@XmlAttribute(name = "oldValue")
	public String getOldReturnType() {
		return OptionalHelper.optionalToString(oldReturnTypeOptional);
	}

	@XmlAttribute(name = "newValue")
	public String getNewReturnType() {
		return OptionalHelper.optionalToString(newReturnTypeOptional);
	}

	public String toString()
	{
		return "JApiReturnType [oldReturnTypeOptional="
			+ oldReturnTypeOptional
			+ ", newReturnTypeOptional="
			+ newReturnTypeOptional
			+ ", changeStatus="
			+ changeStatus
			+ "]";
	}



}
