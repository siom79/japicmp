package japicmp.util;

import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.AbstractModifier;
import japicmp.model.AccessModifier;
import japicmp.model.BridgeModifier;
import japicmp.model.FinalModifier;
import japicmp.model.JApiAttribute;
import japicmp.model.JApiCanBeSynthetic;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiHasAccessModifier;
import japicmp.model.JApiModifier;
import japicmp.model.JApiModifierBase;
import japicmp.model.StaticModifier;
import japicmp.model.SyntheticAttribute;
import japicmp.model.SyntheticModifier;
import japicmp.model.TransientModifier;
import japicmp.model.VolatileModifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;

import java.lang.reflect.Modifier;

public class ModifierHelper {
	public static final int ACC_BRIDGE = 0x00000040;
	public static final int ACC_SYNTHETIC = 0x00001000;

	private static final List<? extends JApiModifierBase> IGNORED_MODIFIERS = Arrays.asList(
		AbstractModifier.NON_ABSTRACT,
		AccessModifier.PACKAGE_PROTECTED,
		BridgeModifier.NON_BRIDGE,
		FinalModifier.NON_FINAL,
		StaticModifier.NON_STATIC,
		SyntheticModifier.NON_SYNTHETIC,
		TransientModifier.NON_TRANSIENT,
		VolatileModifier.NON_VOLATILE
	);

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
		return accessModifier.getOldModifier().map(ModifierHelper::isNotPrivate).orElse(false)
			|| accessModifier.getNewModifier().map(ModifierHelper::isNotPrivate).orElse(false);
	}

	private static boolean isNotPrivate(AccessModifier accessModifier) {
		return accessModifier != AccessModifier.PRIVATE;
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
				return new JApiModifier<>(Optional.of(oldClassModifier), Optional.<T>empty(), JApiChangeStatus.REMOVED);
			}
			if (newClassOptional.isPresent()) {
				CtClass newClass = newClassOptional.get();
				T newClassModifier = callback.getModifierForNew(newClass);
				return new JApiModifier<>(Optional.<T>empty(), Optional.of(newClassModifier), JApiChangeStatus.NEW);
			}
		}
		return new JApiModifier<>(Optional.<T>empty(), Optional.<T>empty(), JApiChangeStatus.UNCHANGED);
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
				return new JApiModifier<>(Optional.of(oldClassModifier), Optional.<T>empty(), JApiChangeStatus.REMOVED);
			}
			if (newClassOptional.isPresent()) {
				CtBehavior newClass = newClassOptional.get();
				T newClassModifier = callback.getModifierForNew(newClass);
				return new JApiModifier<>(Optional.<T>empty(), Optional.of(newClassModifier), JApiChangeStatus.NEW);
			}
		}
		return new JApiModifier<>(Optional.<T>empty(), Optional.<T>empty(), JApiChangeStatus.UNCHANGED);
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
				return new JApiModifier<>(Optional.of(oldFieldModifier), Optional.<T>empty(), JApiChangeStatus.REMOVED);
			}
			if (newFieldOptional.isPresent()) {
				CtField newField = newFieldOptional.get();
				T newFieldModifier = callback.getModifierForNew(newField);
				return new JApiModifier<>(Optional.<T>empty(), Optional.of(newFieldModifier), JApiChangeStatus.NEW);
			}
		}
		return new JApiModifier<>(Optional.<T>empty(), Optional.<T>empty(), JApiChangeStatus.UNCHANGED);
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

	public static Optional<String> getOldModifierName(JApiModifier<? extends Enum<? extends Enum<?>>> modifier) {
		return getModifierName(modifier.getOldModifier());
	}

	public static Optional<String> getNewModifierName(JApiModifier<? extends Enum<? extends Enum<?>>> modifier) {
		return getModifierName(modifier.getNewModifier());
	}

	private static Optional<String> getModifierName(Optional<? extends Enum<?>> modifier) {
		return modifier.filter(x -> !IGNORED_MODIFIERS.contains(x))
			.map(Enum::name)
			.map(String::toLowerCase);
	}

	private static boolean hasSyntheticAttribute(JApiAttribute<SyntheticAttribute> syntheticAttribute) {
		final boolean oldIsSynthetic = syntheticAttribute.getOldAttribute().map(SyntheticAttribute.SYNTHETIC::equals).orElse(false);
		final boolean newIsSynthetic = syntheticAttribute.getNewAttribute().map(SyntheticAttribute.SYNTHETIC::equals).orElse(false);
		if (syntheticAttribute.getOldAttribute().isPresent() && syntheticAttribute.getNewAttribute().isPresent()) {
			return oldIsSynthetic && newIsSynthetic;
		}
		return oldIsSynthetic || newIsSynthetic;
	}

	private static boolean hasSyntheticModifier(JApiModifier<SyntheticModifier> syntheticModifier) {
		final boolean oldIsSynthetic = syntheticModifier.getOldModifier().map(SyntheticModifier.SYNTHETIC::equals).orElse(false);
		final boolean newIsSynthetic = syntheticModifier.getNewModifier().map(SyntheticModifier.SYNTHETIC::equals).orElse(false);
		if (syntheticModifier.getOldModifier().isPresent() && syntheticModifier.getNewModifier().isPresent()) {
			return oldIsSynthetic && newIsSynthetic;
		}
		return oldIsSynthetic || newIsSynthetic;
	}
}
