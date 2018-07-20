package japicmp.cmp;

import japicmp.model.JApiCompatibilityChange;
import japicmp.util.Optional;
import japicmp.compat.CompatibilityChanges;
import japicmp.exception.JApiCmpException;
import japicmp.exception.JApiCmpException.Reason;
import japicmp.filter.AnnotationFilterBase;
import japicmp.filter.Filter;
import japicmp.filter.Filters;
import japicmp.filter.JavadocLikePackageFilter;
import japicmp.model.JApiClass;
import japicmp.model.JavaObjectSerializationCompatibility;
import japicmp.output.OutputFilter;
import japicmp.util.AnnotationHelper;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import static japicmp.util.FileHelper.toFileList;

/**
 * This class provides the basic methods to compare the classes within to jar archives.
 */
public class JarArchiveComparator {
	private static final Logger LOGGER = Logger.getLogger(JarArchiveComparator.class.getName());
	private ClassPool commonClassPool;
	private ClassPool oldClassPool;
	private ClassPool newClassPool;
	private String commonClassPathAsString = "";
	private String oldClassPathAsString = "";
	private String newClassPathAsString = "";
	private final JarArchiveComparatorOptions options;

	/**
	 * Constructs an instance of this class and performs a setup of the classpath
	 *
	 * @param options the options used in the further processing
	 */
	public JarArchiveComparator(JarArchiveComparatorOptions options) {
		this.options = options;
		setupClasspaths();
		setupCompatibilityChanges(options);
	}

	private void setupCompatibilityChanges(JarArchiveComparatorOptions options) {
		for (JApiCompatibilityChange jApiCompatibility : JApiCompatibilityChange.values()) {
			jApiCompatibility.resetOverrides();
		}
		for (JarArchiveComparatorOptions.OverrideCompatibilityChange change : options.getOverrideCompatibilityChanges()) {
			JApiCompatibilityChange compatibilityChange = change.getCompatibilityChange();
			for (JApiCompatibilityChange jApiCompatibility : JApiCompatibilityChange.values()) {
				if (jApiCompatibility == compatibilityChange) {
					jApiCompatibility.setBinaryCompatible(change.isBinaryCompatible());
					jApiCompatibility.setSourceCompatible(change.isSourceCompatible());
					jApiCompatibility.setSemanticVersionLevel(change.getSemanticVersionLevel());
				}
			}
		}
	}

	/**
	 * Compares the two given archives.
	 *
	 * @param oldArchive the old version of the archive
	 * @param newArchive the new version of the archive
	 * @return a list which contains one instance of {@link japicmp.model.JApiClass} for each class found in one of the two archives
	 * @throws JApiCmpException if the comparison fails
	 */
	public List<JApiClass> compare(JApiCmpArchive oldArchive, JApiCmpArchive newArchive) {
		return compare(Collections.singletonList(oldArchive), Collections.singletonList(newArchive));
	}

	/**
	 * Compares the two given lists of archives.
	 *
	 * @param oldArchives the old versions of the archives
	 * @param newArchives the new versions of the archives
	 * @return a list which contains one instance of {@link japicmp.model.JApiClass} for each class found in one of the archives
	 * @throws JApiCmpException if the comparison fails
	 */
	public List<JApiClass> compare(List<JApiCmpArchive> oldArchives, List<JApiCmpArchive> newArchives) {
		return createAndCompareClassLists(toFileList(oldArchives), toFileList(newArchives));
	}

	private void checkJavaObjectSerializationCompatibility(List<JApiClass> jApiClasses) {
		JavaObjectSerializationCompatibility javaObjectSerializationCompatibility = new JavaObjectSerializationCompatibility();
		javaObjectSerializationCompatibility.evaluate(jApiClasses);
	}

	private void setupClasspaths() {
		if (this.options.getClassPathMode() == JarArchiveComparatorOptions.ClassPathMode.ONE_COMMON_CLASSPATH) {
			commonClassPool = new ClassPool();
			commonClassPathAsString = setupClasspath(commonClassPool, this.options.getClassPathEntries());
		} else if (this.options.getClassPathMode() == JarArchiveComparatorOptions.ClassPathMode.TWO_SEPARATE_CLASSPATHS) {
			oldClassPool = new ClassPool();
			oldClassPathAsString = setupClasspath(oldClassPool, this.options.getOldClassPath());
			newClassPool = new ClassPool();
			newClassPathAsString = setupClasspath(newClassPool, this.options.getNewClassPath());
		} else {
			throw new JApiCmpException(Reason.IllegalState, "Unknown classpath mode: " + this.options.getClassPathMode());
		}
	}

	private String setupClasspath(ClassPool classPool, List<String> classPathEntries) {
		String classPathAsString = appendUserDefinedClassPathEntries(classPool, classPathEntries);
		return appendSystemClassPath(classPool, classPathAsString);
	}

	private String appendSystemClassPath(ClassPool classPool, String classPathAsString) {
		String retVal = classPathAsString;
		classPool.appendSystemPath();
		if (retVal.length() > 0 && !retVal.endsWith(File.pathSeparator)) {
			retVal += File.pathSeparator;
		}
		return retVal;
	}

