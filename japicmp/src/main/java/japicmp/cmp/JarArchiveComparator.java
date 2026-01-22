package japicmp.cmp;

import japicmp.compat.CompatibilityChanges;
import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.exception.JApiCmpException.Reason;
import japicmp.filter.AnnotationFilterBase;
import japicmp.filter.Filter;
import japicmp.filter.Filters;
import japicmp.filter.JavadocLikePackageFilter;
import japicmp.model.JApiClass;
import japicmp.model.JApiCompatibilityChangeType;
import japicmp.model.JavaObjectSerializationCompatibility;
import japicmp.output.OutputFilter;
import japicmp.util.AnnotationHelper;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class provides the basic methods to compare the classes within to jar archives.
 */
public class JarArchiveComparator {
	private static final Logger LOGGER = Logger.getLogger(JarArchiveComparator.class.getName());
	private ReducibleClassPool commonClassPool;
	private ReducibleClassPool oldClassPool;
	private ReducibleClassPool newClassPool;
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
		for (JApiCompatibilityChangeType jApiCompatibility : JApiCompatibilityChangeType.values()) {
			jApiCompatibility.resetOverrides();
		}
		for (JarArchiveComparatorOptions.OverrideCompatibilityChange change : options.getOverrideCompatibilityChanges()) {
			JApiCompatibilityChangeType compatibilityChange = change.getCompatibilityChange();
			for (JApiCompatibilityChangeType jApiCompatibility : JApiCompatibilityChangeType.values()) {
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
		return createAndCompareClassLists(oldArchives, newArchives);
	}

	private void checkJavaObjectSerializationCompatibility(List<JApiClass> jApiClasses) {
		JavaObjectSerializationCompatibility javaObjectSerializationCompatibility = new JavaObjectSerializationCompatibility();
		javaObjectSerializationCompatibility.evaluate(jApiClasses);
	}

	private void setupClasspaths() {
		if (this.options.getClassPathMode() == JarArchiveComparatorOptions.ClassPathMode.ONE_COMMON_CLASSPATH) {
			commonClassPool = new ReducibleClassPool();
			commonClassPathAsString = setupClasspath(commonClassPool, this.options.getClassPathEntries());
		} else if (this.options.getClassPathMode() == JarArchiveComparatorOptions.ClassPathMode.TWO_SEPARATE_CLASSPATHS) {
			oldClassPool = new ReducibleClassPool();
			oldClassPathAsString = setupClasspath(oldClassPool, this.options.getOldClassPath());
			newClassPool = new ReducibleClassPool();
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
		if (!retVal.isEmpty() && !retVal.endsWith(File.pathSeparator)) {
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

	private void checkBinaryCompatibility(List<JApiClass> classList, JarArchiveComparatorOptions options) {
		CompatibilityChanges compatibilityChanges = new CompatibilityChanges(this, options);
		compatibilityChanges.evaluate(classList);
	}

	private List<JApiClass> createAndCompareClassLists(List<JApiCmpArchive> oldArchives, List<JApiCmpArchive> newArchives) {
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
		ClassesComparator classesComparator = new ClassesComparator(this, options);
		classesComparator.compare(oldClasses, newClasses);
		List<JApiClass> classList = classesComparator.getClasses();
		if (LOGGER.isLoggable(Level.FINE)) {
			for (JApiClass jApiClass : classList) {
				LOGGER.fine(jApiClass.toString());
			}
		}
		checkBinaryCompatibility(classList, options);
		checkJavaObjectSerializationCompatibility(classList);
		OutputFilter.sortClassesAndMethods(classList);
		return classList;
	}

	private List<CtClass> createListOfCtClasses(List<JApiCmpArchive> archives, ReducibleClassPool classPool) {
		List<CtClass> ctClasses = archives.stream()
			.flatMap(archive -> toCtClassStream(archive, classPool))
			.collect(Collectors.toList());
		ctClasses = filterClasses(ctClasses, classPool, false);
		return ctClasses;
	}

	private Stream<CtClass> toCtClassStream(JApiCmpArchive archive, ReducibleClassPool classPool) {
		if (archive.getFile() != null) {
			try (JarFile jarFile = new JarFile(archive.getFile())) {
				return jarFile.stream()
					.map(jarEntry -> ctClass(classPool, jarFile, jarEntry))
					.filter(Objects::nonNull)
					.collect(Collectors.toList()).stream();
			} catch (IOException e) {
				throw new JApiCmpException(Reason.IoException, "Failed to load archive from file: " + e.getMessage(), e);
			}
		} else if (archive.getBytes() != null) {
			return bytesToCtClasses(archive, classPool);
		} else {
			throw new JApiCmpException(Reason.IllegalArgument, JApiCmpArchive.class.getSimpleName() + " has no file and no bytes: " + archive);
		}
	}

	private static Stream<CtClass> bytesToCtClasses(JApiCmpArchive archive, ReducibleClassPool classPool) {
		List<CtClass> ctClasses = new ArrayList<>();
		try (JarInputStream jarInputStream = new JarInputStream(new ByteArrayInputStream(archive.getBytes()))) {
			JarEntry nextJarEntry = jarInputStream.getNextJarEntry();
			while (nextJarEntry != null) {
				if (!nextJarEntry.isDirectory() && nextJarEntry.getName().endsWith(".class")) {
					byte[] byteArray = getJarEntryContent(jarInputStream);
					CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(byteArray));
					ctClasses.add(ctClass);
				}
				nextJarEntry = jarInputStream.getNextJarEntry();
			}
		} catch (IOException e) {
			throw new JApiCmpException(Reason.IoException, "Failed to load archive from byte array: " + e.getMessage(), e);
		}
		return ctClasses.stream();
	}

	private static byte[] getJarEntryContent(JarInputStream jarInputStream) throws IOException {
		int len;
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((len = jarInputStream.read(buffer)) > 0) {
			baos.write(buffer, 0, len);
		}
        return baos.toByteArray();
	}

	private CtClass ctClass(ClassPool classPool, JarFile jarFile, JarEntry jarEntry) {
		String name = jarEntry.getName();
		if (name.endsWith(".class")) {
			CtClass ctClass;
			try (InputStream classFile = jarFile.getInputStream(jarEntry)) {
				ctClass = classPool.makeClass(classFile);
			} catch (Exception e) {
				throw new JApiCmpException(Reason.IoException, String.format("Failed to load file from jar '%s' as class file: %s.", name, e.getMessage()), e);
			}
			return ctClass;
		} else {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine(String.format("Skipping file '%s' because filename does not end with '.class'.", name));
			}
		}
		return null;
	}

	public List<CtClass> filterClasses(List<CtClass> ctClasses, ReducibleClassPool classPool, boolean ignorePackageFilters) {
		// marks whether any package was found
		// if so we need to go over _all_ classes again
		boolean packageFilterEncountered = false;

		List<CtClass> classes = new LinkedList<>();
		for (CtClass ctClass : ctClasses) {
			if (!packageFilterEncountered) {
				if (options.getFilters().includeClass(ctClass)) {
					classes.add(ctClass);
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.fine(String.format("Adding class '%s' with jar name '%s' to list.", ctClass.getName(), ctClass.getName()));
					}
				} else {
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.fine(String.format("Ignoring class '%s' with jar name '%s'.", ctClass.getName(), ctClass.getName()));
					}
				}
			}
			if (!ignorePackageFilters && ctClass.getName().endsWith("package-info")) {
				packageFilterEncountered |= updatePackageFilter(ctClass);
				if (packageFilterEncountered) {
					// we found a package filter, so any filtering we did so far may be invalid
					// reset everything and restart after having read all remaining filters
					classes.forEach(classPool::remove);
					classes.clear();
				}
			}
		}

		return packageFilterEncountered
			? filterClasses(ctClasses, classPool, true)
			: classes;
	}


