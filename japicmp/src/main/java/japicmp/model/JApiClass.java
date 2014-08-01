package japicmp.model;

import com.google.common.base.Optional;
import japicmp.util.ModifierHelper;
import javassist.CtClass;
import javassist.Modifier;
import javassist.NotFoundException;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JApiClass implements JApiHasModifier, JApiHasChangeStatus {
    private final String fullyQualifiedName;
    private final Type type;
    private final Optional<CtClass> oldClass;
    private final Optional<CtClass> newClass;
    private JApiChangeStatus changeStatus;
    private final List<JApiConstructor> constructors = new LinkedList<>();
    private final List<JApiMethod> methods = new LinkedList<>();
    private final List<JApiImplementedInterface> interfaces = new LinkedList<>();
    private final JApiModifier<AccessModifier> accessModifier;
    private final JApiModifier<FinalModifier> finalModifier;
    private final JApiModifier<StaticModifier> staticModifier;
    private final JApiModifier<AbstractModifier> abstractModifier;
    private final JApiSuperclass superclass;

    public enum Type {
        ANNOTATION, INTERFACE, CLASS, ENUM
    }

    public JApiClass(String fullyQualifiedName, Optional<CtClass> oldClass, Optional<CtClass> newClass, JApiChangeStatus changeStatus, Type type) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.newClass = newClass;
        this.oldClass = oldClass;
        this.type = type;
        this.accessModifier = extractAccessModifier(oldClass, newClass);
        this.finalModifier = extractFinalModifier(oldClass, newClass);
        this.staticModifier = extractStaticModifier(oldClass, newClass);
        this.abstractModifier = extractAbstractModifier(oldClass, newClass);
        this.superclass = extractSuperclass(oldClass, newClass);
        computeInterfaceChanges(this.interfaces, oldClass, newClass);
        this.changeStatus = evaluateChangeStatus(changeStatus);
    }

	private JApiSuperclass extractSuperclass(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		JApiSuperclass retVal = null;
		if(oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			Optional<CtClass> superclassOldOptional = getSuperclass(oldClass);
			Optional<CtClass> superclassNewOptional = getSuperclass(newClass);
			if(superclassOldOptional.isPresent() && superclassNewOptional.isPresent()) {
				String nameOld = superclassOldOptional.get().getName();
				String nameNew = superclassNewOptional.get().getName();
				retVal = new JApiSuperclass(Optional.of(nameOld), Optional.of(nameNew), nameOld.equals(nameNew) ? JApiChangeStatus.UNCHANGED : JApiChangeStatus.MODIFIED);
			} else if(superclassOldOptional.isPresent() && !superclassNewOptional.isPresent()) {
				retVal = new JApiSuperclass(Optional.of(superclassOldOptional.get().getName()), Optional.<String>absent(), JApiChangeStatus.REMOVED);
			} else if(!superclassOldOptional.isPresent() && superclassNewOptional.isPresent()) {
				retVal = new JApiSuperclass(Optional.<String>absent(), Optional.of(superclassNewOptional.get().getName()), JApiChangeStatus.NEW);
			} else {
				retVal = new JApiSuperclass(Optional.<String>absent(), Optional.<String>absent(), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if(oldClassOptional.isPresent()) {
				Optional<CtClass> superclassOldOptional = getSuperclass(oldClassOptional.get());
				if(superclassOldOptional.isPresent()) {
					JApiSuperclass superclass = new JApiSuperclass(Optional.of(superclassOldOptional.get().getName()), Optional.<String>absent(), JApiChangeStatus.REMOVED);
					retVal = superclass;
				} else {
					retVal = new JApiSuperclass(Optional.<String>absent(), Optional.<String>absent(), JApiChangeStatus.UNCHANGED);
				}
			} else if(newClassOptional.isPresent()) {
				Optional<CtClass> superclassNewOptional = getSuperclass(newClassOptional.get());
				if(superclassNewOptional.isPresent()) {
					JApiSuperclass superclass = new JApiSuperclass(Optional.<String>absent(), Optional.of(superclassNewOptional.get().getName()), JApiChangeStatus.NEW);
					retVal = superclass;
				} else {
					retVal = new JApiSuperclass(Optional.<String>absent(), Optional.<String>absent(), JApiChangeStatus.UNCHANGED);
				}
			}
		}
		return retVal;
	}
	
	private Optional<CtClass> getSuperclass(CtClass ctClass) {
		try {
			CtClass superClass = ctClass.getSuperclass();
			return Optional.of(superClass);
		} catch (NotFoundException e) {
			return Optional.absent();
		}
	}

	private void computeInterfaceChanges(List<JApiImplementedInterface> interfacesArg, Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
    	if(oldClassOptional.isPresent() && newClassOptional.isPresent()) {
    		CtClass oldClass = oldClassOptional.get();
    		CtClass newClass = newClassOptional.get();
    		Map<String, CtClass> interfaceMapOldClass = buildInterfaceMap(oldClass);
    		Map<String, CtClass> interfaceMapNewClass = buildInterfaceMap(newClass);
    		for(CtClass oldInterface : interfaceMapOldClass.values()) {
    			CtClass ctClassFound = interfaceMapNewClass.get(oldInterface.getName());
    			if(ctClassFound != null) {
    				JApiImplementedInterface jApiClass = new JApiImplementedInterface(oldInterface.getName(), JApiChangeStatus.UNCHANGED);
    				interfacesArg.add(jApiClass);
    			} else {
    				JApiImplementedInterface jApiClass = new JApiImplementedInterface(oldInterface.getName(), JApiChangeStatus.REMOVED);
    				interfacesArg.add(jApiClass);
    			}
    		}
    		for(CtClass newInterface : interfaceMapNewClass.values()) {
    			CtClass ctClassFound = interfaceMapOldClass.get(newInterface.getName());
    			if(ctClassFound == null) {
    				JApiImplementedInterface jApiClass = new JApiImplementedInterface(newInterface.getName(), JApiChangeStatus.NEW);
    				interfacesArg.add(jApiClass);
    			}
    		}
    	} else {
    		if(oldClassOptional.isPresent()) {
    			Map<String, CtClass> interfaceMap = buildInterfaceMap(oldClassOptional.get());
    			for(CtClass ctClass : interfaceMap.values()) {
    				JApiImplementedInterface jApiClass = new JApiImplementedInterface(ctClass.getName(), JApiChangeStatus.REMOVED);
    				interfacesArg.add(jApiClass);
    			}
    		} else if(newClassOptional.isPresent()) {
    			Map<String, CtClass> interfaceMap = buildInterfaceMap(newClassOptional.get());
    			for(CtClass ctClass : interfaceMap.values()) {
    				JApiImplementedInterface jApiClass = new JApiImplementedInterface(ctClass.getName(), JApiChangeStatus.NEW);
    				interfacesArg.add(jApiClass);
    			}
    		}
    	}
	}
    
    private Map<String,CtClass> buildInterfaceMap(CtClass ctClass) {
    	Map<String, CtClass> map = new HashMap<>();
    	try {
			CtClass[] interfaces = ctClass.getInterfaces();
			for(CtClass ctInterface : interfaces) {
				map.put(ctInterface.getName(), ctInterface);
			}
		} catch (NotFoundException e) {

		}
    	return map;
    }

	public void addConstructor(JApiConstructor jApiConstructor) {
        constructors.add(jApiConstructor);
        if(jApiConstructor.getChangeStatus() != JApiChangeStatus.UNCHANGED && this.changeStatus == JApiChangeStatus.UNCHANGED) {
            this.changeStatus = JApiChangeStatus.MODIFIED;
        }
    }

    public void addMethod(JApiMethod jApiMethod) {
        methods.add(jApiMethod);
        if(jApiMethod.getChangeStatus() != JApiChangeStatus.UNCHANGED && this.changeStatus == JApiChangeStatus.UNCHANGED) {
        	this.changeStatus = JApiChangeStatus.MODIFIED;
        }
    }
    
    public JApiChangeStatus evaluateChangeStatus(JApiChangeStatus changeStatus) {
    	if(changeStatus == JApiChangeStatus.UNCHANGED) {
    		if(staticModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
    			changeStatus = JApiChangeStatus.MODIFIED;
    		}
    		if(finalModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
    			changeStatus = JApiChangeStatus.MODIFIED;
    		}
    		if(accessModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
    			changeStatus = JApiChangeStatus.MODIFIED;
    		}
    		if(abstractModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
    			changeStatus = JApiChangeStatus.MODIFIED;
    		}
    		for(JApiImplementedInterface implementedInterface : interfaces) {
    			if(implementedInterface.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
    				changeStatus = JApiChangeStatus.MODIFIED;
    			}
    		}
			if(superclass.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
    	}
    	return changeStatus;
    }
    
    private JApiModifier<StaticModifier> extractStaticModifier(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
    	if(oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			StaticModifier oldClassFinalModifier = Modifier.isStatic(oldClass.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
			StaticModifier newClassFinalModifier = Modifier.isStatic(newClass.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
			if(oldClassFinalModifier != newClassFinalModifier) {
				return new JApiModifier<StaticModifier>(Optional.of(oldClassFinalModifier), Optional.of(newClassFinalModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<StaticModifier>(Optional.of(oldClassFinalModifier), Optional.of(newClassFinalModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if(oldClassOptional.isPresent()) {
				CtClass ctClass = oldClassOptional.get();
				StaticModifier finalModifier = Modifier.isFinal(ctClass.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
				return new JApiModifier<StaticModifier>(Optional.of(finalModifier), Optional.<StaticModifier>absent(), JApiChangeStatus.REMOVED);
			} else {
				CtClass ctClass = newClassOptional.get();
				StaticModifier finalModifier = Modifier.isFinal(ctClass.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
				return new JApiModifier<StaticModifier>(Optional.<StaticModifier>absent(), Optional.of(finalModifier), JApiChangeStatus.NEW);
			}
		}
	}

	private JApiModifier<FinalModifier> extractFinalModifier(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		if(oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			FinalModifier oldClassFinalModifier = Modifier.isFinal(oldClass.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
			FinalModifier newClassFinalModifier = Modifier.isFinal(newClass.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
			if(oldClassFinalModifier != newClassFinalModifier) {
				return new JApiModifier<FinalModifier>(Optional.of(oldClassFinalModifier), Optional.of(newClassFinalModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<FinalModifier>(Optional.of(oldClassFinalModifier), Optional.of(newClassFinalModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if(oldClassOptional.isPresent()) {
				CtClass ctClass = oldClassOptional.get();
				FinalModifier finalModifier = Modifier.isFinal(ctClass.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
				return new JApiModifier<FinalModifier>(Optional.of(finalModifier), Optional.<FinalModifier>absent(), JApiChangeStatus.REMOVED);
			} else {
				CtClass ctClass = newClassOptional.get();
				FinalModifier finalModifier = Modifier.isFinal(ctClass.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
				return new JApiModifier<FinalModifier>(Optional.<FinalModifier>absent(), Optional.of(finalModifier), JApiChangeStatus.NEW);
			}
		}
	}

	private JApiModifier<AccessModifier> extractAccessModifier(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		if(oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			AccessModifier oldClassAccessModifier = ModifierHelper.translateToModifierLevel(oldClass.getModifiers());
			AccessModifier newClassAccessModifier = ModifierHelper.translateToModifierLevel(newClass.getModifiers());
			if(oldClassAccessModifier != newClassAccessModifier) {
				return new JApiModifier<AccessModifier>(Optional.of(oldClassAccessModifier), Optional.of(newClassAccessModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<AccessModifier>(Optional.of(oldClassAccessModifier), Optional.of(newClassAccessModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if(oldClassOptional.isPresent()) {
				CtClass ctClass = oldClassOptional.get();
				AccessModifier accessModifier = ModifierHelper.translateToModifierLevel(ctClass.getModifiers());
				return new JApiModifier<AccessModifier>(Optional.of(accessModifier), Optional.<AccessModifier>absent(), JApiChangeStatus.REMOVED);
			} else {
				CtClass ctClass = newClassOptional.get();
				AccessModifier accessModifier = ModifierHelper.translateToModifierLevel(ctClass.getModifiers());
				return new JApiModifier<AccessModifier>(Optional.<AccessModifier>absent(), Optional.of(accessModifier), JApiChangeStatus.NEW);
			}
		}
	}
	
	private JApiModifier<AbstractModifier> extractAbstractModifier(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		if(oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			AbstractModifier oldClassAccessModifier = Modifier.isAbstract(oldClass.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
			AbstractModifier newClassAccessModifier = Modifier.isAbstract(newClass.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
			if(oldClassAccessModifier != newClassAccessModifier) {
				return new JApiModifier<AbstractModifier>(Optional.of(oldClassAccessModifier), Optional.of(newClassAccessModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<AbstractModifier>(Optional.of(oldClassAccessModifier), Optional.of(newClassAccessModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if(oldClassOptional.isPresent()) {
				CtClass ctClass = oldClassOptional.get();
				AbstractModifier abstractModifier = Modifier.isAbstract(ctClass.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
				return new JApiModifier<AbstractModifier>(Optional.of(abstractModifier), Optional.<AbstractModifier>absent(), JApiChangeStatus.REMOVED);
			} else {
				CtClass ctClass = newClassOptional.get();
				AbstractModifier abstractModifier = Modifier.isAbstract(ctClass.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
				return new JApiModifier<AbstractModifier>(Optional.<AbstractModifier>absent(), Optional.of(abstractModifier), JApiChangeStatus.NEW);
			}
		}
	}

    @XmlAttribute
    public JApiChangeStatus getChangeStatus() {
        return changeStatus;
    }

    @XmlAttribute
    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    @XmlTransient
    public Optional<CtClass> getNewClass() {
        return newClass;
    }

    @XmlTransient
    public Optional<CtClass> getOldClass() {
        return oldClass;
    }

    public void setChangeStatus(JApiChangeStatus changeStatus) {
        this.changeStatus = changeStatus;
    }

    @XmlElement(name = "constructor")
    public List<JApiConstructor> getConstructors() {
        return constructors;
    }

    @XmlElement(name = "method")
    public List<JApiMethod> getMethods() {
        return methods;
    }
    
    @XmlElement(name = "interface")
    public List<JApiImplementedInterface> getInterfaces() {
    	return interfaces;
    }

	@XmlAttribute
    public Type getType() {
        return type;
    }
    
    @XmlAttribute(name = "accessModifierNew")
    public String getAccessModifierNew() {
    	return optionalToString(this.accessModifier.getNewModifier());
    }

    @XmlAttribute(name = "accessModifierOld")
    public String getAccessModifierOld() {
    	return optionalToString(this.accessModifier.getOldModifier());
    }

    @XmlAttribute(name = "finalModifierOld")
	public String getFinalModifierOld() {
		return optionalToString(finalModifier.getOldModifier());
	}

    @XmlAttribute(name = "finalModifierNew")
	public String getFinalModifierNew() {
		return optionalToString(finalModifier.getNewModifier());
	}

    @XmlAttribute(name = "staticModifierOld")
	public String getStaticModifierOld() {
		return optionalToString(staticModifier.getOldModifier());
	}

    @XmlAttribute(name = "staticModifierNew")
	public String getStaticModifierNew() {
		return optionalToString(staticModifier.getNewModifier());
	}
    
    private <T> String optionalToString(Optional<T> optional) {
    	if(optional.isPresent()) {
    		return optional.get().toString();
    	}
    	return "n.a.";
    }
    
    @XmlTransient
    public JApiSuperclass getSuperclass() {
		return superclass;
	}

    @XmlTransient
	@Override
	public JApiModifier<FinalModifier> getFinalModifier() {
		return this.finalModifier;
	}

    @XmlTransient
    @Override
	public JApiModifier<StaticModifier> getStaticModifier() {
		return staticModifier;
	}

    @XmlTransient
	@Override
	public JApiModifier<AccessModifier> getAccessModifier() {
		return this.accessModifier;
	}

    @XmlTransient
	@Override
	public JApiModifier<AbstractModifier> getAbstractModifier() {
		return this.abstractModifier;
	}

	@Override
	public String getAbstractModifierOld() {
		return optionalToString(this.abstractModifier.getOldModifier());
	}

	@Override
	public String getAbstractModifierNew() {
		return optionalToString(this.abstractModifier.getNewModifier());
	}
}
