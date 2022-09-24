package japicmp.model;

import java.util.List;

public interface JApiHasGenericTypes {
	List<JApiGenericType> getOldGenericTypes();
	List<JApiGenericType> getNewGenericTypes();
}
