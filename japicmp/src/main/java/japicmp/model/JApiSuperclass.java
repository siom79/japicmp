package japicmp.model;

import japicmp.util.OptionalUtil;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Optional;

public class JApiSuperclass {
	private final Optional<String> oldSuperclass;
	private final Optional<String> newSuperclass;
	private final JApiChangeStatus changeStatus;
	
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
		return OptionalUtil.optionalToString(this.oldSuperclass);
	}
	
	@XmlAttribute(name = "superclassNew")
	public String getSuperclassNew() {
		return OptionalUtil.optionalToString(this.newSuperclass);
	}
}
