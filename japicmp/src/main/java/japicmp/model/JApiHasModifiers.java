package japicmp.model;

import java.util.List;

public interface JApiHasModifiers extends JApiHasChangeStatus {

	List<JApiModifier<? extends Enum<?>>> getModifiers();
}
