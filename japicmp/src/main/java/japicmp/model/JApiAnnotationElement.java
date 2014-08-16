package japicmp.model;

import javassist.bytecode.annotation.MemberValue;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Optional;

public class JApiAnnotationElement implements JApiHasChangeStatus {
	private final String name;
	private final Optional<MemberValue> oldValue;
	private final Optional<MemberValue> newValue;
	private final JApiChangeStatus changeStatus;
	
	public JApiAnnotationElement(String name, Optional<MemberValue> oldValue, Optional<MemberValue> newValue, JApiChangeStatus changeStatus) {
		this.name = name;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.changeStatus = changeStatus;
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
}