	private String appendUserDefinedClassPathEntries(ClassPool classPool, List<String> classPathEntries) {
		StringBuilder classPathAsString = new StringBuilder();
		for (String classPathEntry : classPathEntries) {
			try {
				classPool.appendClassPath(classPathEntry);
				if (!classPathAsString.toString().endsWith(File.pathSeparator)) {
					classPathAsString.append(File.pathSeparator);
				}
				classPathAsString.append(classPathEntry);
			} catch (NotFoundException e) {
				throw JApiCmpException.forClassLoading(e, classPathEntry, this);
			}
		}
		return classPathAsString.toString();
	}

	/**
	 * Returns the common classpath used by {@link japicmp.cmp.JarArchiveComparator}
	 *
	 * @return the common classpath as String
	 */
	public String getCommonClasspathAsString() {
		return commonClassPathAsString;
	}

	/**
	 * Returns the classpath for the old version as String.
	 *
	 * @return the classpath for the old version
	 */
	public String getOldClassPathAsString() {
		return oldClassPathAsString;
	}

	/**
	 * Returns the classpath for the new version as String.
	 *
	 * @return the classpath for the new version
	 */
	public String getNewClassPathAsString() {
		return newClassPathAsString;
	}

	private void checkBinaryCompatibility(List<JApiClass> classList) {
		CompatibilityChanges compatibilityChanges = new CompatibilityChanges(this);
		compatibilityChanges.evaluate(classList);
	}

	private List<JApiClass> createAndCompareClassLists(List<File> oldArchives, List<File> newArchives) {
		List<CtClass> oldClasses;
		List<CtClass> newClasses;
		if (this.options.getClassPathMode() == JarArchiveComparatorOptions.ClassPathMode.ONE_COMMON_CLASSPATH) {
			oldClasses = createListOfCtClasses(oldArchives, commonClassPool);
			newClasses = createListOfCtClasses(newArchives, commonClassPool);
			return compareClassLists(options, oldClasses, newClasses);
		} else if (this.options.getClassPathMode() == JarArchiveComparatorOptions.ClassPathMode.TWO_SEPARATE_CLASSPATHS) {
			oldClasses = createListOfCtClasses(oldArchives, oldClassPool);
			newClasses = createListOfCtClasses(newArchives, newClassPool);
			return compareClassLists(options, oldClasses, newClasses);
		} else {
			throw new JApiCmpException(Reason.IllegalState, "Unknown classpath mode: " + this.options.getClassPathMode());
		}
	}

	/**
	 * Compares the two lists with CtClass objects using the provided options instance.
	 *
	 * @param options    the options to use
	 * @param oldClasses a list of CtClasses that represent the old version
	 * @param newClasses a list of CtClasses that represent the new version
	 * @return a list of {@link japicmp.model.JApiClass} that represent the changes
	 */
	List<JApiClass> compareClassLists(JarArchiveComparatorOptions options, List<CtClass> oldClasses, List<CtClass> newClasses) {
		List<CtClass> oldClassesFiltered = applyFilter(options, oldClasses);
		List<CtClass> newClassesFiltered = applyFilter(options, newClasses);
		ClassesComparator classesComparator = new ClassesComparator(this, options);
		classesComparator.compare(oldClassesFiltered, newClassesFiltered);
		List<JApiClass> classList = classesComparator.getClasses();
		if (LOGGER.isLoggable(Level.FINE)) {
			for (JApiClass jApiClass : classList) {
				LOGGER.fine(jApiClass.toString());
			}
		}
		checkBinaryCompatibility(classList);
		checkJavaObjectSerializationCompatibility(classList);
		OutputFilter.sortClassesAndMethods(classList);
		return classList;
	}

	private List<CtClass> applyFilter(JarArchiveComparatorOptions options, List<CtClass> ctClasses) {
		List<CtClass> newList = new ArrayList<>(ctClasses.size());
		for (CtClass ctClass : ctClasses) {
			if (options.getFilters().includeClass(ctClass)) {
				newList.add(ctClass);
			}
		}
		return newList;
	}

