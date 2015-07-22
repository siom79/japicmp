package japicmp.model;

import java.util.List;

/**
 *
 */
public interface JApiHasModifier extends JApiHasChangeStatus {

	List<JApiModifier<? extends Enum<?>>> getModifiers();
}
