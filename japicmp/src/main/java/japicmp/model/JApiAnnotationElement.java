package japicmp.model;

import japicmp.util.OptionalHelper;
import javassist.bytecode.annotation.*;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Arrays;
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
            if(oldValue.isPresent() && newValue.isPresent()) {
                MemberValue memberValueOld = oldValue.get();
                MemberValue memberValueNew = newValue.get();
                if(!getMemberValue(memberValueOld).equals(getMemberValue(memberValueNew))) {
                    changeStatus = JApiChangeStatus.MODIFIED;
                }
            } else if(!oldValue.isPresent() && newValue.isPresent()) {
                changeStatus = JApiChangeStatus.NEW;
            } else if(oldValue.isPresent() && !newValue.isPresent()) {
                changeStatus = JApiChangeStatus.REMOVED;
            }
        }
        return changeStatus;
    }

    static JApiAnnotationElementValue getMemberValue(MemberValue memberValue) {
        if(memberValue instanceof DoubleMemberValue) {
            DoubleMemberValue value = (DoubleMemberValue)memberValue;
            return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Double, value.getValue());
        } else if(memberValue instanceof CharMemberValue) {
            CharMemberValue value = (CharMemberValue)memberValue;
            return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Char, value.getValue());
        } else if(memberValue instanceof LongMemberValue) {
            LongMemberValue value = (LongMemberValue)memberValue;
            return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Long, value.getValue());
        } else if(memberValue instanceof IntegerMemberValue) {
            IntegerMemberValue value = (IntegerMemberValue)memberValue;
            return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Integer, value.getValue());
        } else if(memberValue instanceof FloatMemberValue) {
            FloatMemberValue value = (FloatMemberValue)memberValue;
            return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Float, value.getValue());
        } else if(memberValue instanceof AnnotationMemberValue) {
            AnnotationMemberValue value = (AnnotationMemberValue)memberValue;
            return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Annotation, value.getValue());
        } else if(memberValue instanceof ClassMemberValue) {
            ClassMemberValue value = (ClassMemberValue)memberValue;
            return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Class, value.getValue());
        } else if(memberValue instanceof ByteMemberValue) {
            ByteMemberValue value = (ByteMemberValue)memberValue;
            return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Byte, value.getValue());
        } else if(memberValue instanceof EnumMemberValue) {
            EnumMemberValue value = (EnumMemberValue)memberValue;
            return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Enum, value.getValue());
        } else if(memberValue instanceof ArrayMemberValue) {
            ArrayMemberValue value = (ArrayMemberValue)memberValue;
            return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Array, value.getValue());
        } else if(memberValue instanceof ShortMemberValue) {
            ShortMemberValue value = (ShortMemberValue)memberValue;
            return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Short, value.getValue());
        } else if(memberValue instanceof BooleanMemberValue) {
            BooleanMemberValue value = (BooleanMemberValue)memberValue;
            return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.Boolean, value.getValue());
        } else if(memberValue instanceof StringMemberValue) {
            StringMemberValue value = (StringMemberValue)memberValue;
            return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.String, value.getValue());
        } else {
            return new JApiAnnotationElementValue(JApiAnnotationElementValue.Type.UnsupportedType, "n.a.");
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
    public List<JApiAnnotationElementValue> getValueOld() {
        if(this.oldValue.isPresent()) {
            JApiAnnotationElementValue memberValue = getMemberValue(this.oldValue.get());
            if(memberValue.getType() == JApiAnnotationElementValue.Type.Array) {
                return memberValue.getValues();
            } else {
                return Arrays.asList(memberValue);
            }
        }
        return new ArrayList<>();
    }

    @XmlElementWrapper(name = "newElementValues")
    @XmlElement(name = "newElementValue")
    public List<JApiAnnotationElementValue> getValueNew() {
        if(this.newValue.isPresent()) {
            JApiAnnotationElementValue memberValue = getMemberValue(this.newValue.get());
            if(memberValue.getType() == JApiAnnotationElementValue.Type.Array) {
                return memberValue.getValues();
            } else {
                return Arrays.asList(memberValue);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public boolean isBinaryCompatible() {
        return true;
    }
}
