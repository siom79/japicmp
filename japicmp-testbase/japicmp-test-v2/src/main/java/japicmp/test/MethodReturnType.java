package japicmp.test;

import java.util.Collections;
import java.util.Map;

public class MethodReturnType {

	public int methodReturnTypeUnchanged() {
		return 42;
	}

	public String methodReturnTypeChangesFromIntToString() {
		return "";
	}

	public int methodReturnTypeChangesFromStringToInt() {
		return 42;
	}

	public Map<String, MethodReturnType> methodReturnTypeChangesFromListToMap() {
		return Collections.EMPTY_MAP;
	}

	public int methodReturnTypeChangesFromVoidToInt() {
		return 42;
	}
}
