package japicmp.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

public class JApiGenericType {
	private final String type;
	private List<JApiGenericType> genericTypes;
	private final JApiGenericWildCard genericWildCard;

	public enum JApiGenericWildCard {
		NONE, EXTENDS, SUPER, UNBOUNDED
	}

	public JApiGenericType(String type, JApiGenericWildCard genericWildCard) {
		this.type = type;
		this.genericWildCard = genericWildCard;
	}

	@XmlAttribute(name = "type")
	public String getType() {
		return type;
	}

	@XmlAttribute(name = "genericWildCard")
	public JApiGenericWildCard getGenericWildCard() {
		return genericWildCard;
	}

	@XmlElementWrapper(name = "genericTypes")
	@XmlElement(name = "genericType")
	public List<JApiGenericType> getGenericTypes() {
		if (genericTypes == null) {
			genericTypes = new ArrayList<>();
		}
		return genericTypes;
	}

	@Override
	public String toString() {
		if (this.genericWildCard == JApiGenericWildCard.NONE) {
			return this.type;
		} else if (this.genericWildCard == JApiGenericWildCard.UNBOUNDED) {
			return "?";
		} else if (this.genericWildCard == JApiGenericWildCard.EXTENDS) {
			return "? extends " + this.type;
		} else if (this.genericWildCard == JApiGenericWildCard.SUPER) {
			return "? super " + this.type;
		} else {
			return this.type;
		}
	}
}
