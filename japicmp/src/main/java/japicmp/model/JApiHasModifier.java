package japicmp.model;

import com.google.common.base.Optional;

public interface JApiHasModifier {
    String getAccessModifierNew();

    String getAccessModifierOld();

    String getFinalModifierOld();

    String getFinalModifierNew();

    String getStaticModifierOld();

    String getStaticModifierNew();

    Optional<Boolean> getFinalModifierOldOptional();

    Optional<Boolean> getFinalModifierNewOptional();

    Optional<Boolean> getStaticModifierOldOptional();

    Optional<Boolean> getStaticModifierNewOptional();
}
