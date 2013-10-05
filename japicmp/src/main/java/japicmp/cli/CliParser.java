package japicmp.cli;

import com.google.common.base.Optional;
import japicmp.cmp.AccessModifier;
import japicmp.cmp.PackageFilter;
import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.util.StringArrayEnumeration;

public class CliParser {

    public Options parse(String[] args) throws IllegalArgumentException {
        Options options = new Options();
        StringArrayEnumeration sae = new StringArrayEnumeration(args);
        while (sae.hasMoreElements()) {
            String arg = sae.nextElement();
            if ("-n".equals(arg)) {
                String newArchive = getOptionWithArgument("-n", sae);
                options.setNewArchive(newArchive);
            }
            if ("-o".equals(arg)) {
                String oldArchive = getOptionWithArgument("-o", sae);
                options.setOldArchive(oldArchive);
            }
            if ("-x".equals(arg)) {
                String xmlOutputFile = getOptionWithArgument("-x", sae);
                options.setXmlOutputFile(Optional.of(xmlOutputFile));
            }
            if ("-m".equals(arg)) {
                options.setOutputOnlyModifications(true);
            }
            if ("-a".equals(arg)) {
                String accessModifierArg = getOptionWithArgument("-a", sae);
                try {
                    AccessModifier accessModifier = AccessModifier.valueOf(accessModifierArg.toUpperCase());
                    options.setAccessModifier(accessModifier);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(String.format("Invalid value for option -a: %s. Possible values are: %s.", accessModifierArg, listOfAccessModifiers()));
                }
            }
            if ("-i".equals(arg)) {
                String packagesIncludeArg = getOptionWithArgument("-i", sae);
                String[] parts = packagesIncludeArg.split(",");
                for (String part : parts) {
                    part = part.trim();
                    try {
                        options.getPackagesInclude().add(new PackageFilter(part));
                    } catch (Exception e) {
                        throw new IllegalArgumentException(String.format("Wrong syntax for include option '%s': %s", part, e.getMessage()));
                    }
                }
            }
            if ("-e".equals(arg)) {
                String packagesExcludeArg = getOptionWithArgument("-e", sae);
                String[] parts = packagesExcludeArg.split(",");
                for (String part : parts) {
                    part = part.trim();
                    try {
                        options.getPackagesInclude().add(new PackageFilter(part));
                    } catch (Exception e) {
                        throw new IllegalArgumentException(String.format("Wrong syntax for exclude option '%s': %s", part, e.getMessage()));
                    }
                }
            }
            if ("-h".equals(arg)) {
                printHelp();
            }
        }
        checkForMandatoryOptions(options);
        return options;
    }

    private void printHelp() {
        System.out.println("Available parameters:");
        System.out.println("-h                        Prints this help.");
        System.out.println("-o <pathToOldVersionJar>  Provides the path to the old version of the jar.");
        System.out.println("-n <pathToNewVersionJar>  Provides the path to the new version of the jar.");
        System.out.println("-x <pathToXmlOutputFile>  Provides the path to the xml output file. If not given, stdout is used.");
        System.out.println("-a <accessModifier>       Sets the access modifier level (public, package, protected, private), which should be used.");
        System.out.println("-i <packagesToInclude>    Comma separated list of package names to include, * can be used as wildcard.");
        System.out.println("-e <packagesToExclude>    Comma separated list of package names to exclude, * can be used as wildcard.");
        System.out.println("-m                        Outputs only modified classes/methods. If not given, all classes and methods are printed.");
        throw new JApiCmpException(JApiCmpException.Reason.NormalTermination);
    }

    private void checkForMandatoryOptions(Options options) {
        if (options.getOldArchive() == null || options.getOldArchive().length() == 0) {
            throw new IllegalArgumentException("Missing option for old version: -o <pathToOldVersionJar>");
        }
        if (options.getNewArchive() == null || options.getNewArchive().length() == 0) {
            throw new IllegalArgumentException("Missing option for new version: -n <pathToNewVersionJar>");
        }
    }

    private String getOptionWithArgument(String option, StringArrayEnumeration sae) {
        if (sae.hasMoreElements()) {
            String value = sae.nextElement();
            if (value.startsWith("-")) {
                throw new IllegalArgumentException(String.format("Missing argument for option %s.", option));
            }
            return value;
        } else {
            throw new IllegalArgumentException(String.format("Missing argument for option %s.", option));
        }
    }

    private String listOfAccessModifiers() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (AccessModifier am : AccessModifier.values()) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(am.toString());
            i++;
        }
        return sb.toString();
    }
}
