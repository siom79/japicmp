package japicmp.versioning;

import japicmp.util.Optional;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version {
	private static final Logger LOGGER = Logger.getLogger(Version.class.getName());
	private static final Pattern VERSION_PATTERN_THREE_DIGITS = Pattern.compile(".*?([0-9]+)\\.([0-9]+)\\.([0-9]+).*");
	private final String version;

	public Version(String version) {
		this.version = version;
	}

	public Optional<SemanticVersion> getSemanticVersion() {
		return getSemanticVersion(version);
	}

	public static Optional<SemanticVersion> getSemanticVersion(String version)  {
		Optional<SemanticVersion> semanticVersion = Optional.absent();
		Matcher matcher = VERSION_PATTERN_THREE_DIGITS.matcher(version);
		if (matcher.matches()) {
			if (matcher.groupCount() >= 3) {
				try {
					int major = Integer.parseInt(matcher.group(1));
					int minor = Integer.parseInt(matcher.group(2));
					int patch = Integer.parseInt(matcher.group(3));
					semanticVersion = Optional.of(new SemanticVersion(major, minor, patch));
				} catch (NumberFormatException e) {
					LOGGER.log(Level.FINE, String.format("Could not convert version into three digits for file name: %s", version), e);
				}
			} else {
				LOGGER.log(Level.FINE, String.format("Could not find three digits separated by a point in file name: %s", version));
			}
		} else {
			LOGGER.log(Level.FINE, String.format("Could not find three digits separated by a point in file name: %s", version));
		}
		return semanticVersion;
	}

	public String getStringVersion() {
		return version;
	}
}
