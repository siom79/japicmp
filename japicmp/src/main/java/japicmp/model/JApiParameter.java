package japicmp.model;

import javax.xml.bind.annotation.XmlAttribute;

public class JApiParameter {
	private final String type;

	public JApiParameter(String type) {
		this.type = type;
	}

	@XmlAttribute(name = "type")
	public String getType() {
		return type;
	}
}
