package japicmp.model;

/**
 * Implemented by elements which can indicate if they have changed binary compatible or not.
 */
public interface JApiBinaryCompatibility {
	/**
	 * Returns true if this element has changed binary compatible.
	 * @return true if this element has changed binary compatible
	 */
	boolean isBinaryCompatible();
}
