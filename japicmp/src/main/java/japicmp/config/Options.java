package japicmp.config;

import com.google.common.base.Optional;
import japicmp.cmp.AccessModifier;

public class Options {
    private String oldArchive;
    private String newArchive;
    private boolean outputOnlyModifications = false;
    private Optional<String> xmlOutputFile = Optional.<String>absent();
    private AccessModifier acessModifier = AccessModifier.PUBLIC;

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

    public void setAcessModifier(AccessModifier acessModifier) {
        this.acessModifier = acessModifier;
    }

    public AccessModifier getAcessModifier() {
        return acessModifier;
    }
}
