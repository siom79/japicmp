package japicmp.util;

import com.google.common.base.Optional;
import japicmp.model.*;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;

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

	public interface ExtractModifierFromClassCallback<T extends JApiModifierBase> {
		T getModifierForOld(CtClass oldClass);
		T getModifierForNew(CtClass newClass);
	}

	public static <T extends JApiModifierBase> JApiModifier<T> extractModifierFromClass(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional, ExtractModifierFromClassCallback<T> callback) {
		if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			T oldClassModifier = callback.getModifierForOld(oldClass);
			T newClassModifier = callback.getModifierForNew(newClass);
			if (oldClassModifier != newClassModifier) {
				return new JApiModifier<T>(Optional.of(oldClassModifier), Optional.of(newClassModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<T>(Optional.of(oldClassModifier), Optional.of(newClassModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if (oldClassOptional.isPresent()) {
				CtClass oldClass = oldClassOptional.get();
				T oldClassModifier = callback.getModifierForOld(oldClass);
				return new JApiModifier<T>(Optional.of(oldClassModifier), Optional.<T>absent(), JApiChangeStatus.REMOVED);
			}
			if (newClassOptional.isPresent()) {
				CtClass newClass = newClassOptional.get();
				T newClassModifier = callback.getModifierForNew(newClass);
				return new JApiModifier<T>(Optional.<T>absent(), Optional.of(newClassModifier), JApiChangeStatus.NEW);
			}
		}
		return new JApiModifier<T>(Optional.<T>absent(), Optional.<T>absent(), JApiChangeStatus.UNCHANGED);
	}

	public interface ExtractModifierFromBehaviorCallback<T extends JApiModifierBase> {
		T getModifierForOld(CtBehavior oldClass);
		T getModifierForNew(CtBehavior newClass);
	}

	public static <T extends JApiModifierBase> JApiModifier<T> extractModifierFromBehavior(Optional<? extends CtBehavior> oldClassOptional, Optional<? extends CtBehavior> newClassOptional, ExtractModifierFromBehaviorCallback<T> callback) {
		if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtBehavior oldClass = oldClassOptional.get();
			CtBehavior newClass = newClassOptional.get();
			T oldClassModifier = callback.getModifierForOld(oldClass);
			T newClassModifier = callback.getModifierForNew(newClass);
			if (oldClassModifier != newClassModifier) {
				return new JApiModifier<T>(Optional.of(oldClassModifier), Optional.of(newClassModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<T>(Optional.of(oldClassModifier), Optional.of(newClassModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if (oldClassOptional.isPresent()) {
				CtBehavior oldClass = oldClassOptional.get();
				T oldClassModifier = callback.getModifierForOld(oldClass);
				return new JApiModifier<T>(Optional.of(oldClassModifier), Optional.<T>absent(), JApiChangeStatus.REMOVED);
			}
			if (newClassOptional.isPresent()) {
				CtBehavior newClass = newClassOptional.get();
				T newClassModifier = callback.getModifierForNew(newClass);
				return new JApiModifier<T>(Optional.<T>absent(), Optional.of(newClassModifier), JApiChangeStatus.NEW);
			}
		}
		return new JApiModifier<T>(Optional.<T>absent(), Optional.<T>absent(), JApiChangeStatus.UNCHANGED);
	}

	public interface ExtractModifierFromFieldCallback<T extends JApiModifierBase> {
		T getModifierForOld(CtField oldField);
		T getModifierForNew(CtField newField);
	}

	public static <T extends JApiModifierBase> JApiModifier<T> extractModifierFromField(Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional, ExtractModifierFromFieldCallback<T> callback) {
		if (oldFieldOptional.isPresent() && newFieldOptional.isPresent()) {
			CtField oldField = oldFieldOptional.get();
			CtField newField = newFieldOptional.get();
			T oldFieldModifier = callback.getModifierForOld(oldField);
			T newFieldModifier = callback.getModifierForNew(newField);
			if (oldFieldModifier != newFieldModifier) {
				return new JApiModifier<T>(Optional.of(oldFieldModifier), Optional.of(newFieldModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<T>(Optional.of(oldFieldModifier), Optional.of(newFieldModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if (oldFieldOptional.isPresent()) {
				CtField oldField = oldFieldOptional.get();
				T oldFieldModifier = callback.getModifierForOld(oldField);
				return new JApiModifier<T>(Optional.of(oldFieldModifier), Optional.<T>absent(), JApiChangeStatus.REMOVED);
			}
			if (newFieldOptional.isPresent()) {
				CtField newField = newFieldOptional.get();
				T newFieldModifier = callback.getModifierForNew(newField);
				return new JApiModifier<T>(Optional.<T>absent(), Optional.of(newFieldModifier), JApiChangeStatus.NEW);
			}
		}
		return new JApiModifier<T>(Optional.<T>absent(), Optional.<T>absent(), JApiChangeStatus.UNCHANGED);
	}

	public static boolean isBridge(int modifier) {
		return (modifier & 0x00000040) != 0;
	}

	public static boolean isSynthetic(int modifier) {
		return (modifier & 0x00001000) != 0;
	}
}
