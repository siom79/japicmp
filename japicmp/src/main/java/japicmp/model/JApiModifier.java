package japicmp.model;

import japicmp.util.OptionalHelper;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.Optional;

public class JApiModifier<T> implements JApiHasChangeStatus {
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

	@Override
	@XmlAttribute(name = "changeStatus")
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}

	@XmlAttribute(name = "oldValue")
	public String getValueOld() {
		return OptionalHelper.optionalToString(this.oldModifier);
	}

	@XmlAttribute(name = "newValue")
	public String getValueNew() {
		return OptionalHelper.optionalToString(this.newModifier);
	}

	public boolean hasChangedFromTo(T oldValue, T newValue) {
		boolean hasChanged = false;
		if (oldModifier.isPresent() && newModifier.isPresent()) {
			if (oldModifier.get() == oldValue && newModifier.get() == newValue) {
				hasChanged = true;
			}
		}
		return hasChanged;
	}

	public boolean hasChangedFrom(T oldValue) {
		boolean hasChanged = false;
		if (oldModifier.isPresent() && newModifier.isPresent()) {
			if (oldModifier.get() == oldValue && newModifier.get() != oldValue) {
				hasChanged = true;
			}
		} else if (oldModifier.isPresent()) {
			if (oldModifier.get() == oldValue) {
				hasChanged = true;
			}
		} else {
			hasChanged = true;
		}
		return hasChanged;
	}

	public boolean hasChangedTo(T value) {
		boolean hasChangedTo = false;
		if (oldModifier.isPresent() && newModifier.isPresent()) {
			T newValue = newModifier.get();
			T oldValue = oldModifier.get();
			if (value == newValue && oldValue != value) {
				hasChangedTo = true;
			}
		}
		return hasChangedTo;
	}

	public boolean hasChangedToMoreVisible() {
		boolean hasChangedTo = false;
		if (oldModifier.isPresent() && newModifier.isPresent()) {
			T oldValue = oldModifier.get();
			T newValue = newModifier.get();
			if (oldValue instanceof AccessModifier && newValue instanceof AccessModifier) {
				return ((AccessModifier)newValue).getLevel() > ((AccessModifier)oldValue).getLevel();
			}
		}
		return hasChangedTo;
	}
}
