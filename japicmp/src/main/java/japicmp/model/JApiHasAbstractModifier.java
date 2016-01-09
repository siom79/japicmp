package japicmp.model;

/**
 * Interface for any elements that have an abstract modifier.
 */
public interface JApiHasAbstractModifier {
	/**
	 * Returns the abstract modifier.
	 *
	 * @return the abstract modifier
	 */
	JApiModifier<AbstractModifier> getAbstractModifier();
}
