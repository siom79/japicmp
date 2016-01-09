package japicmp.test;

import java.util.Collections;
import java.util.List;

public class MethodReturnType {

	public int methodReturnTypeUnchanged() {
		return 42;
	}

	public int methodReturnTypeChangesFromIntToString() {
		return 42;
	}

	public String methodReturnTypeChangesFromStringToInt() {
		return "";
	}

	public List<MethodReturnType> methodReturnTypeChangesFromListToMap() {
		return Collections.EMPTY_LIST;
	}

	public void methodReturnTypeChangesFromVoidToInt() {

	}
}
