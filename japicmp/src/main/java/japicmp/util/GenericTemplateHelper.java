package japicmp.util;

import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiGenericTemplate;
import japicmp.model.JApiGenericType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericTemplateHelper {

	public interface SignatureParserCallback {
		boolean isOldAndNewPresent();
		boolean isOldPresent();
		boolean isNewPresent();
		SignatureParser oldSignatureParser();
		SignatureParser newSignatureParser();
	}

	public static List<JApiGenericTemplate> computeGenericTemplateChanges(SignatureParserCallback callback) {
		List<JApiGenericTemplate> genericTemplates = new ArrayList<>();
		if (callback.isOldAndNewPresent()) {
			SignatureParser oldSignatureParser = callback.oldSignatureParser();
			SignatureParser newSignatureParser = callback.newSignatureParser();
			Map<String, SignatureParser.ParsedTemplate> oldGenericTemplatesMap = new HashMap<>();
			for (SignatureParser.ParsedTemplate parsedTemplate : oldSignatureParser.getTemplates()) {
				if (!oldGenericTemplatesMap.containsKey(parsedTemplate.getName())) {
					oldGenericTemplatesMap.put(parsedTemplate.getName(), parsedTemplate);
				}
			}
			for (SignatureParser.ParsedTemplate newParsedTemplate : newSignatureParser.getTemplates()) {
				SignatureParser.ParsedTemplate oldParsedTemplate = oldGenericTemplatesMap.get(newParsedTemplate.getName());
				if (oldParsedTemplate != null) {
					JApiChangeStatus changeStatus = JApiChangeStatus.UNCHANGED;
					if (!newParsedTemplate.getType().equals(oldParsedTemplate.getType())) {
						changeStatus = JApiChangeStatus.MODIFIED;
					}
					JApiGenericTemplate jApiGenericTemplate = new JApiGenericTemplate(changeStatus, newParsedTemplate.getName(), japicmp.util.Optional.of(oldParsedTemplate.getType()), japicmp.util.Optional.of(newParsedTemplate.getType()));
					SignatureParser.copyGenericParameters(oldParsedTemplate, jApiGenericTemplate.getOldGenericTypes());
					SignatureParser.copyGenericParameters(newParsedTemplate, jApiGenericTemplate.getNewGenericTypes());
					SignatureParser.copyGenericTypeInterfaces(oldParsedTemplate, jApiGenericTemplate.getOldInterfaceTypes());
					SignatureParser.copyGenericTypeInterfaces(newParsedTemplate, jApiGenericTemplate.getNewInterfaceTypes());
					genericTemplates.add(jApiGenericTemplate);
					oldGenericTemplatesMap.remove(newParsedTemplate.getName());
				} else {
					JApiGenericTemplate jApiGenericTemplate = new JApiGenericTemplate(JApiChangeStatus.NEW, newParsedTemplate.getName(), japicmp.util.Optional.absent(), japicmp.util.Optional.of(newParsedTemplate.getType()));
					SignatureParser.copyGenericParameters(newParsedTemplate, jApiGenericTemplate.getNewGenericTypes());
					SignatureParser.copyGenericTypeInterfaces(newParsedTemplate, jApiGenericTemplate.getNewInterfaceTypes());
					genericTemplates.add(jApiGenericTemplate);
				}
			}
			for (SignatureParser.ParsedTemplate oldParsedTemplate : oldGenericTemplatesMap.values()) {
				JApiGenericTemplate jApiGenericTemplate = new JApiGenericTemplate(JApiChangeStatus.REMOVED, oldParsedTemplate.getName(), japicmp.util.Optional.of(oldParsedTemplate.getType()), japicmp.util.Optional.absent());
				SignatureParser.copyGenericParameters(oldParsedTemplate, jApiGenericTemplate.getOldGenericTypes());
				SignatureParser.copyGenericTypeInterfaces(oldParsedTemplate, jApiGenericTemplate.getOldInterfaceTypes());
				genericTemplates.add(jApiGenericTemplate);
			}
		} else if (callback.isNewPresent()) {
			SignatureParser newSignatureParser = callback.newSignatureParser();
			for (SignatureParser.ParsedTemplate newParsedTemplate : newSignatureParser.getTemplates()) {
				JApiGenericTemplate jApiGenericTemplate = new JApiGenericTemplate(JApiChangeStatus.NEW, newParsedTemplate.getName(), japicmp.util.Optional.absent(), japicmp.util.Optional.of(newParsedTemplate.getType()));
				SignatureParser.copyGenericParameters(newParsedTemplate, jApiGenericTemplate.getNewGenericTypes());
				SignatureParser.copyGenericTypeInterfaces(newParsedTemplate, jApiGenericTemplate.getNewInterfaceTypes());
				genericTemplates.add(jApiGenericTemplate);
			}
		} else if (callback.isOldPresent()) {
			SignatureParser oldSignatureParser = callback.oldSignatureParser();
			for (SignatureParser.ParsedTemplate oldParsedTemplate : oldSignatureParser.getTemplates()) {
				JApiGenericTemplate jApiGenericTemplate = new JApiGenericTemplate(JApiChangeStatus.REMOVED, oldParsedTemplate.getName(), japicmp.util.Optional.of(oldParsedTemplate.getType()), japicmp.util.Optional.absent());
				SignatureParser.copyGenericParameters(oldParsedTemplate, jApiGenericTemplate.getOldGenericTypes());
				SignatureParser.copyGenericTypeInterfaces(oldParsedTemplate, jApiGenericTemplate.getOldInterfaceTypes());
				genericTemplates.add(jApiGenericTemplate);
			}
		}
		return genericTemplates;
	}

	public static boolean haveGenericTemplateInterfacesChanges(List<JApiGenericType> oldInterfaces, List<JApiGenericType> newInterfaces) {
		if (oldInterfaces.size() != newInterfaces.size()) {
			return true;
		}
		for (int i = 0; i < oldInterfaces.size(); i++) {
			if (!oldInterfaces.get(i).getType().equals(newInterfaces.get(i).getType())) {
				return true;
			}
		}
		return false;
	}
}
