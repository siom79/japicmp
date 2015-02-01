package japicmp;

import org.junit.contrib.java.lang.system.LogMode;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;

public class OutLogRule extends StandardOutputStreamLog {
	public OutLogRule(LogMode logOnly) {
		super(logOnly);
	}
}
