package japicmp.model;

import java.util.List;

/**
 * Implemented by elements which can indicate if they have changed compatible or not.
 */
public interface JApiCompatibility {
	/**
	 * Returns true if this element has changed binary compatible.
	 *
	 * @return true if this element has changed binary compatible
	 */
	boolean isBinaryCompatible();

	/**
	 * Returns true if this element has changed source compatible.
	 *
	 * @return true if this element has changed source compatible
     */
	boolean isSourceCompatible();

	/**
	 * Returns all compatibility changes.
	 * @return a list of compatibility changes
     */
	List<JApiCompatibilityChange> getCompatibilityChanges();
}
