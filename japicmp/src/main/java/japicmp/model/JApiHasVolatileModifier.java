package japicmp.model;

/**
 * Implemented by all elements that can have a volatile modifier.
 */
public interface JApiHasVolatileModifier {
	/**
	 * Returns the volatile modifier.
	 *
	 * @return the volatile modifier
	 */
	JApiModifier<VolatileModifier> getVolatileModifier();
}
