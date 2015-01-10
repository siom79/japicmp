package japicmp.util;

import japicmp.model.AccessModifier;
import japicmp.model.JApiHasAccessModifier;
import japicmp.model.JApiModifier;

import java.lang.reflect.Modifier;

public class ModifierHelper {

    private ModifierHelper() {

    }
    
    public static boolean matchesModifierLevel(AccessModifier modifierLevelOfElement, AccessModifier modifierLevel) {
    	return (modifierLevelOfElement.getLevel() >= modifierLevel.getLevel());
    }

    public static boolean matchesModifierLevel(int modifierOfElement, AccessModifier modifierLevel) {
        AccessModifier modifierLevelOfElement = translateToModifierLevel(modifierOfElement);
        return matchesModifierLevel(modifierLevelOfElement, modifierLevel);
    }

    public static AccessModifier translateToModifierLevel(int modifier) {
        if(Modifier.isPublic(modifier)) {
            return AccessModifier.PUBLIC;
        } else if(Modifier.isProtected(modifier)) {
            return AccessModifier.PROTECTED;
        } else if(Modifier.isPrivate(modifier)) {
            return AccessModifier.PRIVATE;
        } else {
            return AccessModifier.PACKAGE_PROTECTED;
        }
    }
    
	public static boolean isNotPrivate(JApiHasAccessModifier jApiHasAccessModifier) {
		JApiModifier<AccessModifier> accessModifier = jApiHasAccessModifier.getAccessModifier();
		if(accessModifier.getOldModifier().isPresent() && accessModifier.getNewModifier().isPresent()) {
			if(accessModifier.getNewModifier().get().getLevel() > AccessModifier.PRIVATE.getLevel() || accessModifier.getOldModifier().get().getLevel() > AccessModifier.PRIVATE.getLevel()) {
				return true;
			}
		} else if(!accessModifier.getOldModifier().isPresent() && accessModifier.getNewModifier().isPresent()) {
			if(accessModifier.getNewModifier().get().getLevel() > AccessModifier.PRIVATE.getLevel()) {
				return true;
			}
		} else if(accessModifier.getOldModifier().isPresent() && !accessModifier.getNewModifier().isPresent()) {
			if(accessModifier.getOldModifier().get().getLevel() > AccessModifier.PRIVATE.getLevel()) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasModifierLevelDecreased(JApiHasAccessModifier hasAccessModifier) {
		JApiModifier<AccessModifier> accessModifier = hasAccessModifier.getAccessModifier();
		if(accessModifier.getOldModifier().isPresent() && accessModifier.getNewModifier().isPresent()) {
			AccessModifier oldModifier = accessModifier.getOldModifier().get();
			AccessModifier newModifier = accessModifier.getNewModifier().get();
			if(newModifier.getLevel() < oldModifier.getLevel()) {
				return true;
			}
		}
		return false;
	}

	public static boolean matchesModifierLevel(JApiHasAccessModifier hasAccessModifier, AccessModifier accessModifierParam) {
		JApiModifier<AccessModifier> accessModifier = hasAccessModifier.getAccessModifier();
		if (accessModifier.getOldModifier().isPresent()) {
			AccessModifier oldModifier = accessModifier.getOldModifier().get();
			if (matchesModifierLevel(oldModifier, accessModifierParam)) {
				return true;
			}
		}
		if (accessModifier.getNewModifier().isPresent()) {
			AccessModifier newModifier = accessModifier.getNewModifier().get();
			if (matchesModifierLevel(newModifier, accessModifierParam)) {
				return true;
			}
		}
		return false;
	}
}
