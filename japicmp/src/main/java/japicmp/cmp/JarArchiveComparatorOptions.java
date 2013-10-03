package japicmp.cmp;

import java.util.LinkedList;
import java.util.List;

public class JarArchiveComparatorOptions {
    private List<String> packagesInclude = new LinkedList<String>();
    private List<String> packagesExclude = new LinkedList<String>();
    private AccessModifier modifierLevel = AccessModifier.PUBLIC;

    public List<String> getPackagesExclude() {
        return packagesExclude;
    }

    public List<String> getPackagesInclude() {
        return packagesInclude;
    }

    public AccessModifier getModifierLevel() {
        return modifierLevel;
    }

    public void setModifierLevel(AccessModifier modifierLevel) {
        this.modifierLevel = modifierLevel;
    }
}
