package japicmp.model;

/**
 * Represents the change status.
 */
public enum JApiChangeStatus {
	NEW, REMOVED, UNCHANGED, MODIFIED;

	public boolean isNotNewOrRemoved() {
		return this != NEW && this != REMOVED;
	}
}
