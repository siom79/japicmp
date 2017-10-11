package japicmp.util;

import japicmp.util.Optional;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.AccessModifier;
import japicmp.model.JApiAttribute;
import japicmp.model.JApiCanBeSynthetic;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiHasAccessModifier;
import japicmp.model.JApiModifier;
import japicmp.model.JApiModifierBase;
import japicmp.model.SyntheticAttribute;
import japicmp.model.SyntheticModifier;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;

import java.lang.reflect.Modifier;

public class ModifierHelper {
	public static final int ACC_BRIDGE = 0x00000040;
	public static final int ACC_SYNTHETIC = 0x00001000;

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
		if (Modifier.isPublic(modifier)) {
			return AccessModifier.PUBLIC;
		} else if (Modifier.isProtected(modifier)) {
			return AccessModifier.PROTECTED;
		} else if (Modifier.isPrivate(modifier)) {
			return AccessModifier.PRIVATE;
		} else {
			return AccessModifier.PACKAGE_PROTECTED;
		}
	}

	public static boolean isNotPrivate(JApiHasAccessModifier jApiHasAccessModifier) {
		JApiModifier<AccessModifier> accessModifier = jApiHasAccessModifier.getAccessModifier();
		if (accessModifier.getOldModifier().isPresent() && accessModifier.getNewModifier().isPresent()) {
			if (accessModifier.getNewModifier().get().getLevel() > AccessModifier.PRIVATE.getLevel() || accessModifier.getOldModifier().get().getLevel() > AccessModifier.PRIVATE.getLevel()) {
				return true;
			}
		} else if (!accessModifier.getOldModifier().isPresent() && accessModifier.getNewModifier().isPresent()) {
			if (accessModifier.getNewModifier().get().getLevel() > AccessModifier.PRIVATE.getLevel()) {
				return true;
			}
		} else if (accessModifier.getOldModifier().isPresent() && !accessModifier.getNewModifier().isPresent()) {
			if (accessModifier.getOldModifier().get().getLevel() > AccessModifier.PRIVATE.getLevel()) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasModifierLevelDecreased(JApiHasAccessModifier hasAccessModifier) {
		JApiModifier<AccessModifier> accessModifier = hasAccessModifier.getAccessModifier();
		if (accessModifier.getOldModifier().isPresent() && accessModifier.getNewModifier().isPresent()) {
			AccessModifier oldModifier = accessModifier.getOldModifier().get();
			AccessModifier newModifier = accessModifier.getNewModifier().get();
			if (newModifier.getLevel() < oldModifier.getLevel()) {
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
				return new JApiModifier<>(Optional.of(oldClassModifier), Optional.of(newClassModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<>(Optional.of(oldClassModifier), Optional.of(newClassModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if (oldClassOptional.isPresent()) {
				CtClass oldClass = oldClassOptional.get();
				T oldClassModifier = callback.getModifierForOld(oldClass);
				return new JApiModifier<>(Optional.of(oldClassModifier), Optional.<T>absent(), JApiChangeStatus.REMOVED);
			}
			if (newClassOptional.isPresent()) {
				CtClass newClass = newClassOptional.get();
				T newClassModifier = callback.getModifierForNew(newClass);
				return new JApiModifier<>(Optional.<T>absent(), Optional.of(newClassModifier), JApiChangeStatus.NEW);
			}
		}
		return new JApiModifier<>(Optional.<T>absent(), Optional.<T>absent(), JApiChangeStatus.UNCHANGED);
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
				return new JApiModifier<>(Optional.of(oldClassModifier), Optional.of(newClassModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<>(Optional.of(oldClassModifier), Optional.of(newClassModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if (oldClassOptional.isPresent()) {
				CtBehavior oldClass = oldClassOptional.get();
				T oldClassModifier = callback.getModifierForOld(oldClass);
				return new JApiModifier<>(Optional.of(oldClassModifier), Optional.<T>absent(), JApiChangeStatus.REMOVED);
			}
			if (newClassOptional.isPresent()) {
				CtBehavior newClass = newClassOptional.get();
				T newClassModifier = callback.getModifierForNew(newClass);
				return new JApiModifier<>(Optional.<T>absent(), Optional.of(newClassModifier), JApiChangeStatus.NEW);
			}
		}
		return new JApiModifier<>(Optional.<T>absent(), Optional.<T>absent(), JApiChangeStatus.UNCHANGED);
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
				return new JApiModifier<>(Optional.of(oldFieldModifier), Optional.of(newFieldModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<>(Optional.of(oldFieldModifier), Optional.of(newFieldModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if (oldFieldOptional.isPresent()) {
				CtField oldField = oldFieldOptional.get();
				T oldFieldModifier = callback.getModifierForOld(oldField);
				return new JApiModifier<>(Optional.of(oldFieldModifier), Optional.<T>absent(), JApiChangeStatus.REMOVED);
			}
			if (newFieldOptional.isPresent()) {
				CtField newField = newFieldOptional.get();
				T newFieldModifier = callback.getModifierForNew(newField);
				return new JApiModifier<>(Optional.<T>absent(), Optional.of(newFieldModifier), JApiChangeStatus.NEW);
			}
		}
		return new JApiModifier<>(Optional.<T>absent(), Optional.<T>absent(), JApiChangeStatus.UNCHANGED);
	}

	public static boolean isBridge(int modifier) {
		return (modifier & ACC_BRIDGE) != 0;
	}

	public static boolean isSynthetic(int modifier) {
		return (modifier & ACC_SYNTHETIC) != 0;
	}

	public static boolean includeSynthetic(JApiCanBeSynthetic jApiCanBeSynthetic, JarArchiveComparatorOptions options) {
		return options.isIncludeSynthetic() || !isSynthetic(jApiCanBeSynthetic);
	}

	public static boolean includeSynthetic(JApiCanBeSynthetic jApiCanBeSynthetic, Options options) {
		return !isSynthetic(jApiCanBeSynthetic) || options.isIncludeSynthetic();
	}

	public static boolean isSynthetic(JApiCanBeSynthetic jApiClass) {
		boolean isSynthetic = false;
		JApiModifier<SyntheticModifier> syntheticModifier = jApiClass.getSyntheticModifier();
		JApiAttribute<SyntheticAttribute> syntheticAttribute = jApiClass.getSyntheticAttribute();
		boolean hasSyntheticModifier = hasSyntheticModifier(syntheticModifier);
		boolean hasSyntheticAttribute = hasSyntheticAttribute(syntheticAttribute);
		if (hasSyntheticModifier || hasSyntheticAttribute) {
			isSynthetic = true;
		}
		return isSynthetic;
	}

	private static boolean hasSyntheticAttribute(JApiAttribute<SyntheticAttribute> syntheticAttribute) {
		boolean hasSyntheticAttribute = false;
		if (syntheticAttribute.getOldAttribute().isPresent() && syntheticAttribute.getNewAttribute().isPresent()) {
			SyntheticAttribute oldAttribute = syntheticAttribute.getOldAttribute().get();
			SyntheticAttribute newAttribute = syntheticAttribute.getNewAttribute().get();
			if (oldAttribute == SyntheticAttribute.SYNTHETIC && newAttribute == SyntheticAttribute.SYNTHETIC) {
				hasSyntheticAttribute = true;
			}
		} else if (syntheticAttribute.getOldAttribute().isPresent()) {
			SyntheticAttribute oldAttribute = syntheticAttribute.getOldAttribute().get();
			if (oldAttribute == SyntheticAttribute.SYNTHETIC) {
				hasSyntheticAttribute = true;
			}
		} else if (syntheticAttribute.getNewAttribute().isPresent()) {
			SyntheticAttribute newAttribute = syntheticAttribute.getNewAttribute().get();
			if (newAttribute == SyntheticAttribute.SYNTHETIC) {
				hasSyntheticAttribute = true;
			}
		}
		return hasSyntheticAttribute;
	}

	private static boolean hasSyntheticModifier(JApiModifier<SyntheticModifier> syntheticModifier) {
		boolean hasSyntheticModifier = false;
		if (syntheticModifier.getOldModifier().isPresent() && syntheticModifier.getNewModifier().isPresent()) {
			SyntheticModifier oldModifier = syntheticModifier.getOldModifier().get();
			SyntheticModifier newModifier = syntheticModifier.getNewModifier().get();
			if (oldModifier == SyntheticModifier.SYNTHETIC && newModifier == SyntheticModifier.SYNTHETIC) {
				hasSyntheticModifier = true;
			}
		} else if (syntheticModifier.getOldModifier().isPresent()) {
			SyntheticModifier oldModifier = syntheticModifier.getOldModifier().get();
			if (oldModifier == SyntheticModifier.SYNTHETIC) {
				hasSyntheticModifier = true;
			}
		} else if (syntheticModifier.getNewModifier().isPresent()) {
			SyntheticModifier newModifier = syntheticModifier.getNewModifier().get();
			if (newModifier == SyntheticModifier.SYNTHETIC) {
				hasSyntheticModifier = true;
			}
		}
		return hasSyntheticModifier;
	}
}
