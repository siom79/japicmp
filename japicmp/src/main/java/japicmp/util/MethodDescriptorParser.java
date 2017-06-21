package japicmp.util;

import java.util.LinkedList;
import java.util.List;

public class MethodDescriptorParser {
	private final List<String> parameters = new LinkedList<>();
	private String returnType = "void";

	/**
	 * Parses a method descriptor as specified in the Java Virtual Machine Specification (see http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3.3).
	 *
	 * @param methodDescriptor the method descriptor
	 */
	public void parse(String methodDescriptor) {
		int parenthesisCloseIndex = methodDescriptor.indexOf(')');
		if (parenthesisCloseIndex > -1) {
			parseParameters(methodDescriptor, parenthesisCloseIndex);
			parseReturnValue(methodDescriptor, parenthesisCloseIndex);
		}
	}

	private void parseReturnValue(String signature, int parenthesisCloseIndex) {
		String retValPart = signature.substring(parenthesisCloseIndex + 1);
		List<String> retValTypes = parseTypes(retValPart);
		returnType = retValTypes.get(0);
	}

	private void parseParameters(String signature, int parenthesisCloseIndex) {
		String paramPart = signature.substring(1, parenthesisCloseIndex);
		List<String> paramTypes = parseTypes(paramPart);
		parameters.clear();
		parameters.addAll(paramTypes);
	}

	public List<String> parseTypes(String paramPart) {
		List<String> types = new LinkedList<>();
		boolean arrayNotation = false;
		for (int i = 0; i < paramPart.length(); i++) {
			char c = paramPart.charAt(i);
			String type = "void";
			switch (c) {
				case 'Z':
					type = "boolean";
					break;
				case 'B':
					type = "byte";
					break;
				case 'C':
					type = "char";
					break;
				case 'S':
					type = "short";
					break;
				case 'I':
					type = "int";
					break;
				case 'J':
					type = "long";
					break;
				case 'F':
					type = "float";
					break;
				case 'D':
					type = "double";
					break;
				case 'V':
					type = "void";
					break;
				case '[':
					arrayNotation = true;
					continue;
				case 'L':
					StringBuilder fqn = new StringBuilder();
					i++;
					while (i < paramPart.length()) {
						c = paramPart.charAt(i);
						if (c == ';') {
							break;
						} else if (c == '/') {
							fqn.append('.');
						} else {
							fqn.append(c);
						}
						i++;
					}
					type = fqn.toString();
					break;
				default:
					throw new IllegalStateException("Unknown type signature: '" + c + "'");
			}
			if (arrayNotation) {
				type += "[]";
				arrayNotation = false;
			}
			types.add(type);
		}
		return types;
	}

	public List<String> getParameters() {
		return parameters;
	}

	public String getReturnType() {
		return returnType;
	}

	public String getMethodSignature(String methodName) {
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		for (String parameter : parameters) {
			if (counter > 0) {
				sb.append(",");
			}
			sb.append(parameter);
			counter++;
		}
		return methodName + "(" + sb.toString() + ")";
	}
}
