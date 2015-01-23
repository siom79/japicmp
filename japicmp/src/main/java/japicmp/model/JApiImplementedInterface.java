package japicmp.model;

import javax.xml.bind.annotation.XmlAttribute;

public class JApiImplementedInterface implements JApiHasChangeStatus, JApiBinaryCompatibility {
	private final String fullyQualifiedName;
	private final JApiChangeStatus changeStatus;
	private boolean binaryCompatible = true;

	public JApiImplementedInterface(String fullyQualifiedName, JApiChangeStatus changeStatus) {
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
		return this.binaryCompatible;
	}

	void setBinaryCompatible(boolean binaryCompatible) {
		this.binaryCompatible = binaryCompatible;
	}
}
