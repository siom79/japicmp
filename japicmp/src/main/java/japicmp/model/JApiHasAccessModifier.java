package japicmp.model;

/**
 * Implemented by all elements that have an access modifier.
 */
public interface JApiHasAccessModifier {
	/**
	 * Returns the access modifier.
	 *
	 * @return the access modifier
	 */
	JApiModifier<AccessModifier> getAccessModifier();
}
