package japicmp.cmp;

import com.google.common.base.Optional;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;
import japicmp.model.JApiParameter;
import japicmp.util.ModifierHelper;
import japicmp.util.SignatureParser;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;

import java.util.HashMap;
import java.util.Map;

public class ClassComparator {
    private final JarArchiveComparatorOptions options;

    public ClassComparator(JarArchiveComparatorOptions options) {
        this.options = options;
    }

    public void compare(JApiClass jApiClass) {
        Map<String, CtMethod> oldMethodsMap = createMethodMap(jApiClass.getOldClass());
        Map<String, CtMethod> newMethodsMap = createMethodMap(jApiClass.getNewClass());
        sortMethodsIntoLists(oldMethodsMap, newMethodsMap, jApiClass);
    }

    private void sortMethodsIntoLists(Map<String, CtMethod> oldMethodsMap, Map<String, CtMethod> newMethodsMap, JApiClass jApiClass) {
        SignatureParser signatureParser = new SignatureParser();
        for (CtMethod ctMethod : oldMethodsMap.values()) {
            String longName = ctMethod.getLongName();
            signatureParser.parse(ctMethod.getSignature());
            CtMethod foundMethod = newMethodsMap.get(longName);
            if (foundMethod == null) {
                JApiMethod jApiMethod = new JApiMethod(ctMethod.getName(), JApiChangeStatus.REMOVED, Optional.of(ctMethod), Optional.<CtMethod>absent(), signatureParser.getReturnType());
                addParametersToMethod(signatureParser, jApiMethod);
                jApiClass.addMethod(jApiMethod);
            } else {
                JApiMethod jApiMethod = new JApiMethod(ctMethod.getName(), JApiChangeStatus.UNCHANGED, Optional.of(ctMethod), Optional.of(foundMethod), signatureParser.getReturnType());
                addParametersToMethod(signatureParser, jApiMethod);
                jApiClass.addMethod(jApiMethod);
            }
        }
        for (CtMethod ctMethod : newMethodsMap.values()) {
            String longName = ctMethod.getLongName();
            signatureParser.parse(ctMethod.getSignature());
            CtMethod foundMethod = oldMethodsMap.get(longName);
            if (foundMethod == null) {
                JApiMethod jApiMethod = new JApiMethod(ctMethod.getName(), JApiChangeStatus.NEW, Optional.<CtMethod>absent(), Optional.of(ctMethod), signatureParser.getReturnType());
                addParametersToMethod(signatureParser, jApiMethod);
                jApiClass.addMethod(jApiMethod);
            }
        }
    }

    private void addParametersToMethod(SignatureParser signatureParser, JApiMethod jApiMethod) {
        for (String param : signatureParser.getParameters()) {
            jApiMethod.addParameter(new JApiParameter(param));
        }
    }

    private Map<String, CtMethod> createMethodMap(Optional<CtClass> ctClass) {
        Map<String, CtMethod> methods = new HashMap<String, CtMethod>();
        if (ctClass.isPresent()) {
            for (CtMethod ctMethod : ctClass.get().getDeclaredMethods()) {
                if(ModifierHelper.matchesModifierLevel(ctMethod.getModifiers(), options.getModifierLevel())) {
                    methods.put(ctMethod.getLongName(), ctMethod);
                }
            }
        }
        return methods;
    }
    
    private Map<String, CtBehavior> createConstructorMap(Optional<CtClass> ctClass) {
        Map<String, CtBehavior> methods = new HashMap<String, CtBehavior>();
        if (ctClass.isPresent()) {
            for(CtConstructor ctConstructor : ctClass.get().getDeclaredConstructors()) {
            	if(ModifierHelper.matchesModifierLevel(ctConstructor.getModifiers(), options.getModifierLevel())) {
            		methods.put(ctConstructor.getLongName(), ctConstructor);
            	}
            }
        }
        return methods;
    }
}
