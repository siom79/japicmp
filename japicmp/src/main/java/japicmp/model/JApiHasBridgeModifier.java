package japicmp.model;

/**
 * Implemented by all elements that have a bridge modifier.
 */
public interface JApiHasBridgeModifier {

	JApiModifier<BridgeModifier> getBridgeModifier();
}
