package japicmp.cmp;

import java.util.LinkedList;
import java.util.List;

public class JarArchiveComparatorOptions {
    private List<String> packagesInclude = new LinkedList<String>();
    private List<String> packagesExclude = new LinkedList<String>();

    public List<String> getPackagesExclude() {
        return packagesExclude;
    }

    public List<String> getPackagesInclude() {
        return packagesInclude;
    }
}
