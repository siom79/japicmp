package japicmp.model;

/**
 * Represents the access modifiers as defined in the Java Language Specification.
 */
public enum AccessModifier implements JApiModifierBase {
	PUBLIC(3), PROTECTED(2), PACKAGE_PROTECTED(1), PRIVATE(0);

	private int level;

	AccessModifier(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public static String listOfAccessModifier() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (AccessModifier am : AccessModifier.values()) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(am.toString());
			i++;
		}
		return sb.toString();
	}
}
