package japicmp.util;

import japicmp.model.JApiClassFileFormatVersion;

public class JApiClassFileFormatVersionHelper {

	private JApiClassFileFormatVersionHelper() {
	}

	public static String getOldJdkVersion(JApiClassFileFormatVersion x) {
		return getJdkVersion(x.getMajorVersionOld(), x.getMinorVersionOld());
	}

	public static String getNewJdkVersion(JApiClassFileFormatVersion x) {
		return getJdkVersion(x.getMajorVersionNew(), x.getMinorVersionNew());
	}

	private static String getJdkVersion(int major, int minor) {
		if (major == -1 || minor == -1) {
			return "";
		}
		if (major == 45) {
			return minor < 3 ? "JDK 1.0" : "JDK 1.1";
		} else if (major == 46) {
			return "JDK 1.2";
		} else if (major == 47) {
			return "JDK 1.3";
		} else if (major == 48) {
			return "JDK 1.4";
		} else if (major >= 49) {
			// according to: https://andbin.github.io/java-versions-cheat-sheet/
			return "JDK " + (major - 44);
		}
		return "Version " + major + "." + minor;
	}
}
