package japicmp.model;

import com.google.common.base.Optional;

public class JApiModifier<T> {
	private final Optional<T> oldModifier;
	private final Optional<T> newModifier;
	private final JApiChangeStatus changeStatus;
	
	public JApiModifier(Optional<T> oldModifier, Optional<T> newModifier, JApiChangeStatus changeStatus) {
		this.oldModifier = oldModifier;
		this.newModifier = newModifier;
		this.changeStatus = changeStatus;
	}

	public Optional<T> getOldModifier() {
		return oldModifier;
	}

	public Optional<T> getNewModifier() {
		return newModifier;
	}

	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}
}
