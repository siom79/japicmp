package japicmp.output.markdown.config;

import static japicmp.output.markdown.Markdown.*;
import static japicmp.output.semver.SemverOut.*;
import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import japicmp.model.*;
import japicmp.model.JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus;
import japicmp.output.markdown.MarkdownBadge;
import japicmp.util.Optional;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class MarkdownMessageOptions {

	public String summaryMajorChanges = "> [!CAUTION]\n>\n> Incompatible changes found while checking backward compatibility of %s with %s.";
	public String summaryMinorChanges = "> [!WARNING]\n>\n> Compatible changes found while checking backward compatibility of %s with %s.";
	public String summaryPatchChanges = "> [!IMPORTANT]\n>\n> Compatible bug fixes found while checking backward compatibility of %s with %s.";
	public String summaryNoChanges    = "> [!NOTE]\n>\n> No incompatible changes found while checking backward compatibility of %s with %s.";

	public String oneNewVersion   = "version `%s`";
	public String oneOldVersion   = "the previous version `%s`";
	public String manyNewArchives = "several archives";
	public String manyOldArchives = "their previous versions";
	public String unknownVersion  = "unknown";
	public String expandResults   = "Expand for details.";
	public String expandOptions   = "Expand to see options used.";

	public String warningAllMissingClassesIgnored  = "> [!WARNING]\n>\n> All missing classes, i.e. superclasses and interfaces that could not be found on the classpath were ignored.\n>\n> Hence changes caused by these superclasses and interfaces are not reflected in the output.\n\n";
	public String warningSomeMissingClassesIgnored = "> [!WARNING]\n>\n> Certain classes, i.e. superclasses and interfaces that could not be found on the classpath were ignored.\n>\n> Hence changes caused by these superclasses and interfaces are not reflected in the output.\n\n";

	public String generatedOn    = "*Generated on: %s*.";
	public String dateTimeFormat = "yyyy-MM-dd HH:mm:ss.SSSZ";

	public String classpathMode                       = "**Classpath mode**: `%s`";
	public String oldArchives                         = "**Old archives**:%s";
	public String newArchives                         = "**New archives**:%s";
	public String oldClasspath                        = "**Old classpath**:\n```\n%s\n```";
	public String newClasspath                        = "**New classpath**:\n```\n%s\n```";
	public String accessModifierFilter                = "**Access modifier filter**: `%s`";
	public String evaluateAnnotations                 = "**Evaluate annotations**: %s";
	public String reportOnlySummary                   = "**Report only summary**: %s";
	public String reportOnlyChanges                   = "**Report only changes**: %s";
	public String reportOnlyBinaryIncompatibleChanges = "**Report only binary-incompatible changes**: %s";
	public String includeSpecificElements             = "**Include specific elements**: %s";
	public String includeSynthetic                    = "**Include synthetic classes and class members**: %s";
	public String excludeSpecificElements             = "**Exclude specific elements**: %s";
	public String ignoreAllMissingClasses             = "**Ignore all missing classes**: %s";
	public String ignoreSpecificMissingClasses        = "**Ignore specific missing classes**: %s";
	public String treatChangesAsErrors                = "**Treat changes as errors**:%s";

	public String anyChanges                                                  = "Any changes: %s";
	public String binaryIncompatibleChanges                                   = "Binary incompatible changes: %s";
	public String sourceIncompatibleChanges                                   = "Source incompatible changes: %s";
	public String incompatibleChangesCausedByExcludedClasses                  = "Incompatible changes caused by excluded classes: %s";
	public String semanticallyIncompatibleChanges                             = "Semantically incompatible changes: %s";
	public String semanticallyIncompatibleChangesIncludingDevelopmentVersions = "Semantically incompatible changes, including development versions: %s";

	public String added     = "**%s**";
	public String removed   = "~~%s~~";
	public String modified  = "~~%s~~ &rarr; **%s**";
	public String unchanged = "%s";

	public String yes = "Yes";
	public String no  = "No";

	public String checked   = "[X]";
	public String unchecked = "[ ]";

	public String compatibilityBinary        = "%s Binary-compatible";
	public String compatibilitySource        = "%s Source-compatible";
	public String compatibilitySerialization = "%s Serialization-compatible";

	public String noCompatibilityChanges = "No changes";

	public String colorMajorChanges  = "red";
	public String colorMinorChanges  = "orange";
	public String colorPatchChanges  = "yellow";
	public String colorNoChanges     = "green";
	public String colorVersionNumber = "blue";

	public String badgeMajorChanges = new MarkdownBadge("semver", "MAJOR", colorMajorChanges, "semver").toString();
	public String badgeMinorChanges = new MarkdownBadge("semver", "MINOR", colorMinorChanges, "semver").toString();
	public String badgePatchChanges = new MarkdownBadge("semver", "PATCH", colorPatchChanges, "semver").toString();
	public String badgeNoChanges    = new MarkdownBadge("semver", "OK", colorNoChanges, "semver").toString();

	public String typeAnnotation = "Annotation";
	public String typeInterface  = "Interface";
	public String typeClass      = "Class";
	public String typeEnum       = "Enum";

	public String statusNew                       = "Added";
	public String statusRemoved                   = "Removed";
	public String statusModified                  = "Modified";
	public String statusUnchanged                 = "Unchanged";
	public String statusIncompatible              = "Incompatible";
	public String statusSourceIncompatible        = "Source-incompatible";
	public String statusBinaryIncompatible        = "Binary-incompatible";
	public String statusSerializationIncompatible = "Serialization-incompatible";

	public Map<JApiJavaObjectSerializationChangeStatus, String> serializationCompatibility =
		stream(JApiJavaObjectSerializationChangeStatus.values()).collect(toMap(identity(), e -> capitalize(e.getDescription())));

	public Map<JApiCompatibilityChangeType, String> compatibilityChangeType =
		stream(JApiCompatibilityChangeType.values()).collect(toMap(identity(), e -> capitalize(e.name())));

	private static String capitalize(String text) {
		return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase().replace(UNDERSCORE, SPACE);
	}

	public String getSemanticColor(JApiSemanticVersionLevel level) {
		switch (level) {
			default:
			case MAJOR:
				return colorMajorChanges;
			case MINOR:
				return colorMinorChanges;
			case PATCH:
				return colorPatchChanges;
		}
	}

	public String getSemverBadge(String semver) {
		switch (semver) {
			case SEMVER_MAJOR:
				return badgeMajorChanges;
			case SEMVER_MINOR:
				return badgeMinorChanges;
			case SEMVER_PATCH:
				return badgePatchChanges;
			case SEMVER_COMPATIBLE:
			default:
				return badgeNoChanges;
		}
	}

	public String getSummaryMessage(String semver) {
		switch (semver) {
			case SEMVER_MAJOR:
				return summaryMajorChanges;
			case SEMVER_MINOR:
				return summaryMinorChanges;
			case SEMVER_PATCH:
				return summaryPatchChanges;
			case SEMVER_COMPATIBLE:
			default:
				return summaryNoChanges;
		}
	}

	public String getClassType(Optional<JApiClassType.ClassType> classType) {
		if (!classType.isPresent()) {
			return EMPTY;
		}
		switch (classType.get()) {
			case ANNOTATION:
				return typeAnnotation;
			case INTERFACE:
				return typeInterface;
			default:
			case CLASS:
				return typeClass;
			case ENUM:
				return typeEnum;
		}
	}

	public String getCurrentTimestamp() {
		final ZonedDateTime now = ZonedDateTime.now();
		try {
			return DateTimeFormatter.ofPattern(dateTimeFormat).format(now);
		} catch (Exception e) {
			return now.toString();
		}
	}

	public String yesNo(boolean value) {
		return value ? yes : no;
	}

	public String checkbox(boolean value) {
		return value ? checked : unchecked;
	}
}
