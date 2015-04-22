package japicmp.cmp;

import japicmp.config.PackageFilter;
import japicmp.model.AccessModifier;

import java.util.LinkedList;
import java.util.List;

public class JarArchiveComparatorOptions {
    private List<String> classPathEntries = new LinkedList<>();
    private List<PackageFilter> packagesInclude = new LinkedList<>();
    private List<PackageFilter> packagesExclude = new LinkedList<>();
    private AccessModifier accessModifier = AccessModifier.PROTECTED;

    public List<PackageFilter> getPackagesExclude() {
        return packagesExclude;
    }

    public List<PackageFilter> getPackagesInclude() {
        return packagesInclude;
    }

    public List<String> getClassPathEntries() {
        return classPathEntries;
    }

    public void setAccessModifier(AccessModifier accessModifier) {
        this.accessModifier = accessModifier;
    }

    public AccessModifier getAccessModifier() {
        return accessModifier;
    }
}
