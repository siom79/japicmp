package japicmp.cmp;

import japicmp.model.JApiCompatibilityChange;
import japicmp.model.JApiSemanticVersionLevel;
import japicmp.util.Optional;
import japicmp.config.IgnoreMissingClasses;
import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.filter.Filters;
import japicmp.model.AccessModifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents all options for the comparison.
 */
public class JarArchiveComparatorOptions {
	private final List<String> classPathEntries = new LinkedList<>();
	private AccessModifier accessModifier = AccessModifier.PROTECTED;
	private final Filters filters = new Filters();
	private boolean includeSynthetic = false;
	private IgnoreMissingClasses ignoreMissingClasses = new IgnoreMissingClasses();
	private ClassPathMode classPathMode = ClassPathMode.ONE_COMMON_CLASSPATH;
	private List<String> oldClassPath = new LinkedList<>();
	private List<String> newClassPath = new LinkedList<>();
	private boolean noAnnotations = false;
	private boolean includeClassFileFormatVersion = false;
	private List<OverrideCompatibilityChange> overrideCompatibilityChanges = new ArrayList<>();

	public static class OverrideCompatibilityChange {
		private JApiCompatibilityChange compatibilityChange;
		private boolean binaryCompatible;
		private boolean sourceCompatible;
		private JApiSemanticVersionLevel semanticVersionLevel;

		public OverrideCompatibilityChange(JApiCompatibilityChange compatibilityChange, boolean binaryCompatible,
										   boolean sourceCompatible, JApiSemanticVersionLevel semanticVersionLevel) {
			this.compatibilityChange = compatibilityChange;
			this.binaryCompatible = binaryCompatible;
			this.sourceCompatible = sourceCompatible;
			this.semanticVersionLevel = semanticVersionLevel;
		}

		public JApiCompatibilityChange getCompatibilityChange() {
			return compatibilityChange;
		}

		public boolean isBinaryCompatible() {
			return binaryCompatible;
		}

		public boolean isSourceCompatible() {
			return sourceCompatible;
		}

		public JApiSemanticVersionLevel getSemanticVersionLevel() {
			return semanticVersionLevel;
		}
	}

	/**
	 * When both versions of the archives under comparison use the exact same classpath
	 * only one classpath has to be provided. If the two class paths differ, both separate class paths
	 * can be provided.
	 */
	public enum ClassPathMode {
		ONE_COMMON_CLASSPATH, TWO_SEPARATE_CLASSPATHS
	}

	public static JarArchiveComparatorOptions of(Options options) {
		JarArchiveComparatorOptions comparatorOptions = new JarArchiveComparatorOptions();
		comparatorOptions.getFilters().getExcludes().addAll(options.getExcludes());
		comparatorOptions.getFilters().getIncludes().addAll(options.getIncludes());
		comparatorOptions.setAccessModifier(options.getAccessModifier());
		comparatorOptions.setIncludeSynthetic(options.isIncludeSynthetic());
		comparatorOptions.setIgnoreMissingClasses(options.getIgnoreMissingClasses());
		toJarArchiveComparatorClassPathMode(options, comparatorOptions);
		toJarArchiveComparatorClassPath(options.getOldClassPath(), comparatorOptions.getOldClassPath());
		toJarArchiveComparatorClassPath(options.getNewClassPath(), comparatorOptions.getNewClassPath());
		comparatorOptions.setNoAnnotations(options.isNoAnnotations());
		return comparatorOptions;
	}

	private void setIgnoreMissingClasses(IgnoreMissingClasses ignoreMissingClasses) {
		this.ignoreMissingClasses = ignoreMissingClasses;
	}

	private static void toJarArchiveComparatorClassPathMode(Options options, JarArchiveComparatorOptions comparatorOptions) {
		switch (options.getClassPathMode()) {
			case TWO_SEPARATE_CLASSPATHS:
				comparatorOptions.setClassPathMode(ClassPathMode.TWO_SEPARATE_CLASSPATHS);
				break;
			case ONE_COMMON_CLASSPATH:
				comparatorOptions.setClassPathMode(ClassPathMode.ONE_COMMON_CLASSPATH);
				break;
			default:
				throw new JApiCmpException(JApiCmpException.Reason.IllegalState, "Unknown classPathMode: " + options.getClassPathMode());
		}
	}

	private static void toJarArchiveComparatorClassPath(Optional<String> classPathOptional, List<String> comparatorClassPath) {
		if (classPathOptional.isPresent()) {
			String classPathAsString = classPathOptional.get();
			Collections.addAll(comparatorClassPath, classPathAsString.split(File.pathSeparator));
		}
	}

	public Filters getFilters() {
		return filters;
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

	public void setClassPathMode(ClassPathMode classPathMode) {
		this.classPathMode = classPathMode;
	}

	public ClassPathMode getClassPathMode() {
		return classPathMode;
	}

	public void setOldClassPath(List<String> oldClassPath) {
		this.oldClassPath = oldClassPath;
	}

	public List<String> getOldClassPath() {
		return oldClassPath;
	}

	public void setNewClassPath(List<String> newClassPath) {
		this.newClassPath = newClassPath;
	}

	public List<String> getNewClassPath() {
		return newClassPath;
	}

	public void setNoAnnotations(boolean noAnnotations) {
		this.noAnnotations = noAnnotations;
	}

	public boolean isNoAnnotations() {
		return noAnnotations;
	}

	public IgnoreMissingClasses getIgnoreMissingClasses() {
		return ignoreMissingClasses;
	}

	public boolean isIncludeClassFileFormatVersion() {
		return includeClassFileFormatVersion;
	}

	public void addOverrideCompatibilityChange(OverrideCompatibilityChange overrideCompatibilityChange) {
		this.overrideCompatibilityChanges.add(overrideCompatibilityChange);
	}

	public List<OverrideCompatibilityChange> getOverrideCompatibilityChanges() {
		return overrideCompatibilityChanges;
	}
}
