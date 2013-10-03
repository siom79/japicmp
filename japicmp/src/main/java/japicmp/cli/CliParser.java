package japicmp.cli;

import com.google.common.base.Optional;
import japicmp.cmp.AccessModifier;
import japicmp.config.Options;
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
            if ("-h".equals(arg)) {
                System.out.println("Available parameters:");
                System.out.println("-h                        Prints this help.");
                System.out.println("-o <pathToOldVersionJar>  Provides the path to the old version of the jar.");
                System.out.println("-n <pathToNewVersionJar>  Provides the path to the new version of the jar.");
                System.out.println("-x <pathToXmlOutputFile>  Provides the path to the xml output file. If not given, stdout is used.");
                System.out.println("-a <accessModifier>       Sets the access modifier level (public, package, protected, private), which should be used.");
                System.out.println("-m                        Outputs only modified classes/methods. If not given, all classes and methods are printed.");
                System.exit(0);
            }
            if ("-a".equals(arg)) {
                String accessModifierArg = getOptionWithArgument("-a", sae);
                try {
                    AccessModifier accessModifier = AccessModifier.valueOf(accessModifierArg.toUpperCase());
                    options.setAcessModifier(accessModifier);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(String.format("Invalid value for option -a: %s. Possible values are: %s.", accessModifierArg, listOfAccessModifiers()));
                }
            }
        }
        checkForMandatoryOptions(options);
        return options;
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
