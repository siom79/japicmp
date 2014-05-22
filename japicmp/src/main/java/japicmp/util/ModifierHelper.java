package japicmp.util;

import japicmp.model.AccessModifier;

import java.lang.reflect.Modifier;

public class ModifierHelper {

    private ModifierHelper() {

    }

    public static boolean matchesModifierLevel(int modifierOfElement, AccessModifier modifierLevel) {
        AccessModifier modifierLevelOfElement = translateToModifierLevel(modifierOfElement);
        return (modifierLevelOfElement.getLevel() >= modifierLevel.getLevel());
    }

    public static AccessModifier translateToModifierLevel(int modifier) {
        if(Modifier.isPublic(modifier)) {
            return AccessModifier.PUBLIC;
        } else if(Modifier.isProtected(modifier)) {
            return AccessModifier.PROTECTED;
        } else if(Modifier.isPrivate(modifier)) {
            return AccessModifier.PRIVATE;
        } else {
            return AccessModifier.PACKAGE;
        }
    }
}
