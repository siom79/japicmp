package japicmp.config;

import com.google.common.base.Optional;
import japicmp.model.AccessModifier;

import java.util.LinkedList;
import java.util.List;

public class Options {
    private String oldArchive;
    private String newArchive;
    private boolean outputOnlyModifications = false;
    private Optional<String> xmlOutputFile = Optional.<String>absent();
    private AccessModifier accessModifier = AccessModifier.PUBLIC;
    private List<PackageFilter> packagesInclude = new LinkedList<PackageFilter>();
    private List<PackageFilter> packagesExclude = new LinkedList<PackageFilter>();

    public String getNewArchive() {
        return newArchive;
    }

    public void setNewArchive(String newArchive) {
        this.newArchive = newArchive;
    }

    public String getOldArchive() {
        return oldArchive;
    }

    public void setOldArchive(String oldArchive) {
        this.oldArchive = oldArchive;
    }

    public boolean isOutputOnlyModifications() {
        return outputOnlyModifications;
    }

    public void setOutputOnlyModifications(boolean outputOnlyModifications) {
        this.outputOnlyModifications = outputOnlyModifications;
    }

    public Optional<String> getXmlOutputFile() {
        return xmlOutputFile;
    }

    public void setXmlOutputFile(Optional<String> xmlOutputFile) {
        this.xmlOutputFile = xmlOutputFile;
    }

    public void setAccessModifier(AccessModifier accessModifier) {
        this.accessModifier = accessModifier;
    }

    public AccessModifier getAccessModifier() {
        return accessModifier;
    }

    public List<PackageFilter> getPackagesInclude() {
        return packagesInclude;
    }

    public List<PackageFilter> getPackagesExclude() {
        return packagesExclude;
    }

    public void addPackagesExcludeFromArgument(String packagesExcludeArg) {
        String[] parts = packagesExcludeArg.split(",");
        for (String part : parts) {
            part = part.trim();
            try {
                getPackagesExclude().add(new PackageFilter(part));
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format("Wrong syntax for package include option '%s': %s", part, e.getMessage()));
            }
        }
    }

    public void addPackageIncludeFromArgument(String packagesIncludeArg) {
        String[] parts = packagesIncludeArg.split(",");
        for (String part : parts) {
            part = part.trim();
            try {
                getPackagesInclude().add(new PackageFilter(part));
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format("Wrong syntax for package exclude option '%s': %s", part, e.getMessage()));
            }
        }
    }
}
