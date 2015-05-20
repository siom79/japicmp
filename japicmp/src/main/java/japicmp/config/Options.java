package japicmp.config;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import japicmp.exception.JApiCmpException;
import japicmp.filter.*;
import japicmp.model.AccessModifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Options {
    private File oldArchive;
    private File newArchive;
    private boolean outputOnlyModifications = false;
    private boolean outputOnlyBinaryIncompatibleModifications = false;
    private Optional<String> xmlOutputFile = Optional.absent();
    private Optional<String> htmlOutputFile = Optional.absent();
    private Optional<AccessModifier> accessModifier = Optional.of(AccessModifier.PROTECTED);
    private List<Filter> includes = new ArrayList<>();
    private List<Filter> excludes = new ArrayList<>();
    private List<ClassFilter> classesExclude = new ArrayList<>();
    private boolean includeSynthetic = false;

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

    public void setAccessModifier(AccessModifier accessModifier) {
        this.accessModifier = Optional.of(accessModifier);
    }

    public AccessModifier getAccessModifier() {
        return accessModifier.get();
    }

    public List<Filter> getIncludes() {
        return ImmutableList.copyOf(includes);
    }

    public List<Filter> getExcludes() {
        return ImmutableList.copyOf(excludes);
    }

    public void addPackagesExcludeFromArgument(String packagesExcludeArg) {
        addExcludeFromArgument(Optional.of(packagesExcludeArg));
    }

    public void addExcludeFromArgument(Optional<String> packagesExcludeArg) {
        excludes = createFilterList(packagesExcludeArg, excludes, "Wrong syntax for exclude option '%s': %s");
    }

    public void addPackageIncludeFromArgument(String packagesIncludeArg) {
        addIncludeFromArgument(Optional.of(packagesIncludeArg));
    }

    public void addIncludeFromArgument(Optional<String> packagesIncludeArg) {
        includes = createFilterList(packagesIncludeArg, includes, "Wrong syntax for include option '%s': %s");
    }

    private List<Filter> createFilterList(Optional<String> argumentString, List<Filter> filters, String errorMessage) {
        for (String filterString : Splitter.on(";").trimResults().omitEmptyStrings().split(argumentString.or(""))) {
            try {
                if (filterString.contains("#")) {
                    if (filterString.contains("(")) {
                        BehaviorFilter behaviorFilter = new BehaviorFilter(filterString);
                        filters.add(behaviorFilter);
                    } else {
                        FieldFilter fieldFilter = new FieldFilter(filterString);
                        filters.add(fieldFilter);
                    }
                } else {
                    ClassFilter classFilter = new ClassFilter(filterString);
                    filters.add(classFilter);
                    PackageFilter packageFilter = new PackageFilter(filterString);
                    filters.add(packageFilter);
                }
            } catch (Exception e) {
                throw new JApiCmpException(JApiCmpException.Reason.CliError, String.format(errorMessage, filterString, e.getMessage()));
            }
        }
        return filters;
    }

    public void setOutputOnlyBinaryIncompatibleModifications(boolean outputOnlyBinaryIncompatibleModifications) {
        this.outputOnlyBinaryIncompatibleModifications = outputOnlyBinaryIncompatibleModifications;
    }

    public boolean isOutputOnlyBinaryIncompatibleModifications() {
        return outputOnlyBinaryIncompatibleModifications;
    }

    public Optional<String> getHtmlOutputFile() {
        return htmlOutputFile;
    }

    public void setHtmlOutputFile(Optional<String> htmlOutputFile) {
        this.htmlOutputFile = htmlOutputFile;
    }

    public void setIncludeSynthetic(boolean showSynthetic) {
        this.includeSynthetic = showSynthetic;
    }

    public boolean isIncludeSynthetic() {
        return includeSynthetic;
    }

    public void addClassesExcludeFromArgument(Optional<String> stringOptional) {
        Iterable<String> classesAsStrings = Splitter.on(",").trimResults().omitEmptyStrings().split(stringOptional.or(""));
        for (String classAsString : classesAsStrings) {
            try {
                ClassFilter classFilter = new ClassFilter(classAsString);
                this.classesExclude.add(classFilter);
            } catch (Exception e) {
                throw new JApiCmpException(JApiCmpException.Reason.CliError, "Wrong syntax for class exclude option '" + classAsString + "': " + e.getMessage());
            }
        }
    }

    public List<ClassFilter> getClassesExclude() {
        return classesExclude;
    }
}
