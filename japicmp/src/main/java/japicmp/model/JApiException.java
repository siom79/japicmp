package japicmp.model;

import javax.xml.bind.annotation.XmlAttribute;

public class JApiException implements JApiHasChangeStatus {
	private final String name;
	private final JApiChangeStatus changeStatus;

	public JApiException(String name, JApiChangeStatus changeStatus) {
		this.name = name;
		this.changeStatus = changeStatus;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	@XmlAttribute(name = "changeStatus")
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}
}
