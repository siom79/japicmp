package japicmp.model;

import japicmp.exception.JApiCmpException;

import java.util.Optional;

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
		for (AccessModifier am : values()) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(am.toString());
			i++;
		}
		return sb.toString();
	}

	public static Optional<AccessModifier> toModifier(String accessModifierArg) {
		if (accessModifierArg != null) {
			try {
				return Optional.of(valueOf(accessModifierArg.toUpperCase()));
			} catch (IllegalArgumentException e) {
				throw new JApiCmpException(JApiCmpException.Reason.CliError, String.format("Invalid value for option accessModifier: %s. Possible values are: %s.",
					accessModifierArg, listOfAccessModifier()), e);
			}
		} else {
			return Optional.of(PROTECTED);
		}
	}
}
