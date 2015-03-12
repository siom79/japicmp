package japicmp.cmp;

import com.google.common.base.Optional;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiClassType;
import japicmp.util.ClassHelper;
import javassist.CtClass;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClassesComparator {
    private List<JApiClass> classes = new LinkedList<>();
    private JarArchiveComparator jarArchiveComparator;

    public ClassesComparator(JarArchiveComparator jarArchiveComparator) {
        this.jarArchiveComparator = jarArchiveComparator;
    }

    public void compare(List<CtClass> oldClassesArg, List<CtClass> newClassesArg) {
        classes = new LinkedList<>();
        Map<String, CtClass> oldClassesMap = createClassMap(oldClassesArg);
        Map<String, CtClass> newClassesMap = createClassMap(newClassesArg);
        sortIntoLists(oldClassesMap, newClassesMap);
    }

    private void sortIntoLists(Map<String, CtClass> oldClassesMap, Map<String, CtClass> newClassesMap) {
        for(CtClass oldCtClass : oldClassesMap.values()) {
            CtClass newCtClass = newClassesMap.get(oldCtClass.getName());
            if(newCtClass == null) {
                JApiClassType classType = new JApiClassType(Optional.of(ClassHelper.getType(oldCtClass)), Optional.<JApiClassType.ClassType>absent(), JApiChangeStatus.REMOVED);
                classes.add(new JApiClass(this.jarArchiveComparator, oldCtClass.getName(), Optional.of(oldCtClass), Optional.<CtClass>absent(), JApiChangeStatus.REMOVED, classType));
            } else {
                JApiChangeStatus changeStatus = JApiChangeStatus.UNCHANGED;
                JApiClassType.ClassType oldType = ClassHelper.getType(oldCtClass);
                JApiClassType.ClassType newType = ClassHelper.getType(newCtClass);
                if (oldType != newType) {
                    changeStatus = JApiChangeStatus.MODIFIED;
                }
                JApiClassType classType = new JApiClassType(Optional.of(oldType), Optional.of(newType), changeStatus);
                classes.add(new JApiClass(this.jarArchiveComparator, oldCtClass.getName(), Optional.of(oldCtClass), Optional.of(newCtClass), changeStatus, classType));
            }
        }
        for(CtClass newCtClass : newClassesMap.values()) {
            CtClass oldCtClass = oldClassesMap.get(newCtClass.getName());
            if(oldCtClass == null) {
                JApiClassType.ClassType newType = ClassHelper.getType(newCtClass);
                JApiClassType classType = new JApiClassType(Optional.<JApiClassType.ClassType>absent(), Optional.of(newType), JApiChangeStatus.NEW);
                classes.add(new JApiClass(this.jarArchiveComparator, newCtClass.getName(), Optional.<CtClass>absent(), Optional.<CtClass>of(newCtClass), JApiChangeStatus.NEW, classType));
            }
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
