package japicmp.model;

/**
 * Implemented by all elements that can have a synthetic modifier.
 */
public interface JApiHasSyntheticModifier {

	JApiModifier<SyntheticModifier> getSyntheticModifier();
}
