package japicmp.model;

import java.util.List;

public interface JApiHasModifiers extends JApiHasChangeStatus {

	List<? extends JApiModifier<? extends Enum<? extends Enum<?>>>> getModifiers();
}
