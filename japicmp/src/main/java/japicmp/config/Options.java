package japicmp.config;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import japicmp.model.AccessModifier;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Options {
    private File oldArchive;
    private File newArchive;
    private boolean outputOnlyModifications = false;
    private Optional<String> xmlOutputFile = Optional.<String>absent();
    private Optional<AccessModifier> accessModifier = Optional.of(AccessModifier.PUBLIC);
    private List<PackageFilter> packagesInclude = new LinkedList<PackageFilter>();
    private List<PackageFilter> packagesExclude = new LinkedList<PackageFilter>();

    public File getNewArchive() {
        return newArchive;
    }

    public void setNewArchive(File newArchive) {
        this.newArchive = newArchive;
    }

    public File getOldArchive() {
        return oldArchive;
    }

    public void setOldArchive(File oldArchive) {
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

    public void setAccessModifier(Optional<AccessModifier> accessModifier) {
        this.accessModifier = accessModifier;
    }

    public AccessModifier getAccessModifier() {
        return accessModifier.get();
    }

    public List<PackageFilter> getPackagesInclude() {
        return ImmutableList.copyOf(packagesInclude);
    }

    public List<PackageFilter> getPackagesExclude() {
        return ImmutableList.copyOf(packagesExclude);
    }

    public void addPackagesExcludeFromArgument(Optional<String> packagesExcludeArg) {
        packagesExclude = apply(packagesExcludeArg, packagesExclude);
    }

    public void addPackageIncludeFromArgument(Optional<String> packagesIncludeArg) {
        packagesInclude = apply(packagesIncludeArg, packagesInclude);
    }

    private List<PackageFilter> apply(Optional<String> filterString, List<PackageFilter> packages) {
        for (String part : Splitter.on(",").trimResults().omitEmptyStrings().split(filterString.or(""))) {
            try {
                packages.add(new PackageFilter(part));
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format("Wrong syntax for package exclude option '%s': %s", part, e.getMessage()));
            }
        }
        return packages;
    }
}
