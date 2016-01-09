package japicmp.model;

/**
 * Implemented by all elements that can have a synthetic modifier.
 */
public interface JApiHasSyntheticModifier {
	/**
	 * Returns the synthetic modifier.
	 *
	 * @return the synthetic modifier
	 */
	JApiModifier<SyntheticModifier> getSyntheticModifier();
}
