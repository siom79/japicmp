package japicmp.model;

import japicmp.cmp.JarArchiveComparator;
import japicmp.util.OptionalHelper;
import japicmp.util.SignatureParser;
import javassist.CtMethod;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Optional;

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

	@Override
	public void enhanceGenericTypeToParameters() {
		super.enhanceGenericTypeToParameters(this.jApiClass, this.oldMethod, this.newMethod);
	}

	private JApiReturnType computeReturnTypeChanges(Optional<CtMethod> oldMethodOptional, Optional<CtMethod> newMethodOptional) {
		JApiReturnType jApiReturnType = new JApiReturnType(JApiChangeStatus.UNCHANGED, Optional.empty(), Optional.empty());
		if (oldMethodOptional.isPresent() && newMethodOptional.isPresent()) {
			SignatureParser.ParsedParameter oldReturnType = computeReturnType(oldMethodOptional.get());
			SignatureParser.ParsedParameter newReturnType = computeReturnType(newMethodOptional.get());
			JApiChangeStatus changeStatusReturnType = JApiChangeStatus.UNCHANGED;
			if (!oldReturnType.getType().equals(newReturnType.getType())) {
				changeStatusReturnType = JApiChangeStatus.MODIFIED;
			}
			jApiReturnType = new JApiReturnType(changeStatusReturnType, Optional.of(oldReturnType.getType()), Optional.of(newReturnType.getType()));
			SignatureParser.copyGenericParameters(computeReturnTypeGenericSignature(oldMethodOptional.get(), oldReturnType), jApiReturnType.getOldGenericTypes());
			SignatureParser.copyGenericParameters(computeReturnTypeGenericSignature(newMethodOptional.get(), newReturnType), jApiReturnType.getNewGenericTypes());
		} else {
			if (oldMethodOptional.isPresent()) {
				SignatureParser.ParsedParameter oldReturnType = computeReturnType(oldMethodOptional.get());
				jApiReturnType = new JApiReturnType(JApiChangeStatus.REMOVED, Optional.of(oldReturnType.getType()), Optional.empty());
				SignatureParser.copyGenericParameters(computeReturnTypeGenericSignature(oldMethodOptional.get(), oldReturnType), jApiReturnType.getOldGenericTypes());
			}
			if (newMethodOptional.isPresent()) {
				SignatureParser.ParsedParameter newReturnType = computeReturnType(newMethodOptional.get());
				jApiReturnType = new JApiReturnType(JApiChangeStatus.NEW, Optional.empty(), Optional.of(newReturnType.getType()));
				SignatureParser.copyGenericParameters(computeReturnTypeGenericSignature(newMethodOptional.get(), newReturnType), jApiReturnType.getNewGenericTypes());
			}
		}
		return jApiReturnType;
	}

	private SignatureParser.ParsedParameter computeReturnType(CtMethod ctMethod) {
		SignatureParser parser = new SignatureParser();
		parser.parse(ctMethod.getSignature());
		return parser.getReturnType();
	}

	private SignatureParser.ParsedParameter computeReturnTypeGenericSignature(CtMethod ctMethod, SignatureParser.ParsedParameter rawSignatureParam) {
		SignatureParser parser = new SignatureParser();
		String genericSignature = ctMethod.getGenericSignature();
		if (genericSignature != null) {
			parser.parse(genericSignature);
			return parser.getReturnType();
		}
		return rawSignatureParam;
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

	@Override
	public boolean isSourceCompatible() {
		boolean sourceCompatible = super.isSourceCompatible();
		if (sourceCompatible) {
			sourceCompatible = returnType.isSourceCompatible();
		}
		for (JApiParameter jApiParameter : getParameters()) {
			for (JApiCompatibilityChange compatibilityChange : jApiParameter.getCompatibilityChanges()) {
				if (!compatibilityChange.isSourceCompatible()) {
					sourceCompatible = false;
					break;
				}
			}
		}
		return sourceCompatible;
	}
}
