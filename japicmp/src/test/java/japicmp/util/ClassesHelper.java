package japicmp.util;

import japicmp.cmp.ClassesComparator;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiClass;
import javassist.ClassPool;
import javassist.CtClass;

import java.util.List;

public class ClassesHelper {

	public interface ClassesGenerator {
		List<CtClass> createOldClasses(ClassPool classPool);

		List<CtClass> createNewClasses(ClassPool classPool);
	}

	public static List<JApiClass> compareClasses(JarArchiveComparatorOptions options, ClassesGenerator classesGenerator) throws Exception {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		ClassPool classPool = jarArchiveComparator.getCommonClassPool();
		ClassesComparator classesComparator = new ClassesComparator(jarArchiveComparator, options);
		List<CtClass> oldClasses = classesGenerator.createOldClasses(classPool);
		List<CtClass> newClasses = classesGenerator.createNewClasses(classPool);
		classesComparator.compare(oldClasses, newClasses);
		return classesComparator.getClasses();
	}
}
