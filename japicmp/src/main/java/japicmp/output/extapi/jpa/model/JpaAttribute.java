package japicmp.output.extapi.jpa.model;

import japicmp.model.JApiChangeStatus;

public class JpaAttribute {
	private final JApiChangeStatus changeStatus;

	public JpaAttribute(JApiChangeStatus changeStatus) {
		this.changeStatus = evaluateChangeStatus(changeStatus);
	}

	private JApiChangeStatus evaluateChangeStatus(JApiChangeStatus changeStatus) {
		if (changeStatus == JApiChangeStatus.UNCHANGED) {

		}
		return changeStatus;
	}
}
