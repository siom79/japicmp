package japicmp.util;

import static japicmp.model.VarargsModifier.*;
import static java.util.stream.Collectors.joining;

import japicmp.model.*;
import java.util.List;

public class TypeNameHelper {

	private static final String STR_ARRAY = "[]";
	private static final String STR_VARARGS = "...";

	private TypeNameHelper() {
	}

	/**
	 * Formats a generic template.
	 *
	 * @param name            The generic template name.
	 * @param type            The type extended by the generic template.
	 * @param genericTypes    The generic types of the generic template.
	 * @param shorten         Whether to truncate types to 32 characters or not.
	 * @return                A string with the format {@code "NAME extends TYPE<GENERIC TYPES>"}.
	 */
	public static String formatGenericTemplate(String name, String type, List<JApiGenericType> genericTypes, boolean shorten) {
		return type == null ? name : name + " extends " + formatTypeName(type, genericTypes, shorten);
	}

	/**
	 * Formats a Java type in FQN form plus generic types.
	 *
	 * @param typeName     The fully qualified type name to format.
	 * @param genericTypes The possibly empty list of generic types.
	 * @param maxLength	   The maximum number of characters that will be produced.
	 *                     If greater or equal to 3, the FQN will be truncated to its simple name,
	 *                     and surplus characters will be replaced with the ellipsis character.
	 * @return             The formatted type.
	 */
	public static String formatTypeName(String typeName, List<JApiGenericType> genericTypes, int maxLength) {
		if (typeName == null || typeName.isEmpty() || typeName.equals(OptionalHelper.N_A)) {
			return typeName;
		}
		return truncateTypeName(typeName, maxLength) + formatGenericTypes(genericTypes, maxLength);
	}

	/**
	 * Formats a Java type in FQN form plus generic templates.
	 *
	 * @param typeName            The fully qualified type name to format.
	 * @param hasChangeStatus     The element that has a change status.
	 * @param hasGenericTemplates The element that has generic templates.
	 * @param maxLength           The maximum number of characters that will be produced.
	 *                            If greater or equal to 3, the FQN will be truncated to its simple name,
	 *                            and surplus characters will be replaced with the ellipsis character.
	 * @return                    The formatted type.
	 */
	public static String formatTypeName(String typeName, JApiHasChangeStatus hasChangeStatus, JApiHasGenericTemplates hasGenericTemplates, int maxLength) {
		if (typeName == null || typeName.isEmpty() || typeName.equals(OptionalHelper.N_A)) {
			return typeName;
		}
		return truncateTypeName(typeName, maxLength) + (hasGenericTemplates == null ? ""
			: formatGenericTemplates(hasChangeStatus, hasGenericTemplates.getGenericTemplates(), maxLength));
	}

	/**
	 * Formats a Java type in FQN form plus generic types.
	 *
	 * @param type         The fully qualified name to format.
	 * @param genericTypes The possibly empty list of generic types.
	 * @param shorten      Whether to truncate the type to 32 characters or not.
	 * @return             The formatted type.
	 */
	public static String formatTypeName(String type, List<JApiGenericType> genericTypes, boolean shorten) {
		return formatTypeName(type, genericTypes, shorten ? 32 : -1);
	}

	/**
	 * Formats a Java type in FQN form plus generic templates.
	 *
	 * @param typeName            The fully qualified type name to format.
	 * @param hasChangeStatus     The element that has a change status.
	 * @param hasGenericTemplates The element that has generic templates.
	 * @param shorten             Whether to truncate the type to 32 characters or not.
	 * @return                    The formatted type.
	 */
	public static String formatTypeName(String typeName, JApiHasChangeStatus hasChangeStatus, JApiHasGenericTemplates hasGenericTemplates, boolean shorten) {
		return formatTypeName(typeName, hasChangeStatus, hasGenericTemplates, shorten ? 32 : -1);
	}

