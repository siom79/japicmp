package japicmp.model;

import japicmp.util.OptionalHelper;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JApiReturnType implements JApiHasGenericTypes, JApiHasChangeStatus, JApiCompatibility {
	private final Optional<String> oldReturnTypeOptional;
	private final Optional<String> newReturnTypeOptional;
	private final JApiChangeStatus changeStatus;
	private final List<JApiGenericType> oldGenericTypes = new ArrayList<>();
	private final List<JApiGenericType> newGenericTypes = new ArrayList<>();
	private final List<JApiCompatibilityChange> compatibilityChanges = new ArrayList<>();

	public JApiReturnType(JApiChangeStatus changeStatus, Optional<String> oldReturnTypeOptional, Optional<String> newReturnTypeOptional) {
		this.changeStatus = changeStatus;
		this.oldReturnTypeOptional = oldReturnTypeOptional;
		this.newReturnTypeOptional = newReturnTypeOptional;
	}

	@Override
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

	@XmlElementWrapper(name = "oldGenericTypes")
	@XmlElement(name = "oldGenericType")
	public List<JApiGenericType> getOldGenericTypes() {
		return oldGenericTypes;
	}

	@XmlElementWrapper(name = "newGenericTypes")
	@XmlElement(name = "newGenericType")
	public List<JApiGenericType> getNewGenericTypes() {
		return newGenericTypes;
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

	@Override
	@XmlAttribute
	public boolean isBinaryCompatible() {
		boolean binaryCompatible = true;
		for (JApiCompatibilityChange compatibilityChange : compatibilityChanges) {
			if (!compatibilityChange.isBinaryCompatible()) {
				binaryCompatible = false;
				break;
			}
		}
		return binaryCompatible;
	}

	@Override
	@XmlAttribute
	public boolean isSourceCompatible() {
		boolean sourceCompatible = true;
		for (JApiCompatibilityChange compatibilityChange : compatibilityChanges) {
			if (!compatibilityChange.isSourceCompatible()) {
				sourceCompatible = false;
				break;
			}
		}
		return sourceCompatible;
	}

	@Override
	@XmlElementWrapper(name = "compatibilityChanges")
	@XmlElement(name = "compatibilityChange")
	public List<JApiCompatibilityChange> getCompatibilityChanges() {
		return this.compatibilityChanges;
	}
}
