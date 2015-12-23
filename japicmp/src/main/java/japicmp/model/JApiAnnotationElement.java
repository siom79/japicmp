package japicmp.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import com.google.common.base.Optional;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import java.util.ArrayList;
import java.util.List;

public class JApiAnnotationElement implements JApiHasChangeStatus, JApiBinaryCompatibility {
	private final String name;
	private final Optional<MemberValue> oldValue;
	private final Optional<MemberValue> newValue;
	private final JApiChangeStatus changeStatus;

	public JApiAnnotationElement(String name, Optional<MemberValue> oldValue, Optional<MemberValue> newValue, JApiChangeStatus changeStatus) {
		this.name = name;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.changeStatus = evaluateChangeStatus(changeStatus);
	}

	private JApiChangeStatus evaluateChangeStatus(JApiChangeStatus changeStatus) {
		if (changeStatus == JApiChangeStatus.UNCHANGED) {
			if (oldValue.isPresent() && newValue.isPresent()) {
				MemberValue memberValueOld = oldValue.get();
				MemberValue memberValueNew = newValue.get();
				if (!getMemberValue(memberValueOld).equals(getMemberValue(memberValueNew))) {
					changeStatus = JApiChangeStatus.MODIFIED;
				}
			} else if (!oldValue.isPresent() && newValue.isPresent()) {
				changeStatus = JApiChangeStatus.NEW;
			} else if (oldValue.isPresent() && !newValue.isPresent()) {
				changeStatus = JApiChangeStatus.REMOVED;
			}
		}
		return changeStatus;
	}

	static JApiAnnotationElementValue getMemberValue(MemberValue memberValue) {
		if (memberValue instanceof DoubleMemberValue) {
			DoubleMemberValue value = (DoubleMemberValue) memberValue;
			return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Double, value.getValue(), Double.class.getName());
		} else if (memberValue instanceof CharMemberValue) {
			CharMemberValue value = (CharMemberValue) memberValue;
			return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Char, value.getValue(), Character.class.getName());
		} else if (memberValue instanceof LongMemberValue) {
			LongMemberValue value = (LongMemberValue) memberValue;
			return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Long, value.getValue(), Long.class.getName());
		} else if (memberValue instanceof IntegerMemberValue) {
			IntegerMemberValue value = (IntegerMemberValue) memberValue;
			return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Integer, value.getValue(), Integer.class.getName());
		} else if (memberValue instanceof FloatMemberValue) {
			FloatMemberValue value = (FloatMemberValue) memberValue;
			return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Float, value.getValue(), Float.class.getName());
		} else if (memberValue instanceof AnnotationMemberValue) {
			AnnotationMemberValue value = (AnnotationMemberValue) memberValue;
			Annotation annotation = value.getValue();
			return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Annotation, annotation, annotation.getTypeName());
		} else if (memberValue instanceof ClassMemberValue) {
			ClassMemberValue value = (ClassMemberValue) memberValue;
			return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Class, value.getValue(), value.getValue());
		} else if (memberValue instanceof ByteMemberValue) {
			ByteMemberValue value = (ByteMemberValue) memberValue;
			return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Byte, value.getValue(), Byte.class.getName());
		} else if (memberValue instanceof EnumMemberValue) {
			EnumMemberValue value = (EnumMemberValue) memberValue;
			String type = value.getType();
			return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Enum, value.getValue(), type);
		} else if (memberValue instanceof ArrayMemberValue) {
			ArrayMemberValue value = (ArrayMemberValue) memberValue;
			return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Array, value.getValue(), "[]");
		} else if (memberValue instanceof ShortMemberValue) {
			ShortMemberValue value = (ShortMemberValue) memberValue;
			return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Short, value.getValue(), Short.class.getName());
		} else if (memberValue instanceof BooleanMemberValue) {
			BooleanMemberValue value = (BooleanMemberValue) memberValue;
			return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Boolean, value.getValue(), Boolean.class.getName());
		} else if (memberValue instanceof StringMemberValue) {
			StringMemberValue value = (StringMemberValue) memberValue;
			return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.String, value.getValue(), String.class.getName());
		} else {
			return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.UnsupportedType, "n.a.", "n.a.");
		}
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	@XmlTransient
	public Optional<MemberValue> getOldValue() {
		return oldValue;
	}

	@XmlTransient
	public Optional<MemberValue> getNewValue() {
		return newValue;
	}

	@Override
	@XmlAttribute(name = "changeStatus")
	public JApiChangeStatus getChangeStatus() {
		return this.changeStatus;
	}

	@XmlElementWrapper(name = "oldElementValues")
	@XmlElement(name = "oldElementValue")
	public List<JApiAnnotationElementValue> getOldElementValues() {
		List<JApiAnnotationElementValue> values = new ArrayList<>();
		if (this.oldValue.isPresent()) {
			JApiAnnotationElementValue memberValue = getMemberValue(this.oldValue.get());
			if (memberValue.getType() == JApiAnnotationElementValue.Type.Array) {
				List<JApiAnnotationElementValue> multiValues = memberValue.getValues();
				for (JApiAnnotationElementValue multiValue : multiValues) {
					values.add(multiValue);
				}
			} else {
				values.add(memberValue);
			}
		}
		return values;
	}

	@XmlElementWrapper(name = "newElementValues")
	@XmlElement(name = "newElementValue")
	public List<JApiAnnotationElementValue> getNewElementValues() {
		List<JApiAnnotationElementValue> values = new ArrayList<>();
		if (this.newValue.isPresent()) {
			JApiAnnotationElementValue memberValue = getMemberValue(this.newValue.get());
			if (memberValue.getType() == JApiAnnotationElementValue.Type.Array) {
				List<JApiAnnotationElementValue> multiValues = memberValue.getValues();
				for (JApiAnnotationElementValue multiValue : multiValues) {
					values.add(multiValue);
				}
			} else {
				values.add(memberValue);
			}
		}
		return values;
	}

	@Override
	public boolean isBinaryCompatible() {
		return true;
	}
}