	/**
	 * Formats a parameter type plus generic types.
	 *
	 * @param method       The method this parameter belongs to.
	 * @param parameter    The parameter whose type will be formatted.
	 * @param varargs      The varargs modifier of the method.
	 * @param genericTypes The possibly empty list of generic types.
	 * @param shorten      Whether to truncate the type to 32 characters or not.
	 * @return             The formatted type.
	 */
	public static String formatParameterTypeName(JApiBehavior method, JApiParameter parameter, Optional<VarargsModifier> varargs, List<JApiGenericType> genericTypes, boolean shorten) {
		final String type = parameter.getType();
		final List<JApiParameter> params = method.getParameters();
		if (type.endsWith(STR_ARRAY) && parameter == params.get(params.size() - 1) && varargs.or(NON_VARARGS) == VARARGS) {
			return formatTypeName(type.substring(0, type.length() - 2) + STR_VARARGS, genericTypes, shorten);
		}
		return formatTypeName(type, genericTypes, shorten);
	}

	private static String formatGenericTypes(List<JApiGenericType> genericTypes, int maxLength) {
		if (genericTypes == null || genericTypes.isEmpty()) {
			return "";
		}
		return "<" + genericTypes.stream().map(x -> formatGenericType(x, maxLength)).collect(joining(", ")) + ">";
	}

	private static String formatGenericTemplates(JApiHasChangeStatus hasChangeStatus, List<JApiGenericTemplate> genericTemplates, int maxLength) {
		if (genericTemplates == null || genericTemplates.isEmpty()) {
			return "";
		}
		return "<" + genericTemplates.stream().map(x -> formatGenericTemplate(hasChangeStatus, x, maxLength)).collect(joining(", ")) + ">";
	}

	private static String formatGenericType(JApiGenericType genericType, int maxLength) {
		switch (genericType.getGenericWildCard()) {
			case NONE:
				return formatTypeName(genericType.getType(), genericType.getGenericTypes(), maxLength);
			default:
			case UNBOUNDED:
				return "?";
			case EXTENDS:
				return "? extends " + formatTypeName(genericType.getType(), genericType.getGenericTypes(), maxLength);
			case SUPER:
				return "? super " + formatTypeName(genericType.getType(), genericType.getGenericTypes(), maxLength);
		}
	}

	private static String formatGenericTemplate(JApiHasChangeStatus hasChangeStatus, JApiGenericTemplate genericTemplate, int maxLength) {
		final String name = genericTemplate.getName();
		String type = null;
		List<JApiGenericType> genericTypes = null;
		if (maxLength <= 0) {
			switch (hasChangeStatus.getChangeStatus()) {
				default:
				case MODIFIED:
				case NEW:
					if (genericTemplate.getNewTypeOptional().isPresent()) {
						type = genericTemplate.getNewType();
						genericTypes = genericTemplate.getNewGenericTypes();
						break;
					}
				case UNCHANGED:
				case REMOVED:
					if (genericTemplate.getOldTypeOptional().isPresent()) {
						type = genericTemplate.getOldType();
						genericTypes = genericTemplate.getOldGenericTypes();
					}
			}
		}
		if (type == null) {
			return name;
		}
		return name + " extends " + formatTypeName(type, genericTypes, maxLength);
	}

	private static String truncateTypeName(String typeName, int maxLength) {
		if (maxLength < 3) {
			return typeName;
		}
		// Enforcing max length implies sticking to simple name only
		final int dollar = typeName.lastIndexOf('$');
		final int dot = (typeName.endsWith(STR_VARARGS) ? typeName.substring(0, typeName.length() - 3) : typeName).lastIndexOf('.');
		final int pos = Integer.max(dollar, dot);
		final String name = pos > 0 ? typeName.substring(pos + 1) : typeName;
		final int length = name.length();
		if (length > maxLength) {
			// Cut in half and replace surplus characters with ellipsis
			final int half = maxLength / 2;
			return name.substring(0, maxLength - half - 1) + '\u2026' + name.substring(length - half);
		}
		return name;
	}
}
