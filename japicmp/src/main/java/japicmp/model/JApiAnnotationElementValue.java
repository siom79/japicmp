package japicmp.model;

import javassist.bytecode.annotation.MemberValue;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

public class JApiAnnotationElementValue {
    private final Type type;
    private final Object value;

    public enum Type {
        Double, Char, Long, Integer, Float, Byte, Enum, Annotation, Class, Short, Boolean, UnsupportedType, Array, String
    }

    public JApiAnnotationElementValue(Type type, Object value) {
        this.type = type;
        this.value = value;
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
    public String getStringValue() {
        List<JApiAnnotationElementValue> values = getValues();
        return getString(values);
    }

    private String getString(List<JApiAnnotationElementValue> values) {
        StringBuilder sb = new StringBuilder();
        for(JApiAnnotationElementValue value : values) {
            if(sb.length() > 0) {
                sb.append(",");
            }
            if(value.getType() != Type.Array) {
                sb.append(value.getValue());
            } else {
                sb.append(getString(value.getValues()));
            }
        }
        return sb.toString();
    }

    @XmlTransient
    public List<JApiAnnotationElementValue> getValues() {
        List<JApiAnnotationElementValue> values = new ArrayList<>();
        if (type == Type.Array) {
            if (value instanceof MemberValue[]) {
                MemberValue[] memberValues = (MemberValue[]) value;
                for (MemberValue memberValue : memberValues) {
                    values.add(JApiAnnotationElement.getMemberValue(memberValue));
                }
            } else {
                values.add(this);
            }
        } else {
            values.add(this);
        }
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JApiAnnotationElementValue that = (JApiAnnotationElementValue) o;

        if (type != that.type) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }
}
