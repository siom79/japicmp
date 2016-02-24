package japicmp.model;

import com.google.common.base.Optional;
import japicmp.cmp.JarArchiveComparator;
import javassist.CtClass;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.List;

public class JApiException implements JApiHasChangeStatus {
	private final String name;
	private final JApiChangeStatus changeStatus;
	private final boolean checkedException;

	public JApiException(JarArchiveComparator jarArchiveComparator, String name, Optional<CtClass> ctClassOptional, JApiChangeStatus changeStatus) {
		this.name = name;
		this.changeStatus = changeStatus;
		this.checkedException = isCheckedException(ctClassOptional, jarArchiveComparator);
	}

	private boolean isCheckedException(Optional<CtClass> ctClassOptional, JarArchiveComparator jarArchiveComparator) {
		boolean checked = false;
		Optional<CtClass> exceptionOptional = jarArchiveComparator.loadClass(JarArchiveComparator.ArchiveType.NEW, "java.lang.Exception");
		if (exceptionOptional.isPresent()) {
			CtClass ctClass = ctClassOptional.get();
			if (ctClass.subclassOf(exceptionOptional.get())) {
				checked = true;
			}
		}
		return checked;
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