	private boolean updatePackageFilter(CtClass ctClass) {
		boolean filtersUpdated = false;
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
		if (!newFilters.isEmpty()) {
			filters.getIncludes().addAll(newFilters);
			newFilters.clear();
			filtersUpdated = true;
		}
		for (Filter filter : filters.getExcludes()) {
			if (filter instanceof AnnotationFilterBase) {
				String className = ((AnnotationFilterBase) filter).getClassName();
				if (AnnotationHelper.hasAnnotation(ctClass.getClassFile(), className)) {
					newFilters.add(new JavadocLikePackageFilter(ctClass.getPackageName(), false));
				}
			}
		}
		if (!newFilters.isEmpty()) {
			filters.getExcludes().addAll(newFilters);
			newFilters.clear();
			filtersUpdated = true;
		}
		return filtersUpdated;
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
	public ReducibleClassPool getCommonClassPool() {
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
	 *
	 * @param archiveType specify if this class should be loaded from the old or new class path
	 * @param name        the name of the class (FQN)
	 * @return the loaded class (if options are not set to ignore missing classes)
	 * @throws japicmp.exception.JApiCmpException if loading the class fails
	 */
	public Optional<CtClass> loadClass(ArchiveType archiveType, String name) {
		Optional<CtClass> loadedClass = Optional.empty();
		if (Options.N_A.equalsIgnoreCase(name)) {
			return loadedClass;
		}
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
