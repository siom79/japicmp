package japicmp.cmp;

import com.google.common.base.Optional;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
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
        for(CtClass ctClass : oldClassesMap.values()) {
            CtClass foundClass = newClassesMap.get(ctClass.getName());
            if(foundClass == null) {
                classes.add(new JApiClass(this.jarArchiveComparator, ctClass.getName(), Optional.<CtClass>of(ctClass), Optional.<CtClass>absent(), JApiChangeStatus.REMOVED, ClassHelper.getType(ctClass)));
            } else {
                classes.add(new JApiClass(this.jarArchiveComparator, ctClass.getName(), Optional.<CtClass>of(ctClass), Optional.<CtClass>of(foundClass), JApiChangeStatus.UNCHANGED, ClassHelper.getType(ctClass)));
            }
        }
        for(CtClass ctClass : newClassesMap.values()) {
            CtClass foundClass = oldClassesMap.get(ctClass.getName());
            if(foundClass == null) {
                classes.add(new JApiClass(this.jarArchiveComparator, ctClass.getName(), Optional.<CtClass>absent(), Optional.<CtClass>of(ctClass), JApiChangeStatus.NEW, ClassHelper.getType(ctClass)));
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
