package japicmp.model;

import japicmp.util.Optional;
import japicmp.util.OptionalHelper;
import japicmp.cmp.JarArchiveComparator;
import japicmp.util.MethodDescriptorParser;
import javassist.CtMethod;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class JApiMethod extends JApiBehavior {
	private final Optional<CtMethod> oldMethod;
	private final Optional<CtMethod> newMethod;
	private final JApiReturnType returnType;

	public JApiMethod(JApiClass jApiClass, String name, JApiChangeStatus changeStatus, Optional<CtMethod> oldMethod, Optional<CtMethod> newMethod, JarArchiveComparator jarArchiveComparator) {
		super(jApiClass, name, oldMethod, newMethod, changeStatus, jarArchiveComparator);
		this.oldMethod = oldMethod;
		this.newMethod = newMethod;
		this.returnType = computeReturnTypeChanges(oldMethod, newMethod);
		this.changeStatus = evaluateChangeStatus(this.changeStatus);
	}

	private JApiChangeStatus evaluateChangeStatus(JApiChangeStatus changeStatus) {
		if (changeStatus == JApiChangeStatus.UNCHANGED) {
			if (this.returnType.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
		}
		return changeStatus;
	}

	private JApiReturnType computeReturnTypeChanges(Optional<CtMethod> oldMethodOptional, Optional<CtMethod> newMethodOptional) {
		JApiReturnType jApiReturnType = new JApiReturnType(JApiChangeStatus.UNCHANGED, Optional.<String>absent(), Optional.<String>absent());
		if (oldMethodOptional.isPresent() && newMethodOptional.isPresent()) {
			String oldReturnType = computeReturnType(oldMethodOptional.get());
			String newReturnType = computeReturnType(newMethodOptional.get());
			JApiChangeStatus changeStatusReturnType = JApiChangeStatus.UNCHANGED;
			if (!oldReturnType.equals(newReturnType)) {
				changeStatusReturnType = JApiChangeStatus.MODIFIED;
			}
			jApiReturnType = new JApiReturnType(changeStatusReturnType, Optional.of(oldReturnType), Optional.of(newReturnType));
		} else {
			if (oldMethodOptional.isPresent()) {
				String oldReturnType = computeReturnType(oldMethodOptional.get());
				jApiReturnType = new JApiReturnType(JApiChangeStatus.REMOVED, Optional.of(oldReturnType), Optional.<String>absent());
			}
			if (newMethodOptional.isPresent()) {
				String newReturnType = computeReturnType(newMethodOptional.get());
				jApiReturnType = new JApiReturnType(JApiChangeStatus.NEW, Optional.<String>absent(), Optional.of(newReturnType));
			}
		}
		return jApiReturnType;
	}

	private String computeReturnType(CtMethod oldMethod) {
		MethodDescriptorParser parser = new MethodDescriptorParser();
		parser.parse(oldMethod.getSignature());
		return parser.getReturnType();
	}

	public boolean hasSameReturnType(JApiMethod otherMethod) {
		boolean haveSameReturnType = false;
		JApiReturnType otherReturnType = otherMethod.getReturnType();
		if (otherReturnType.getChangeStatus() == JApiChangeStatus.UNCHANGED || otherReturnType.getChangeStatus() == JApiChangeStatus.MODIFIED) {
			if (this.returnType.getChangeStatus() == JApiChangeStatus.UNCHANGED || this.returnType.getChangeStatus() == JApiChangeStatus.MODIFIED) {
				if (otherReturnType.getOldReturnType().equals(this.returnType.getOldReturnType()) && otherReturnType.getNewReturnType().equals(this.returnType.getNewReturnType())) {
					haveSameReturnType = true;
				}
			} else if (this.returnType.getChangeStatus() == JApiChangeStatus.NEW) {
				if (otherReturnType.getNewReturnType().equals(this.returnType.getNewReturnType())) {
					haveSameReturnType = true;
				}
			} else if (this.returnType.getChangeStatus() == JApiChangeStatus.REMOVED) {
				if (otherReturnType.getOldReturnType().equals(this.returnType.getOldReturnType())) {
					haveSameReturnType = true;
				}
			}
		} else if (otherReturnType.getChangeStatus() == JApiChangeStatus.NEW) {
			if (this.returnType.getChangeStatus() == JApiChangeStatus.UNCHANGED || this.returnType.getChangeStatus() == JApiChangeStatus.MODIFIED) {
				if (otherReturnType.getNewReturnType().equals(this.returnType.getNewReturnType())) {
					haveSameReturnType = true;
				}
			} else if (this.returnType.getChangeStatus() == JApiChangeStatus.NEW) {
				if (otherReturnType.getNewReturnType().equals(this.returnType.getNewReturnType())) {
					haveSameReturnType = true;
				}
			} else if (this.returnType.getChangeStatus() == JApiChangeStatus.REMOVED) {
				if (otherReturnType.getNewReturnType().equals(this.returnType.getOldReturnType())) {
					haveSameReturnType = true;
				}
			}
		} else {
			if (this.returnType.getChangeStatus() == JApiChangeStatus.UNCHANGED || this.returnType.getChangeStatus() == JApiChangeStatus.MODIFIED) {
				if (otherReturnType.getOldReturnType().equals(this.returnType.getNewReturnType())) {
					haveSameReturnType = true;
				}
			} else if (this.returnType.getChangeStatus() == JApiChangeStatus.NEW) {
				if (otherReturnType.getOldReturnType().equals(this.returnType.getNewReturnType())) {
					haveSameReturnType = true;
				}
			} else if (this.returnType.getChangeStatus() == JApiChangeStatus.REMOVED) {
				if (otherReturnType.getOldReturnType().equals(this.returnType.getOldReturnType())) {
					haveSameReturnType = true;
				}
			}
		}
		return haveSameReturnType;
	}

	public boolean hasSameSignature(JApiMethod jApiMethod) {
		return hasSameReturnType(jApiMethod) && hasSameParameter(jApiMethod);
	}

	@XmlTransient
	public Optional<CtMethod> getNewMethod() {
		return newMethod;
	}

	@XmlTransient
	public Optional<CtMethod> getOldMethod() {
		return oldMethod;
	}

	@XmlElement(name = "returnType")
	public JApiReturnType getReturnType() {
		return returnType;
	}

	public String toString()
	{
		return "JApiMethod [oldMethod="
			+ toString(oldMethod)
			+ ", newMethod="
			+ toString(newMethod)
			+ ", returnType="
			+ returnType
			+ ", getCompatibilityChanges()="
			+ getCompatibilityChanges()
			+ "]";
	}

	public static String toString(Optional<CtMethod> method) {
		if(method == null ) {
			return OptionalHelper.N_A;
		}
		if(method.isPresent()) {
			return method.get().getLongName();
		}
		return OptionalHelper.N_A;
	}


}
