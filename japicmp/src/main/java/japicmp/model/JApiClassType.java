package japicmp.model;

import japicmp.util.Optional;
import japicmp.util.OptionalHelper;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

public class JApiClassType {
	private final Optional<ClassType> oldTypeOptional;
	private final Optional<ClassType> newTypeOptional;
	private final JApiChangeStatus changeStatus;

	public enum ClassType {
		ANNOTATION, INTERFACE, CLASS, ENUM
	}

	public JApiClassType(Optional<ClassType> oldTypeOptional, Optional<ClassType> newTypeOptional, JApiChangeStatus changeStatus) {
		this.oldTypeOptional = oldTypeOptional;
		this.newTypeOptional = newTypeOptional;
		this.changeStatus = changeStatus;
	}

	@XmlAttribute
	public String getOldType() {
		return OptionalHelper.optionalToString(oldTypeOptional);
	}

	@XmlAttribute
	public String getNewType() {
		return OptionalHelper.optionalToString(newTypeOptional);
	}

	@XmlAttribute
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}

	@XmlTransient
	public Optional<ClassType> getOldTypeOptional() {
		return oldTypeOptional;
	}

	@XmlTransient
	public Optional<ClassType> getNewTypeOptional() {
		return newTypeOptional;
	}
}
