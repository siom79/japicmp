package japicmp.model;

import com.google.common.base.Optional;

public interface JApiModifier {
    public String getAccessModifierNew();

    public String getAccessModifierOld();

    public String getFinalModifierOld();

    public String getFinalModifierNew();

    public String getStaticModifierOld();

    public String getStaticModifierNew();

    public Optional<Boolean> getFinalModifierOldOptional();

    public Optional<Boolean> getFinalModifierNewOptional();

    public Optional<Boolean> getStaticModifierOldOptional();

    public Optional<Boolean> getStaticModifierNewOptional();
}
