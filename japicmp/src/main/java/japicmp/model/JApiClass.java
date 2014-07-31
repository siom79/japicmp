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
    private final Optional<CtClass> oldClass;
    private final Optional<CtClass> newClass;
    private final List<JApiConstructor> constructors = new LinkedList<>();
    private final List<JApiMethod> methods = new LinkedList<>();
    private final List<JApiImplementedInterface> interfaces = new LinkedList<>();
    private JApiChangeStatus changeStatus;
    private final Type type;
    private final Optional<AccessModifier> accessModifierOld;
    private final Optional<AccessModifier> accessModifierNew;
    private final Optional<Boolean> finalModifierOld;
    private final Optional<Boolean> finalModifierNew;
    private final Optional<Boolean> staticModifierOld;
    private final Optional<Boolean> staticModifierNew;
    private Optional<JApiSuperclass> superclass;

    public enum Type {
        ANNOTATION, INTERFACE, CLASS, ENUM
    }

    public JApiClass(String fullyQualifiedName, Optional<CtClass> oldClass, Optional<CtClass> newClass, JApiChangeStatus changeStatus, Type type) {
        this.changeStatus = changeStatus;
        this.fullyQualifiedName = fullyQualifiedName;
        this.newClass = newClass;
        this.oldClass = oldClass;
        this.type = type;
        this.accessModifierOld = extractAccessModifier(oldClass);
        this.accessModifierNew = extractAccessModifier(newClass);
        this.finalModifierOld = extractFinalModifier(oldClass);
        this.finalModifierNew = extractFinalModifier(newClass);
        this.staticModifierOld = extractStaticFinalModifier(oldClass);
        this.staticModifierNew = extractStaticFinalModifier(newClass);
        this.superclass = extractSuperclass(oldClass, newClass);
        computeInterfaceChanges(this.interfaces, oldClass, newClass);
        evaluateChangeStatus();
    }

	private Optional<JApiSuperclass> extractSuperclass(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		Optional<JApiSuperclass> retVal = Optional.absent();
		if(oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			Optional<CtClass> superclassOldOptional = getSuperclass(oldClass);
			Optional<CtClass> superclassNewOptional = getSuperclass(newClass);
			if(superclassOldOptional.isPresent() && superclassNewOptional.isPresent()) {
				String nameOld = superclassOldOptional.get().getName();
				String nameNew = superclassNewOptional.get().getName();
				JApiSuperclass superclass = new JApiSuperclass(Optional.of(nameOld), Optional.of(nameNew), nameOld.equals(nameNew) ? JApiChangeStatus.UNCHANGED : JApiChangeStatus.MODIFIED);
				retVal = Optional.of(superclass);
			} else if(superclassOldOptional.isPresent() && !superclassNewOptional.isPresent()) {
				JApiSuperclass superclass = new JApiSuperclass(Optional.of(superclassOldOptional.get().getName()), Optional.<String>absent(), JApiChangeStatus.REMOVED);
				retVal = Optional.of(superclass);
			} else if(!superclassOldOptional.isPresent() && superclassNewOptional.isPresent()) {
				JApiSuperclass superclass = new JApiSuperclass(Optional.<String>absent(), Optional.of(superclassNewOptional.get().getName()), JApiChangeStatus.NEW);
				retVal = Optional.of(superclass);
			}
		} else {
			if(oldClassOptional.isPresent()) {
				Optional<CtClass> superclassOldOptional = getSuperclass(oldClassOptional.get());
				if(superclassOldOptional.isPresent()) {
					JApiSuperclass superclass = new JApiSuperclass(Optional.of(superclassOldOptional.get().getName()), Optional.<String>absent(), JApiChangeStatus.REMOVED);
					retVal = Optional.of(superclass);
				}
			} else if(newClassOptional.isPresent()) {
				Optional<CtClass> superclassNewOptional = getSuperclass(newClassOptional.get());
				if(superclassNewOptional.isPresent()) {
					JApiSuperclass superclass = new JApiSuperclass(Optional.<String>absent(), Optional.of(superclassNewOptional.get().getName()), JApiChangeStatus.NEW);
					retVal = Optional.of(superclass);
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
    
    public void evaluateChangeStatus() {
    	if(this.changeStatus == JApiChangeStatus.UNCHANGED) {
    		if(this.accessModifierOld.isPresent() && this.accessModifierNew.isPresent()) {
    			if(this.accessModifierOld.get() != this.accessModifierNew.get()) {
    				this.changeStatus = JApiChangeStatus.MODIFIED;
    			}
    		} else if(this.accessModifierOld.isPresent() && !this.accessModifierNew.isPresent()) {
    			this.changeStatus = JApiChangeStatus.MODIFIED;
    		} else if(!this.accessModifierOld.isPresent() && this.accessModifierNew.isPresent()) {
    			this.changeStatus = JApiChangeStatus.MODIFIED;
    		}
    		if(this.finalModifierOld.isPresent() && this.finalModifierNew.isPresent()) {
    			if(this.finalModifierOld.get() != this.finalModifierNew.get()) {
    				this.changeStatus = JApiChangeStatus.MODIFIED;
    			}
    		} else if(this.finalModifierOld.isPresent() && !this.finalModifierNew.isPresent()) {
    			this.changeStatus = JApiChangeStatus.MODIFIED;
    		} else if(!this.finalModifierOld.isPresent() && this.finalModifierNew.isPresent()) {
    			this.changeStatus = JApiChangeStatus.MODIFIED;
    		}
    		if(this.staticModifierOld.isPresent() && this.staticModifierNew.isPresent()) {
    			if(this.staticModifierOld.get() != this.staticModifierNew.get()) {
    				this.changeStatus = JApiChangeStatus.MODIFIED;
    			}
    		} else if(this.staticModifierOld.isPresent() && !this.staticModifierNew.isPresent()) {
    			this.changeStatus = JApiChangeStatus.MODIFIED;
    		} else if(!this.staticModifierOld.isPresent() && this.staticModifierNew.isPresent()) {
    			this.changeStatus = JApiChangeStatus.MODIFIED;
    		}
    		for(JApiImplementedInterface implementedInterface : interfaces) {
    			if(implementedInterface.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
    				this.changeStatus = JApiChangeStatus.MODIFIED;
    			}
    		}
    		if(this.superclass.isPresent()) {
    			JApiSuperclass jApiSuperclass = this.superclass.get();
    			if(jApiSuperclass.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
    				this.changeStatus = JApiChangeStatus.MODIFIED;
    			}
    		}
    	}
    }
    
    private Optional<Boolean> extractStaticFinalModifier(Optional<CtClass> classOptional) {
		if(classOptional.isPresent()) {
			CtClass ctClass = classOptional.get();
			return Optional.of(Modifier.isStatic(ctClass.getModifiers()));
		}
		return Optional.absent();
	}

	private Optional<Boolean> extractFinalModifier(Optional<CtClass> classOptional) {
		if(classOptional.isPresent()) {
			CtClass ctClass = classOptional.get();
			return Optional.of(Modifier.isFinal(ctClass.getModifiers()));
		}
		return Optional.absent();
	}

	private Optional<AccessModifier> extractAccessModifier(Optional<CtClass> classOptional) {
		if(classOptional.isPresent()) {
			CtClass ctClass = classOptional.get();
			AccessModifier accessModifier = ModifierHelper.translateToModifierLevel(ctClass.getModifiers());
			return Optional.of(accessModifier);
		}
		return Optional.absent();
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

    @XmlElement(name = "superclass")
    public String getSuperclass() {
		return optionalToString(superclass);
	}

	@XmlAttribute
    public Type getType() {
        return type;
    }
    
    @XmlAttribute(name = "accessModifierNew")
    public String getAccessModifierNew() {
    	return optionalToString(accessModifierNew);
    }

    @XmlAttribute(name = "accessModifierOld")
    public String getAccessModifierOld() {
    	return optionalToString(accessModifierOld);
    }

    @XmlAttribute(name = "finalModifierOld")
	public String getFinalModifierOld() {
		return optionalToString(finalModifierOld);
	}

    @XmlAttribute(name = "finalModifierNew")
	public String getFinalModifierNew() {
		return optionalToString(finalModifierNew);
	}

    @XmlAttribute(name = "staticModifierOld")
	public String getStaticModifierOld() {
		return optionalToString(staticModifierOld);
	}

    @XmlAttribute(name = "staticModifierNew")
	public String getStaticModifierNew() {
		return optionalToString(staticModifierNew);
	}
    
    private <T> String optionalToString(Optional<T> optional) {
    	if(optional.isPresent()) {
    		return optional.get().toString();
    	}
    	return "n.a.";
    }
    
    @XmlTransient
	public Optional<Boolean> getFinalModifierOldOptional() {
		return finalModifierOld;
	}

    @XmlTransient
	public Optional<Boolean> getFinalModifierNewOptional() {
		return finalModifierNew;
	}

    @XmlTransient
	public Optional<Boolean> getStaticModifierOldOptional() {
		return staticModifierOld;
	}

    @XmlTransient
	public Optional<Boolean> getStaticModifierNewOptional() {
		return staticModifierNew;
	}
    
    @XmlTransient
    public Optional<JApiSuperclass> getSuperclassOptional() {
		return superclass;
	}
    
    public void setSuperclassOptional(Optional<JApiSuperclass> superclass) {
		this.superclass = superclass;
	}
}
