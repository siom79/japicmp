package japicmp.model;

/**
 * Implemented by all elements that can have a transient modifier.
 */
public interface JApiHasTransientModifier {
	/**
	 * Returns the transient modifier.
	 *
	 * @return the transient modifier
	 */
	JApiModifier<TransientModifier> getTransientModifier();
}
