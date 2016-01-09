package japicmp.model;

import java.util.List;

/**
 * Implemented by all elements that can have annotations.
 */
public interface JApiHasAnnotations {
	/**
	 * Returns a list of annotations as {@link japicmp.model.JApiAnnotation}.
	 *
	 * @return a list of annotations as {@link japicmp.model.JApiAnnotation}
	 */
	List<JApiAnnotation> getAnnotations();
}
