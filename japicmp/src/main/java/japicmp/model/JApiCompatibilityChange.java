package japicmp.model;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.Objects;

public class JApiCompatibilityChange {
	private final JApiCompatibilityChangeType type;
	private boolean binaryCompatible;
	private boolean sourceCompatible;

	public JApiCompatibilityChange(JApiCompatibilityChangeType type) {
		this.type = type;
		this.binaryCompatible = type.isBinaryCompatible();
		this.sourceCompatible = type.isSourceCompatible();
	}

	@XmlAttribute(name = "type")
	public JApiCompatibilityChangeType getType() {
		return type;
	}

	@XmlAttribute(name = "binaryCompatible")
	public boolean isBinaryCompatible() {
		return binaryCompatible;
	}

	@XmlAttribute(name = "sourceCompatible")
	public boolean isSourceCompatible() {
		return sourceCompatible;
	}

	public void setBinaryCompatible(boolean binaryCompatible) {
		this.binaryCompatible = binaryCompatible;
	}

	public void setSourceCompatible(boolean sourceCompatible) {
		this.sourceCompatible = sourceCompatible;
	}

	public JApiSemanticVersionLevel getSemanticVersionLevel() {
		return type.getSemanticVersionLevel();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		JApiCompatibilityChange that = (JApiCompatibilityChange) o;
		return type == that.type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type);
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("JApiCompatibilityChange{");
		sb.append("type=").append(type);
		sb.append('}');
		return sb.toString();
	}
}
