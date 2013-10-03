package japicmp.cmp;

import com.google.common.base.Optional;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.util.ModifierHelper;
import javassist.CtClass;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClassesComparator {
    private List<JApiClass> classes = new LinkedList<JApiClass>();

    public void compare(List<CtClass> oldClassesArg, List<CtClass> newClassesArg) {
        classes = new LinkedList<JApiClass>();
        Map<String, CtClass> oldClassesMap = createClassMap(oldClassesArg);
        Map<String, CtClass> newClassesMap = createClassMap(newClassesArg);
        sortIntoLists(oldClassesMap, newClassesMap);
    }

    private void sortIntoLists(Map<String, CtClass> oldClassesMap, Map<String, CtClass> newClassesMap) {
        for(CtClass ctClass : oldClassesMap.values()) {
            CtClass foundClass = newClassesMap.get(ctClass.getName());
            if(foundClass == null) {
                classes.add(new JApiClass(ctClass.getName(), Optional.<CtClass>of(ctClass), Optional.<CtClass>absent(), JApiChangeStatus.REMOVED, getType(ctClass), Optional.of(ModifierHelper.translateToModifierLevel(ctClass.getModifiers())), Optional.<AccessModifier>absent()));
            } else {
                classes.add(new JApiClass(ctClass.getName(), Optional.<CtClass>of(ctClass), Optional.<CtClass>of(foundClass), JApiChangeStatus.UNCHANGED, getType(ctClass), Optional.of(ModifierHelper.translateToModifierLevel(ctClass.getModifiers())), Optional.of(ModifierHelper.translateToModifierLevel(foundClass.getModifiers()))));
            }
        }
        for(CtClass ctClass : newClassesMap.values()) {
            CtClass foundClass = oldClassesMap.get(ctClass.getName());
            if(foundClass == null) {
                classes.add(new JApiClass(ctClass.getName(), Optional.<CtClass>absent(), Optional.<CtClass>of(ctClass), JApiChangeStatus.NEW, getType(ctClass), Optional.<AccessModifier>absent(), Optional.of(ModifierHelper.translateToModifierLevel(ctClass.getModifiers()))));
            }
        }
    }

    private JApiClass.Type getType(CtClass ctClass) {
        if(ctClass.isAnnotation()) {
            return JApiClass.Type.ANNOTATION;
        } else if(ctClass.isEnum()) {
            return JApiClass.Type.ENUM;
        } else if(ctClass.isInterface()) {
            return JApiClass.Type.INTERFACE;
        } else {
            return JApiClass.Type.CLASS;
        }
    }

    private Map<String, CtClass> createClassMap(List<CtClass> oldClassesArg) {
        Map<String, CtClass> oldClassesMap = new HashMap<String, CtClass>();
        for(CtClass ctClass : oldClassesArg) {
            oldClassesMap.put(ctClass.getName(), ctClass);
        }
        return oldClassesMap;
    }

    public List<JApiClass> getClasses() {
        return classes;
    }
}