	private List<CtClass> createListOfCtClasses(List<File> archives, ClassPool classPool) {
		List<CtClass> classes = new LinkedList<>();
		for (File archive : archives) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Loading classes from jar file '" + archive.getAbsolutePath() + "'");
			}
			try (JarFile jarFile = new JarFile(archive)) {
				Enumeration<JarEntry> entryEnumeration = jarFile.entries();
				while (entryEnumeration.hasMoreElements()) {
					JarEntry jarEntry = entryEnumeration.nextElement();
					String name = jarEntry.getName();
					if (name.endsWith(".class")) {
						CtClass ctClass;
						try {
							ctClass = classPool.makeClass(jarFile.getInputStream(jarEntry));
						} catch (Exception e) {
							throw new JApiCmpException(Reason.IoException, String.format("Failed to load file from jar '%s' as class file: %s.", name, e.getMessage()), e);
						}
						classes.add(ctClass);
						if (LOGGER.isLoggable(Level.FINE)) {
							LOGGER.fine(String.format("Adding class '%s' with jar name '%s' to list.", ctClass.getName(), name));
						}
						if (name.endsWith("package-info.class")) {
							updatePackageFilter(ctClass);
						}
					} else {
						if (LOGGER.isLoggable(Level.FINE)) {
							LOGGER.fine(String.format("Skipping file '%s' because filename does not end with '.class'.", name));
						}
					}
				}
			} catch (IOException e) {
				throw new JApiCmpException(Reason.IoException, String.format("Processing of jar file %s failed: %s", archive.getAbsolutePath(), e.getMessage()), e);
			}
		}
		return classes;
	}

	private void updatePackageFilter(CtClass ctClass) {
		Filters filters = options.getFilters();
		List<Filter> newFilters = new LinkedList<>();
		for (Filter filter : filters.getIncludes()) {
			if (filter instanceof AnnotationFilterBase) {
				String className = ((AnnotationFilterBase) filter).getClassName();
				if (AnnotationHelper.hasAnnotation(ctClass.getClassFile(), className)) {
					newFilters.add(new JavadocLikePackageFilter(ctClass.getPackageName(), false));
				}
			}
		}
		if (newFilters.size() > 0) {
			filters.getIncludes().addAll(newFilters);
			newFilters.clear();
		}
		for (Filter filter : filters.getExcludes()) {
			if (filter instanceof AnnotationFilterBase) {
				String className = ((AnnotationFilterBase) filter).getClassName();
				if (AnnotationHelper.hasAnnotation(ctClass.getClassFile(), className)) {
					newFilters.add(new JavadocLikePackageFilter(ctClass.getPackageName(), false));
				}
			}
		}
		if (newFilters.size() > 0) {
			filters.getExcludes().addAll(newFilters);
			newFilters.clear();
		}
	}

	/**
	 * Returns the instance of {@link japicmp.cmp.JarArchiveComparatorOptions} that is used.
	 *
	 * @return an instance of {@link japicmp.cmp.JarArchiveComparatorOptions}
	 */
	public JarArchiveComparatorOptions getJarArchiveComparatorOptions() {
		return this.options;
	}

	/**
	 * Returns the javassist ClassPool instance that is used by this instance. This can be used in unit tests to define
	 * artificial CtClass instances for the same ClassPool.
	 *
	 * @return an instance of ClassPool
	 */
	public ClassPool getCommonClassPool() {
		return commonClassPool;
	}

	/**
	 * Returns the javassist ClassPool that is used for the old version.
	 *
	 * @return an instance of ClassPool
     */
	public ClassPool getOldClassPool() {
		return oldClassPool;
	}

	/**
	 * Returns the javassist ClassPool that is used for the new version.
	 *
	 * @return an instance of ClassPool
	 */
	public ClassPool getNewClassPool() {
		return newClassPool;
	}

	public enum ArchiveType {
		OLD, NEW
	}

	/**
	 * Loads a class either from the old, new or common classpath.
	 * @param archiveType specify if this class should be loaded from the old or new class path
	 * @param name the name of the class (FQN)
	 * @return the loaded class (if options are not set to ignore missing classes)
	 * @throws japicmp.exception.JApiCmpException if loading the class fails
	 */
	public Optional<CtClass> loadClass(ArchiveType archiveType, String name) {
		Optional<CtClass> loadedClass = Optional.absent();
		if (this.options.getClassPathMode() == JarArchiveComparatorOptions.ClassPathMode.ONE_COMMON_CLASSPATH) {
			try {
				loadedClass = Optional.of(commonClassPool.get(name));
			} catch (NotFoundException e) {
				if (!options.getIgnoreMissingClasses().ignoreClass(e.getMessage())) {
					throw JApiCmpException.forClassLoading(e, name, this);
				}
			}
		} else if (this.options.getClassPathMode() == JarArchiveComparatorOptions.ClassPathMode.TWO_SEPARATE_CLASSPATHS) {
			if (archiveType == ArchiveType.OLD) {
				try {
					loadedClass = Optional.of(oldClassPool.get(name));
				} catch (NotFoundException e) {
					if (!options.getIgnoreMissingClasses().ignoreClass(e.getMessage())) {
						throw JApiCmpException.forClassLoading(e, name, this);
					}
				}
			} else if (archiveType == ArchiveType.NEW) {
				try {
					loadedClass = Optional.of(newClassPool.get(name));
				} catch (NotFoundException e) {
					if (!options.getIgnoreMissingClasses().ignoreClass(e.getMessage())) {
						throw JApiCmpException.forClassLoading(e, name, this);
					}
				}
			} else {
				throw new JApiCmpException(Reason.IllegalState, "Unknown archive type: " + archiveType);
			}
		} else {
			throw new JApiCmpException(Reason.IllegalState, "Unknown classpath mode: " + this.options.getClassPathMode());
		}
		return loadedClass;
	}
}
