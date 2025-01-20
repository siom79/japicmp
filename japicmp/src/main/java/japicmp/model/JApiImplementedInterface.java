package japicmp.model;

import javassist.CtClass;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JApiImplementedInterface implements JApiHasChangeStatus, JApiCompatibility {
	private final CtClass ctClass;
	private final String fullyQualifiedName;
	private final JApiChangeStatus changeStatus;
	private final List<JApiCompatibilityChange> compatibilityChanges = new ArrayList<>();
	private Optional<JApiClass> correspondingJApiClass = Optional.empty();

	public JApiImplementedInterface(CtClass ctClass, String fullyQualifiedName, JApiChangeStatus changeStatus) {
		this.ctClass = ctClass;
		this.fullyQualifiedName = fullyQualifiedName;
		this.changeStatus = changeStatus;
	}

	@XmlAttribute
	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	@XmlAttribute
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}

	@Override
	@XmlAttribute
	public boolean isBinaryCompatible() {
		return compatibilityChanges.stream().allMatch(JApiCompatibilityChange::isBinaryCompatible)
			&& correspondingJApiClass.map(JApiClass::isBinaryCompatible).orElse(true);
	}

	@Override
	@XmlAttribute
	public boolean isSourceCompatible() {
		return compatibilityChanges.stream().allMatch(JApiCompatibilityChange::isSourceCompatible)
			&& correspondingJApiClass.map(JApiClass::isSourceCompatible).orElse(true);
	}

	@XmlElementWrapper(name = "compatibilityChanges")
	@XmlElement(name = "compatibilityChange")
	public List<JApiCompatibilityChange> getCompatibilityChanges() {
		return compatibilityChanges;
	}

	public void setJApiClass(JApiClass jApiClass) {
		this.correspondingJApiClass = Optional.of(jApiClass);
	}

	public Optional<JApiClass> getCorrespondingJApiClass() {
		return correspondingJApiClass;
	}

	@XmlTransient
	public CtClass getCtClass() {
		return ctClass;
	}

	public String toString()
	{
		return "JApiImplementedInterface [fullyQualifiedName="
			+ fullyQualifiedName
			+ ", changeStatus="
			+ changeStatus
			+ ", compatibilityChanges="
			+ compatibilityChanges
			+ "]";
	}


}
