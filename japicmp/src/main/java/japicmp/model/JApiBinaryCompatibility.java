package japicmp.model;

import java.util.List;

/**
 * Implemented by elements which can indicate if they have changed binary compatible or not.
 */
public interface JApiBinaryCompatibility {
	/**
	 * Returns true if this element has changed binary compatible.
	 *
	 * @return true if this element has changed binary compatible
	 */
	boolean isBinaryCompatible();
	/**
	 * Returns all compatibility changes.
	 * @return a list of compatibility changes
     */
	List<JApiCompatibilityChange> getCompatibilityChanges();
}
