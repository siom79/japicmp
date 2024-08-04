package japicmp.util;

import static japicmp.util.TypeNameHelper.formatTypeName;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.Arrays.stream;

import java.util.Set;
import javassist.bytecode.annotation.*;

public class MemberValueHelper {

	private MemberValueHelper() {
	}

	/**
	 * Formats an annotation's member value.
	 *
	 * @param value   The member value.
	 * @param shorten Whether to truncate types to 32 characters or not.
	 * @return        The formatted member value.
	 */
	public static String formatMemberValue(MemberValue value, boolean shorten) {
		if (value instanceof AnnotationMemberValue) {
			final Annotation annotation = ((AnnotationMemberValue) value).getValue();
			final Set<String> names = annotation.getMemberNames();
			final String typeName = formatTypeName(annotation.getTypeName(), emptyList(), shorten);
			if (names.isEmpty()) {
				return typeName;
			}
			return typeName + "(" + names.stream().map(annotation::getMemberValue).map(MemberValue::toString).collect(joining(", ")) + ")";
		} else if (value instanceof ArrayMemberValue) {
			return "{" + stream(((ArrayMemberValue) value).getValue()).map(x -> formatMemberValue(x, shorten)).collect(joining(", ")) + "}";
		} else if (value instanceof BooleanMemberValue) {
			return Boolean.toString(((BooleanMemberValue) value).getValue());
		} else if (value instanceof ByteMemberValue) {
			return Byte.toString(((ByteMemberValue) value).getValue());
		} else if (value instanceof CharMemberValue) {
			return Character.toString(((CharMemberValue) value).getValue());
		} else if (value instanceof FloatMemberValue) {
			return Float.toString(((FloatMemberValue) value).getValue());
		} else if (value instanceof DoubleMemberValue) {
			return Double.toString(((DoubleMemberValue) value).getValue());
		} else if (value instanceof ShortMemberValue) {
			return Short.toString(((ShortMemberValue) value).getValue());
		} else if (value instanceof IntegerMemberValue) {
			return Integer.toString(((IntegerMemberValue) value).getValue());
		} else if (value instanceof LongMemberValue) {
			return Long.toString(((LongMemberValue) value).getValue());
		} else if (value instanceof EnumMemberValue) {
			return shorten ? ((EnumMemberValue) value).getValue() : value.toString();
		} else if (value instanceof StringMemberValue) {
			return "\"" + ((StringMemberValue) value).getValue() + "\"";
		} else if (value instanceof ClassMemberValue) {
			return TypeNameHelper.formatTypeName(((ClassMemberValue) value).getValue(), emptyList(), shorten);
		}
		return value.toString();
	}
}
