package japicmp.cmp;

import japicmp.config.PackageFilter;
import japicmp.model.AccessModifier;

import java.util.LinkedList;
import java.util.List;

public class JarArchiveComparatorOptions {
    private List<String> classPathEntries = new LinkedList<>();
    private List<PackageFilter> packagesInclude = new LinkedList<>();
    private List<PackageFilter> packagesExclude = new LinkedList<>();
    private AccessModifier modifierLevel = AccessModifier.PUBLIC;

    public List<PackageFilter> getPackagesExclude() {
        return packagesExclude;
    }

    public List<PackageFilter> getPackagesInclude() {
        return packagesInclude;
    }

    public AccessModifier getModifierLevel() {
        return modifierLevel;
    }

    public void setModifierLevel(AccessModifier modifierLevel) {
        this.modifierLevel = modifierLevel;
    }

    public List<String> getClassPathEntries() {
        return classPathEntries;
    }
}
