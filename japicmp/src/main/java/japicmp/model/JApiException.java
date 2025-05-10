package japicmp.model;

import japicmp.cmp.JarArchiveComparator;
import java.util.function.Predicate;
import javassist.CtClass;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.Optional;

public class JApiException implements JApiHasChangeStatus {
	private final String name;
	private final JApiChangeStatus changeStatus;
	private final boolean checkedException;

	public JApiException(JarArchiveComparator jarArchiveComparator, String name, Optional<CtClass> ctClassOptional, JApiChangeStatus changeStatus) {
		this.name = name;
		this.changeStatus = changeStatus;
		this.checkedException = isCheckedException(ctClassOptional, jarArchiveComparator);
	}

	private boolean isCheckedException(Optional<CtClass> ctClassOptional, JarArchiveComparator jarArchiveComparator) throws OutOfMemoryError {
		Predicate<CtClass> isException = x -> isSubclassOf(Exception.class, x, jarArchiveComparator);
		Predicate<CtClass> isNotRuntimeException = x -> !isSubclassOf(RuntimeException.class, x, jarArchiveComparator);
		return ctClassOptional.filter(isException.and(isNotRuntimeException)).isPresent();
	}

	private static boolean isSubclassOf(Class<?> clazz, CtClass ctClass, JarArchiveComparator jarArchiveComparator) {
		return jarArchiveComparator.loadClass(JarArchiveComparator.ArchiveType.NEW, clazz.getName()).map(ctClass::subclassOf).orElse(false);
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	@XmlAttribute(name = "changeStatus")
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}

	public boolean isCheckedException() {
		return checkedException;
	}
}
