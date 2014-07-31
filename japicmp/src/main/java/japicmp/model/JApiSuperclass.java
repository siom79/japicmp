package japicmp.model;

import javax.xml.bind.annotation.XmlElement;

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

	@XmlElement
	public Optional<String> getOldSuperclass() {
		return oldSuperclass;
	}

	@XmlElement
	public Optional<String> getNewSuperclass() {
		return newSuperclass;
	}

	@XmlElement
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}
}
