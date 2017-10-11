package japicmp.cmp;

import japicmp.util.Optional;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiClassType;
import japicmp.util.ClassHelper;
import japicmp.util.ModifierHelper;
import javassist.CtClass;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClassesComparator {
	private List<JApiClass> classes = new LinkedList<>();
	private final JarArchiveComparator jarArchiveComparator;
	private final JarArchiveComparatorOptions options;

	public ClassesComparator(JarArchiveComparator jarArchiveComparator, JarArchiveComparatorOptions options) {
		this.jarArchiveComparator = jarArchiveComparator;
		this.options = options;
	}

	public void compare(List<CtClass> oldClassesArg, List<CtClass> newClassesArg) {
		classes = new LinkedList<>();
		Map<String, CtClass> oldClassesMap = createClassMap(oldClassesArg);
		Map<String, CtClass> newClassesMap = createClassMap(newClassesArg);
		sortIntoLists(oldClassesMap, newClassesMap);
	}

	private void sortIntoLists(Map<String, CtClass> oldClassesMap, Map<String, CtClass> newClassesMap) {
		for (CtClass oldCtClass : oldClassesMap.values()) {
			CtClass newCtClass = newClassesMap.get(oldCtClass.getName());
			if (newCtClass == null) {
				JApiClassType classType = new JApiClassType(Optional.of(ClassHelper.getType(oldCtClass)), Optional.<JApiClassType.ClassType>absent(), JApiChangeStatus.REMOVED);
				JApiClass jApiClass = new JApiClass(this.jarArchiveComparator, oldCtClass.getName(), Optional.of(oldCtClass), Optional.<CtClass>absent(), JApiChangeStatus.REMOVED, classType);
				if (includeClass(jApiClass)) {
					classes.add(jApiClass);
				}
			} else {
				JApiChangeStatus changeStatus = JApiChangeStatus.UNCHANGED;
				JApiClassType.ClassType oldType = ClassHelper.getType(oldCtClass);
				JApiClassType.ClassType newType = ClassHelper.getType(newCtClass);
				if (oldType != newType) {
					changeStatus = JApiChangeStatus.MODIFIED;
				}
				JApiClassType classType = new JApiClassType(Optional.of(oldType), Optional.of(newType), changeStatus);
				JApiClass jApiClass = new JApiClass(this.jarArchiveComparator, oldCtClass.getName(), Optional.of(oldCtClass), Optional.of(newCtClass), changeStatus, classType);
				if (includeClass(jApiClass)) {
					classes.add(jApiClass);
				}
			}
		}
		for (CtClass newCtClass : newClassesMap.values()) {
			CtClass oldCtClass = oldClassesMap.get(newCtClass.getName());
			if (oldCtClass == null) {
				JApiClassType.ClassType newType = ClassHelper.getType(newCtClass);
				JApiClassType classType = new JApiClassType(Optional.<JApiClassType.ClassType>absent(), Optional.of(newType), JApiChangeStatus.NEW);
				JApiClass jApiClass = new JApiClass(this.jarArchiveComparator, newCtClass.getName(), Optional.<CtClass>absent(), Optional.of(newCtClass), JApiChangeStatus.NEW, classType);
				if (includeClass(jApiClass)) {
					classes.add(jApiClass);
				}
			}
		}
	}

	private boolean includeClass(JApiClass jApiClass) {
		return ModifierHelper.matchesModifierLevel(jApiClass, options.getAccessModifier());
	}

	private Map<String, CtClass> createClassMap(List<CtClass> oldClassesArg) {
		Map<String, CtClass> oldClassesMap = new HashMap<>();
		for (CtClass ctClass : oldClassesArg) {
			oldClassesMap.put(ctClass.getName(), ctClass);
		}
		return oldClassesMap;
	}

	public List<JApiClass> getClasses() {
		return classes;
	}
}
