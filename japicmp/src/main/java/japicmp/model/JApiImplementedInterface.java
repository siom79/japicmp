package japicmp.model;

import javax.xml.bind.annotation.XmlAttribute;

public class JApiImplementedInterface implements JApiHasChangeStatus {
	private final String fullyQualifiedName;
	private final JApiChangeStatus changeStatus;

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
}
