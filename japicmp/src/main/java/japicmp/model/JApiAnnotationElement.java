package japicmp.model;

import japicmp.util.OptionalHelper;
import javassist.bytecode.annotation.*;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Optional;

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

    private Object getMemberValue(MemberValue memberValue) {
        if(memberValue instanceof DoubleMemberValue) {
            DoubleMemberValue value = (DoubleMemberValue)memberValue;
            return value.getValue();
        } else if(memberValue instanceof CharMemberValue) {
            CharMemberValue value = (CharMemberValue)memberValue;
            return value.getValue();
        } else if(memberValue instanceof LongMemberValue) {
            LongMemberValue value = (LongMemberValue)memberValue;
            return value.getValue();
        } else if(memberValue instanceof IntegerMemberValue) {
            IntegerMemberValue value = (IntegerMemberValue)memberValue;
            return value.getValue();
        } else if(memberValue instanceof FloatMemberValue) {
            FloatMemberValue value = (FloatMemberValue)memberValue;
            return value.getValue();
        } else if(memberValue instanceof AnnotationMemberValue) {
            AnnotationMemberValue value = (AnnotationMemberValue)memberValue;
            return value.getValue();
        } else if(memberValue instanceof ClassMemberValue) {
            ClassMemberValue value = (ClassMemberValue)memberValue;
            return value.getValue();
        } else if(memberValue instanceof ByteMemberValue) {
            ByteMemberValue value = (ByteMemberValue)memberValue;
            return value.getValue();
        } else if(memberValue instanceof EnumMemberValue) {
            EnumMemberValue value = (EnumMemberValue)memberValue;
            return value.getValue();
        } else if(memberValue instanceof ArrayMemberValue) {
            ArrayMemberValue value = (ArrayMemberValue)memberValue;
            return value.getValue();
        } else if(memberValue instanceof ShortMemberValue) {
            ShortMemberValue value = (ShortMemberValue)memberValue;
            return value.getValue();
        } else if(memberValue instanceof BooleanMemberValue) {
            BooleanMemberValue value = (BooleanMemberValue)memberValue;
            return value.getValue();
        } else if(memberValue instanceof StringMemberValue) {
            StringMemberValue value = (StringMemberValue)memberValue;
            return value.getValue();
        } else {
            return "n.a.";
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

    @XmlAttribute(name = "oldValue")
    public String getValueOld() {
        if(this.oldValue.isPresent()) {
            return getMemberValue(this.oldValue.get()).toString();
        }
        return "n.a.";
    }

    @XmlAttribute(name = "newValue")
    public String getValueNew() {
        if(this.newValue.isPresent()) {
            return getMemberValue(this.newValue.get()).toString();
        }
        return "n.a.";
    }

    @Override
    public boolean isBinaryCompatible() {
        return true;
    }
}
