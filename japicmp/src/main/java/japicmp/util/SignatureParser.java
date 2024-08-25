package japicmp.util;

import japicmp.exception.JApiCmpException;
import japicmp.model.JApiClass;
import japicmp.model.JApiGenericTemplate;
import japicmp.model.JApiGenericType;
import japicmp.model.JApiParameter;
import javassist.CtBehavior;
import javassist.CtClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignatureParser {
	private static final Logger LOGGER = Logger.getLogger(SignatureParser.class.getName());
	private final List<ParsedParameter> parameters = new ArrayList<>();
	private ParsedParameter returnType = new ParsedParameter("void");
	private final List<ParsedTemplate> templates = new ArrayList<>();

	public interface HasGenericTypes {
		List<ParsedParameter> getGenericTypes();
	}

	public static class ParsedParameter implements HasGenericTypes {
		String type = "";
		List<ParsedParameter> genericTypes = new ArrayList<>();
		JApiGenericType.JApiGenericWildCard genericWildCard = JApiGenericType.JApiGenericWildCard.NONE;
		boolean template = false;

		public ParsedParameter() {

		}

		public ParsedParameter(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}

		public List<ParsedParameter> getGenericTypes() {
			return genericTypes;
		}

		public JApiGenericType.JApiGenericWildCard getGenericWildCard() {
			return genericWildCard;
		}

		public boolean isTemplate() {
			return template;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ParsedParameter that = (ParsedParameter) o;
			return Objects.equals(type, that.type);
		}

		@Override
		public int hashCode() {
			return Objects.hash(type);
		}
	}

	public static class ParsedTemplate implements HasGenericTypes {
		String name = "";
		String type = "";
		List<ParsedParameter> genericTypes = new ArrayList<>();
		List<ParsedParameter> interfaces = new ArrayList<>();

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}

		@Override
		public List<ParsedParameter> getGenericTypes() {
			return genericTypes;
		}

		public List<ParsedParameter> getInterfaces() {
			return interfaces;
		}
	}

	public void parse(CtBehavior behavior) {
		String signature = behavior.getSignature();
		parse(signature);
	}

	public void parse(String signature) {
		if (signature == null) {
			return;
		}
		int parenthesisOpenIndex = signature.indexOf('(');
		int parenthesisCloseIndex = signature.indexOf(')');
		if (parenthesisOpenIndex > 0 && signature.startsWith("<")) {
			String templateDefWithBrackets = signature.substring(0, parenthesisOpenIndex);
			if (templateDefWithBrackets.startsWith("<") && templateDefWithBrackets.endsWith(">") && templateDefWithBrackets.length() > 2) {
				parseTemplateDefinition(templateDefWithBrackets.substring(1, templateDefWithBrackets.length() - 1));
			}
		}
		if (parenthesisCloseIndex > -1) {
			parseParameters(signature, parenthesisCloseIndex);
			parseReturnValue(signature, parenthesisCloseIndex);
		}
	}

	private void parseReturnValue(String signature, int parenthesisCloseIndex) {
		String retValPart = signature.substring(parenthesisCloseIndex + 1);
		List<ParsedParameter> retValTypes = parseTypes(retValPart);
		if (!retValTypes.isEmpty()) {
			returnType = retValTypes.get(0);
		}
	}

	private void parseParameters(String signature, int parenthesisCloseIndex) {
		int parenthesisOpenIndex = signature.indexOf('(');
		if (parenthesisOpenIndex < 0) {
			throw new JApiCmpException(JApiCmpException.Reason.IllegalState, "Signature does not contain '('.");
		}
		if (parenthesisCloseIndex - parenthesisOpenIndex < 1) {
			throw new JApiCmpException(JApiCmpException.Reason.IllegalState, "Signature must contain the char '(' before the char ')'.");
		}
		String paramPart = signature.substring(parenthesisOpenIndex + 1, parenthesisCloseIndex);
		parameters.clear();
		List<ParsedParameter> paramTypes = parseTypes(paramPart);
		parameters.addAll(paramTypes);
	}

	public List<ParsedTemplate> parseTemplatesOfClass(CtClass ctClass) {
		String genericSignature = ctClass.getGenericSignature();
		if (genericSignature != null && genericSignature.startsWith("<")) {
			int lastClosingBracket = genericSignature.lastIndexOf('>');
			if (lastClosingBracket > 0 && genericSignature.length() - 2 > 0) {
				parseTemplateDefinition(genericSignature.substring(1, genericSignature.length() - 1));
				return this.templates;
			}
		}
		return Collections.emptyList();
	}

	public List<ParsedParameter> parseTypes(String paramPart) {
		List<ParsedParameter> types = new ArrayList<>();
		ParsedParameter parsedParameter = new ParsedParameter();
		int arrayNotation = 0;
		int i = 0;
		while (i < paramPart.length()) {
			char c = paramPart.charAt(i);
			StringBuilder type;
			switch (c) {
				case 'Z':
					type = new StringBuilder("boolean");
					break;
				case 'B':
					type = new StringBuilder("byte");
					break;
				case 'C':
					type = new StringBuilder("char");
					break;
				case 'S':
					type = new StringBuilder("short");
					break;
				case 'I':
					type = new StringBuilder("int");
					break;
				case 'J':
					type = new StringBuilder("long");
					break;
				case 'F':
					type = new StringBuilder("float");
					break;
				case 'D':
					type = new StringBuilder("double");
					break;
				case 'V':
					type = new StringBuilder("void");
					break;
				case '[':
					arrayNotation++;
					i++;
					continue;
				case 'L':
				case 'T':
					StringBuilder fqn = new StringBuilder();
					i++;
					boolean template = c == 'T';
					while (i < paramPart.length()) {
						c = paramPart.charAt(i);
						if (c == ';') {
							break;
						} else if (c == '/') {
							fqn.append('.');
						} else if (c == '<') {
							i = parseGenerics(parsedParameter, paramPart, ++i, false);
						} else {
							fqn.append(c);
						}
						i++;
					}
					type = new StringBuilder(fqn.toString());
					parsedParameter.template = template;
					break;
				default:
					LOGGER.log(Level.FINE, "Unknown type signature: '" + c + "' in " + paramPart);
					return Collections.emptyList();
			}
			if (arrayNotation > 0) {
				for (int an = 0; an < arrayNotation; an++) {
					type.append("[]");
				}
				arrayNotation = 0;
			}
			parsedParameter.type = type.toString();
			types.add(parsedParameter);
			parsedParameter = new ParsedParameter();
			i++;
		}
		return types;
	}

	private int parseGenerics(ParsedParameter parentParameter, String paramPart, int i, boolean parseUntilColon) {
		StringBuilder fqn = new StringBuilder();
		ParsedParameter lastParameter = new ParsedParameter();
		while (i < paramPart.length()) {
			char c = paramPart.charAt(i);
			if (c == ';') {
				lastParameter.type = fqn.toString();
				if (lastParameter.type.startsWith("+") && lastParameter.type.length() > 1) {
					lastParameter.type = lastParameter.type.substring(1);
					lastParameter.genericWildCard = JApiGenericType.JApiGenericWildCard.EXTENDS;
				}
				if (lastParameter.type.startsWith("-") && lastParameter.type.length() > 1) {
					lastParameter.type = lastParameter.type.substring(1);
					lastParameter.genericWildCard = JApiGenericType.JApiGenericWildCard.SUPER;
				}
				if (lastParameter.type.startsWith("[") && lastParameter.type.length() > 1) {
					lastParameter.type = lastParameter.type.substring(1) + "[]";
				}
				if (lastParameter.type.startsWith("L") && lastParameter.type.length() > 1) {
					lastParameter.type = lastParameter.type.substring(1);
				}
				if (lastParameter.type.startsWith("T") && lastParameter.type.length() > 1) {
					lastParameter.type = lastParameter.type.substring(1);
				}
				if (parseUntilColon) {
					parentParameter.type = lastParameter.type;
					parentParameter.genericTypes = lastParameter.genericTypes;
					parentParameter.genericWildCard = lastParameter.genericWildCard;
					parentParameter.template = lastParameter.template;
					return i;
				}
				fqn = new StringBuilder();
				parentParameter.genericTypes.add(lastParameter);
				lastParameter = new ParsedParameter();
			} else if (c == '*') {
				lastParameter.type = "?";
				lastParameter.genericWildCard = JApiGenericType.JApiGenericWildCard.UNBOUNDED;
				fqn = new StringBuilder();
				parentParameter.genericTypes.add(lastParameter);
				lastParameter = new ParsedParameter();
			} else if (c == '/') {
				fqn.append('.');
			} else if (c == '<') {
				i = parseGenerics(lastParameter, paramPart, ++i, false);
			} else if (c == '>') {
				break;
			} else {
				fqn.append(c);
			}
			i++;
		}
		return i;
	}

	// example: <TEST:Ljapicmp/test/Generics$GenericsParamTest<*Ljava/lang/Short;>;TEST2:Ljava/lang/Integer;>
	private void parseTemplateDefinition(String str) {
		StringBuilder name = new StringBuilder();
		ParsedTemplate currentTemplate = new ParsedTemplate();
		int i = 0;
		while (i < str.length()) {
			char c = str.charAt(i);
			if (c == ':') {
				if (i + 1 < str.length() && str.charAt(i + 1) == ':') {
					i++;
				}
				currentTemplate.name = name.toString();

				ParsedParameter parsedParameter = new ParsedParameter();
				i = parseGenerics(parsedParameter, str, i + 1, true);
				currentTemplate.type = parsedParameter.type;
				currentTemplate.genericTypes = parsedParameter.genericTypes;
				while (i + 1 < str.length() && str.charAt(i + 1) == ':') {
					ParsedParameter parsedInterface = new ParsedParameter();
					i = parseGenerics(parsedInterface, str, i + 2, true);
					currentTemplate.interfaces.add(parsedInterface);
				}
				this.templates.add(currentTemplate);

				name = new StringBuilder();
				currentTemplate = new ParsedTemplate();
			} else {
				name.append(c);
			}
			i++;
		}
	}

	public List<ParsedParameter> getParameters() {
		return parameters;
	}

	public enum DiffType {
		NEW_PARAMS,
		OLD_PARAMS
	}

	public List<JApiParameter> getJApiParameters(JApiClass jApiClass, DiffType diffType) {
		List<JApiParameter> jApiParameters = new ArrayList<>(this.parameters.size());
		for (ParsedParameter parsedParameter : this.parameters) {
			String type = parsedParameter.getType();
			Optional<String> templateName = Optional.empty();
			JApiParameter jApiParameter = null;
			if (parsedParameter.isTemplate()) {
				jApiParameter = resolveTemplate(jApiClass, type, diffType);
			}
			if (jApiParameter == null) {
				jApiParameter = new JApiParameter(type, templateName);
				if (diffType == DiffType.NEW_PARAMS) {
					copyGenericParameters(parsedParameter, jApiParameter.getNewGenericTypes());
				} else if (diffType == DiffType.OLD_PARAMS) {
					copyGenericParameters(parsedParameter, jApiParameter.getOldGenericTypes());
				}
			}
			jApiParameters.add(jApiParameter);
		}
		return jApiParameters;
	}

	private JApiParameter resolveTemplate(JApiClass jApiClass, String templateName, DiffType diffType) {
		JApiParameter jApiParameter = null;
		for (ParsedTemplate parsedTemplate : this.templates) {
			if (parsedTemplate.name.equals(templateName)) {
				jApiParameter = new JApiParameter(parsedTemplate.type, Optional.of(templateName));
				if (diffType == DiffType.NEW_PARAMS) {
					copyGenericParameters(parsedTemplate, jApiParameter.getNewGenericTypes());
				} else if (diffType == DiffType.OLD_PARAMS) {
					copyGenericParameters(parsedTemplate, jApiParameter.getOldGenericTypes());
				}
			}
		}
		if (jApiParameter == null) {
			List<JApiGenericTemplate> genericTemplates = jApiClass.getGenericTemplates();
			for (JApiGenericTemplate jApiGenericTemplate : genericTemplates) {
				if (jApiGenericTemplate.getName().equals(templateName)) {
					if (diffType == DiffType.NEW_PARAMS && jApiGenericTemplate.getNewTypeOptional().isPresent()) {
						jApiParameter = new JApiParameter(jApiGenericTemplate.getNewTypeOptional().get(), Optional.of(templateName));
						jApiParameter.getNewGenericTypes().addAll(jApiGenericTemplate.getNewGenericTypes());
					} else if (diffType == DiffType.OLD_PARAMS && jApiGenericTemplate.getOldTypeOptional().isPresent()) {
						jApiParameter = new JApiParameter(jApiGenericTemplate.getOldTypeOptional().get(), Optional.of(templateName));
						jApiParameter.getOldGenericTypes().addAll(jApiGenericTemplate.getOldGenericTypes());
					}
				}
			}
		}
		return jApiParameter;
	}

	public static void copyGenericParameters(HasGenericTypes hasGenericTypes, List<JApiGenericType> genericTypes) {
		copyGenericParameters(genericTypes, hasGenericTypes.getGenericTypes());
	}

	public static void copyGenericTypeInterfaces(ParsedTemplate parsedTemplate, List<JApiGenericType> genericTypes) {
		copyGenericParameters(genericTypes, parsedTemplate.getInterfaces());
	}

	private static void copyGenericParameters(List<JApiGenericType> genericTypes, List<ParsedParameter> genericTypesAsParsedParameter) {
		if (!genericTypesAsParsedParameter.isEmpty()) {
			for (ParsedParameter genericParam : genericTypesAsParsedParameter) {
				JApiGenericType jApiGenericType = new JApiGenericType(genericParam.getType(), genericParam.getGenericWildCard());
				genericTypes.add(jApiGenericType);
				if (!genericParam.getGenericTypes().isEmpty()) {
					copyGenericParams(genericParam, jApiGenericType);
				}
			}
		}
	}

	private static void copyGenericParams(ParsedParameter genericParam, JApiGenericType jApiGenericType) {
		for (ParsedParameter genericOfGenericParam : genericParam.getGenericTypes()) {
			JApiGenericType jApiGenericTypeInner = new JApiGenericType(genericOfGenericParam.getType(), genericParam.getGenericWildCard());
			jApiGenericType.getGenericTypes().add(jApiGenericTypeInner);
			if (!genericOfGenericParam.getGenericTypes().isEmpty()) {
				copyGenericParams(genericOfGenericParam, jApiGenericTypeInner);
			}
		}
	}

	public static boolean equalGenericTypes(List<JApiGenericType> oldGenericTypes, List<JApiGenericType> newGenericTypes) {
		if (oldGenericTypes.size() != newGenericTypes.size()) {
			return false;
		}
		for (int i = 0; i < oldGenericTypes.size(); i++) {
			if (!oldGenericTypes.get(i).getType().equals(newGenericTypes.get(i).getType()) ||
				!oldGenericTypes.get(i).getGenericWildCard().equals(newGenericTypes.get(i).getGenericWildCard())) {
				return false;
			}
		}
		return true;
	}

	public ParsedParameter getReturnType() {
		return returnType;
	}

	public List<ParsedTemplate> getTemplates() {
		return templates;
	}
}
