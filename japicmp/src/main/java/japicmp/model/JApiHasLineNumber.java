package japicmp.model;

import japicmp.util.Optional;

/**
 * This interface is implemented by all elements that can have line number
 * in their source file.
 */
public interface JApiHasLineNumber {
	/**
	 * Returns the line number in the source file of the old element.
	 *
	 * @return the line number in the source file of the old element
	 */
	Optional<Integer> getOldLineNumber();

	/**
	 * Returns the line number in the source file of the new element.
	 *
	 * @return the line number in the source file of the new element
	 */
	Optional<Integer> geNewLineNumber();
}
