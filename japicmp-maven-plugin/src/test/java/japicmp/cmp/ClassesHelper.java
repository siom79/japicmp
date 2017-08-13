package japicmp.cmp;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiClass;
import javassist.ClassPool;
import javassist.CtClass;

import java.util.List;

public class ClassesHelper {

	public interface ClassesGenerator {
		List<CtClass> createOldClasses(ClassPool classPool) throws Exception;

		List<CtClass> createNewClasses(ClassPool classPool) throws Exception;
	}

	public static class CompareClassesResult {
		final List<JApiClass> jApiClasses;
		final JarArchiveComparator jarArchiveComparator;

		public CompareClassesResult(List<JApiClass> jApiClasses, JarArchiveComparator jarArchiveComparator) {
			this.jApiClasses = jApiClasses;
			this.jarArchiveComparator = jarArchiveComparator;
		}

		public List<JApiClass> getjApiClasses() {
			return jApiClasses;
		}

		public JarArchiveComparator getJarArchiveComparator() {
			return jarArchiveComparator;
		}
	}

	public static CompareClassesResult compareClasses(JarArchiveComparatorOptions options, ClassesGenerator classesGenerator) throws Exception {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		ClassPool classPool = jarArchiveComparator.getCommonClassPool();
		List<CtClass> oldClasses = classesGenerator.createOldClasses(classPool);
		List<CtClass> newClasses = classesGenerator.createNewClasses(classPool);
		List<JApiClass> jApiClasses = jarArchiveComparator.compareClassLists(options, oldClasses, newClasses);
		return new CompareClassesResult(jApiClasses, jarArchiveComparator);
	}
}
