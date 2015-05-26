package japicmp.cmp;

import japicmp.filter.Filter;
import japicmp.config.Options;
import japicmp.filter.Filters;
import japicmp.model.AccessModifier;

import java.util.LinkedList;
import java.util.List;

public class JarArchiveComparatorOptions {
	private List<String> classPathEntries = new LinkedList<>();
	private AccessModifier accessModifier = AccessModifier.PROTECTED;
    private Filters filters = new Filters();
	private boolean includeSynthetic = false;

	public static JarArchiveComparatorOptions of(Options options) {
		JarArchiveComparatorOptions comparatorOptions = new JarArchiveComparatorOptions();
        comparatorOptions.getFilters().getExcludes().addAll(options.getExcludes());
        comparatorOptions.getFilters().getIncludes().addAll(options.getIncludes());
		comparatorOptions.setAccessModifier(options.getAccessModifier());
		comparatorOptions.setIncludeSynthetic(options.isIncludeSynthetic());
		return comparatorOptions;
	}

    public Filters getFilters() {
        return filters;
    }

    public void setFilters(Filters filters) {
        this.filters = filters;
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

	public void setIncludeSynthetic(boolean includeSynthetic) {
		this.includeSynthetic = includeSynthetic;
	}

	public boolean isIncludeSynthetic() {
		return includeSynthetic;
	}
}
