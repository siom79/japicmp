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
		} else if (major == 49) {
			return "JDK 5.0";
		} else if (major == 50) {
			return "JDK 6";
		} else if (major == 51) {
			return "JDK 7";
		} else if (major == 52) {
			return "JDK 8";
		} else if (major == 53) {
			return "JDK 9";
		} else if (major == 54) {
			return "JDK 10";
		} else if (major == 55) {
			return "JDK 11";
		} else if (major == 56) {
			return "JDK 12";
		} else if (major == 57) {
			return "JDK 13";
		} else if (major == 58) {
			return "JDK 14";
		} else if (major == 59) {
			return "JDK 15";
		} else if (major == 60) {
			return "JDK 16";
		} else if (major == 61) {
			return "JDK 17";
		} else if (major == 62) {
			return "JDK 18";
		} else if (major == 63) {
			return "JDK 19";
		} else if (major == 64) {
			return "JDK 20";
		} else if (major == 65) {
			return "JDK 21";
		} else if (major == 66) {
			return "JDK 22";
		} else if (major == 67) {
			return "JDK 23";
		}
		return "Version " + major + "." + minor;
	}
}
