package japicmp.model;

/**
 * Implemented by all elements that have a final modifier.
 */
public interface JApiHasFinalModifier {
	/**
	 * Returns the final modifier.
	 *
	 * @return the final modifier
	 */
	JApiModifier<FinalModifier> getFinalModifier();
}
