package japicmp.model;

/**
 * Implemented by all elements that have a bridge modifier.
 */
public interface JApiHasBridgeModifier {
	/**
	 * Returns the bridge modifier. The ACC_BRIDGE property is
	 * added by the compiler for bridge methods.
	 *
	 * @return the bridge modifier
	 */
	JApiModifier<BridgeModifier> getBridgeModifier();
}
