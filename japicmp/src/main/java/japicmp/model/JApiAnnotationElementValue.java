package japicmp.model;

import japicmp.util.Optional;
import com.google.common.xml.XmlEscapers;
import japicmp.util.OptionalHelper;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JApiAnnotationElementValue {
	private final String fullyQualifiedName;
	private final Type type;
	private final Object value;
	private Optional<String> name = Optional.absent();

	public enum Type {
		Double, Char, Long, Integer, Float, Byte, Enum, Annotation, Class, Short, Boolean, UnsupportedType, Array, String
	}

	public JApiAnnotationElementValue(Type type, Object value, String fullyQualifiedName) {
		this.type = type;
		this.value = value;
		this.fullyQualifiedName = fullyQualifiedName;
	}

	@XmlTransient
	public Type getType() {
		return type;
	}

	@XmlAttribute(name = "type")
	public String getTypeString() {
		return type.name();
	}

	@XmlTransient
	public Object getValue() {
		return value;
	}

	@XmlAttribute(name = "value")
	public String getValueString() {
		if (type != Type.Array && type != Type.Annotation) {
			return XmlEscapers.xmlAttributeEscaper().escape(value.toString());
		}
		return "n.a.";
	}

	@XmlElementWrapper(name = "values")
	@XmlElement(name = "value")
	public List<JApiAnnotationElementValue> getValues() {
		List<JApiAnnotationElementValue> values = new ArrayList<>();
		if (type == Type.Array) {
			if (value instanceof MemberValue[]) {
				MemberValue[] memberValues = (MemberValue[]) value;
				for (MemberValue memberValue : memberValues) {
					JApiAnnotationElementValue elementValue = JApiAnnotationElement.getMemberValue(memberValue);
					values.add(elementValue);
				}
			}
		} else if (type == Type.Annotation) {
			if (value instanceof Annotation) {
				Annotation annotation = (Annotation) value;
				@SuppressWarnings("unchecked")
				Set<String> memberNames = annotation.getMemberNames();
				if (memberNames != null) {
					for (String memberName : memberNames) {
						MemberValue memberValue = annotation.getMemberValue(memberName);
						JApiAnnotationElementValue elementValue = JApiAnnotationElement.getMemberValue(memberValue);
						elementValue.setName(Optional.of(memberName));
						values.add(elementValue);
					}
				}
			}
		}
		return values;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		JApiAnnotationElementValue that = (JApiAnnotationElementValue) o;

		if (type != that.type) {
			return false;
		}
		if (type == Type.Array || type == Type.Annotation) {
			List<JApiAnnotationElementValue> values = getValues();
			List<JApiAnnotationElementValue> thatValues = that.getValues();
			if (values.size() != thatValues.size()) {
				return false;
			}
			for (int i = 0; i < values.size(); i++) {
				if (!values.get(i).equals(thatValues.get(i))) {
					return false;
				}
			}
		} else {
			if (value != null ? !value.equals(that.value) : that.value != null)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = fullyQualifiedName != null ? fullyQualifiedName.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (value != null ? value.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	@XmlAttribute(name = "fullyQualifiedName")
	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	@XmlTransient
	public Optional<String> getName() {
		return this.name;
	}

	@XmlAttribute(name = "name")
	public String getNameString() {
		return OptionalHelper.optionalToString(this.name);
	}

	public void setName(Optional<String> name) {
		this.name = name;
	}
}
