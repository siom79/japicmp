package japicmp.model;

import japicmp.util.Optional;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

public class JApiGenericTemplate implements JApiHasChangeStatus, JApiHasGenericTypes, JApiCompatibility {
	private final String name;
	private final Optional<String> oldType;
	private final Optional<String> newType;
	private List<JApiGenericType> oldInterfaceTypes;
	private List<JApiGenericType> newInterfaceTypes;
	private List<JApiGenericType> oldGenericTypes;
	private List<JApiGenericType> newGenericTypes;
	private final JApiChangeStatus changeStatus;
	private final List<JApiCompatibilityChange> compatibilityChanges = new ArrayList<>();

	public JApiGenericTemplate(JApiChangeStatus changeStatus, String name, Optional<String> oldType, Optional<String> newType) {
		this.changeStatus = changeStatus;
		this.name = name;
		this.oldType = oldType;
		this.newType = newType;
	}

	@XmlAttribute(name = "changeStatus")
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	@XmlTransient
	public Optional<String> getOldTypeOptional() {
		return this.oldType;
	}

	@XmlTransient
	public Optional<String> getNewTypeOptional() {
		return this.newType;
	}

	@XmlAttribute(name = "oldType")
	public String getOldType() {
		return this.oldType.or("n.a.");
	}

	@XmlAttribute(name = "newType")
	public String getNewType() {
		return this.newType.or("n.a.");
	}

	@XmlElementWrapper(name = "oldGenericTypes")
	@XmlElement(name = "oldGenericType")
	public List<JApiGenericType> getOldGenericTypes() {
		if (oldGenericTypes == null) {
			oldGenericTypes = new ArrayList<>();
		}
		return oldGenericTypes;
	}

	@XmlElementWrapper(name = "newGenericTypes")
	@XmlElement(name = "newGenericType")
	public List<JApiGenericType> getNewGenericTypes() {
		if (newGenericTypes == null) {
			newGenericTypes = new ArrayList<>();
		}
		return newGenericTypes;
	}

	@Override
	public String toString() {
		return this.name + ":" + this.oldType;
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

	@XmlElementWrapper(name = "oldInterfaceTypes")
	@XmlElement(name = "oldInterfaceType")
	public List<JApiGenericType> getOldInterfaceTypes() {
		if (oldInterfaceTypes == null) {
			oldInterfaceTypes = new ArrayList<>();
		}
		return oldInterfaceTypes;
	}

	@XmlElementWrapper(name = "newInterfaceTypes")
	@XmlElement(name = "newInterfaceType")
	public List<JApiGenericType> getNewInterfaceTypes() {
		if (newInterfaceTypes == null) {
			newInterfaceTypes = new ArrayList<>();
		}
		return newInterfaceTypes;
	}
}
