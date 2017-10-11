package japicmp.model;

import japicmp.util.Optional;
import japicmp.cmp.JarArchiveComparator;
import javassist.CtClass;

import javax.xml.bind.annotation.XmlAttribute;

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
		boolean checkedException = false;
		if (ctClassOptional.isPresent()) {
			boolean subClassOfException = false;
			CtClass ctClass = ctClassOptional.get();
			Optional<CtClass> exceptionOptional = jarArchiveComparator.loadClass(JarArchiveComparator.ArchiveType.NEW, Exception.class.getName());
			if (exceptionOptional.isPresent()) {
				if (ctClass.subclassOf(exceptionOptional.get())) {
					subClassOfException = true;
				}
			}
			if (subClassOfException) {
				Optional<CtClass> runtimeExceptionOptional = jarArchiveComparator.loadClass(JarArchiveComparator.ArchiveType.NEW, RuntimeException.class.getName());
				if (runtimeExceptionOptional.isPresent()) {
					if (!ctClass.subclassOf(runtimeExceptionOptional.get())) {
						checkedException = true;
					}
				}
			}
		}
		return checkedException;
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
