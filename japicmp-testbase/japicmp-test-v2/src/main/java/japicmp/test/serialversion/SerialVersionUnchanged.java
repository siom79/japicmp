package japicmp.test.serialversion;

import java.io.Serializable;

public class SerialVersionUnchanged implements Serializable {
	private int intField = 0;

	public int getIntField() {
		return intField;
	}

	public void setIntField(int intField) {
		this.intField = intField;
	}
}
