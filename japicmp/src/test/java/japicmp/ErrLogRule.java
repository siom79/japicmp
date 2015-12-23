package japicmp;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.junit.contrib.java.lang.system.LogMode;
import org.junit.contrib.java.lang.system.StandardErrorStreamLog;

public class ErrLogRule extends StandardErrorStreamLog {
	public ErrLogRule(LogMode logOnly) {
		super(logOnly);
	}

	static String onlyLineOf(String in) {
		ImmutableList<String> lines = linesOf(in);
		if (lines.size() == 1) {
			return in.replace("\r", "");
		}
		throw new IllegalStateException("more then one line was found, but was \"" + //
			in.replace("\r", "") + "\"");
	}

	public static ImmutableList<String> linesOf(String in) {
		return ImmutableList.copyOf(Splitter.on("\n").omitEmptyStrings().trimResults().split(in));
	}

	public String getOnlyLine() {
		return ErrLogRule.onlyLineOf(getLog());
	}

	public ImmutableList<String> getLines() {
		return linesOf(getLog());
	}
}
