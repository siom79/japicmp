package japicmp.test.serialversion;

import java.io.Serializable;

public class ModifiedAndSerialVersionUidModified implements Serializable {
	private static final long serialVersionUID = 42L;
	private int fieldRemoved = 42;
}
