package japicmp.model;

import java.util.List;

/**
 * Implemented by all elements that have a modifier (access, synthetic, etc.)
 */
public interface JApiHasModifiers extends JApiHasChangeStatus {
	/**
	 * Returns a list of modifiers.
	 *
	 * @return a list of modifiers
	 */
	List<? extends JApiModifier<? extends Enum<? extends Enum<?>>>> getModifiers();
}
