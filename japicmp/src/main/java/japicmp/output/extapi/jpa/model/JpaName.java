package japicmp.output.extapi.jpa.model;

import javax.xml.bind.annotation.XmlAttribute;
import com.google.common.base.Optional;
import japicmp.model.JApiChangeStatus;
import japicmp.util.OptionalHelper;

public class JpaName {
	private final Optional<String> newName;
	private final Optional<String> oldName;
	private final JApiChangeStatus changeStatus;

	public JpaName(Optional<String> oldName, Optional<String> newName, JApiChangeStatus changeStatus) {
		this.oldName = oldName;
		this.newName = newName;
		this.changeStatus = changeStatus;
	}

	@XmlAttribute(name = "changeStatus")
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}

	@XmlAttribute(name = "newName")
	public String getNewName() {
		return OptionalHelper.optionalToString(this.newName);
	}

	@XmlAttribute(name = "oldName")
	public String getOldName() {
		return OptionalHelper.optionalToString(this.oldName);
	}
}
