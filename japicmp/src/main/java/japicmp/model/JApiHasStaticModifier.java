package japicmp.model;

/**
 * Implemented by all elements that have a static modifier.
 */
public interface JApiHasStaticModifier {
	/**
	 * Returns the static modifier.
	 *
	 * @return the static modifier
	 */
	JApiModifier<StaticModifier> getStaticModifier();
}
