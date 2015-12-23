package japicmp.model;

import javax.xml.bind.annotation.XmlAttribute;
import com.google.common.base.Optional;
import japicmp.util.OptionalHelper;

public class JApiReturnType {
	private final Optional<String> oldReturnTypeOptional;
	private final Optional<String> newReturnTypeOptional;
	private final JApiChangeStatus changeStatus;

	public JApiReturnType(JApiChangeStatus changeStatus, Optional<String> oldReturnTypeOptional, Optional<String> newReturnTypeOptional) {
		this.changeStatus = changeStatus;
		this.oldReturnTypeOptional = oldReturnTypeOptional;
		this.newReturnTypeOptional = newReturnTypeOptional;
	}

	@XmlAttribute(name = "changeStatus")
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}

	@XmlAttribute(name = "oldValue")
	public String getOldReturnType() {
		return OptionalHelper.optionalToString(oldReturnTypeOptional);
	}

	@XmlAttribute(name = "newValue")
	public String getNewReturnType() {
		return OptionalHelper.optionalToString(newReturnTypeOptional);
	}
}
