package japicmp.model;

/**
 * Implemented by all elements that can have a change status.
 */
public interface JApiHasChangeStatus {
	/**
	 * Returns the change status of this element.
	 *
	 * @return the change status of this element
	 */
	JApiChangeStatus getChangeStatus();
}
