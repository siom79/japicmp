package japicmp.model;

import japicmp.util.Optional;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JApiParameter implements JApiHasGenericTypes, JApiHasChangeStatus, JApiCompatibility {
	private String type;
	private Optional<String> templateName;
	private final List<JApiGenericType> oldGenericTypes = new ArrayList<>();
	private final List<JApiGenericType> newGenericTypes = new ArrayList<>();
	private final List<JApiCompatibilityChange> compatibilityChanges = new ArrayList<>();
	private final JApiChangeStatus changeStatus = JApiChangeStatus.UNCHANGED;

	public JApiParameter(String type, Optional<String> templateName) {
		this.type = type;
		this.templateName = templateName;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setTemplateName(Optional<String> templateName) {
		this.templateName = templateName;
	}

	@XmlAttribute(name = "changeStatus")
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}

	@XmlAttribute(name = "type")
	public String getType() {
		return type;
	}

	@XmlAttribute(name = "templateName")
	public String getTemplateName() {
		return templateName.or("N/A");
	}

	public Optional<String> getTemplateNameOptional() {
		return templateName;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		JApiParameter that = (JApiParameter) o;
		return Objects.equals(type, that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type);
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
