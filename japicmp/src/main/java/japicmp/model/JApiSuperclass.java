package japicmp.model;

import japicmp.util.OptionalHelper;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Optional;

public class JApiSuperclass implements JApiHasChangeStatus, JApiBinaryCompatibility {
	private final Optional<String> oldSuperclass;
	private final Optional<String> newSuperclass;
	private final JApiChangeStatus changeStatus;
	private boolean binaryCompatible = true;
	
	public JApiSuperclass(Optional<String> oldSuperclass, Optional<String> newSuperclass, JApiChangeStatus changeStatus) {
		this.oldSuperclass = oldSuperclass;
		this.newSuperclass = newSuperclass;
		this.changeStatus = changeStatus;
	}

	@XmlTransient
	public Optional<String> getOldSuperclass() {
		return oldSuperclass;
	}

	@XmlTransient
	public Optional<String> getNewSuperclass() {
		return newSuperclass;
	}

	@XmlAttribute(name = "changeStatus")
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}
	
	@XmlAttribute(name = "superclassOld")
	public String getSuperclassOld() {
		return OptionalHelper.optionalToString(this.oldSuperclass);
	}
	
	@XmlAttribute(name = "superclassNew")
	public String getSuperclassNew() {
		return OptionalHelper.optionalToString(this.newSuperclass);
	}
	
	@Override
	@XmlAttribute
	public boolean isBinaryCompatible() {
		return this.binaryCompatible;
	}

	void setBinaryCompatible(boolean binaryCompatible) {
		this.binaryCompatible = binaryCompatible;
	}
}
