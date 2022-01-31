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
import java.util.function.Supplier;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
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
		ClassesComparator classesComparator = new ClassesComparator(this, options);
		classesComparator.compare(oldClasses, newClasses);
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

	private List<CtClass> createListOfCtClasses(List<File> archives, ReducibleClassPool classPool) {
		return createListOfCtClasses(() -> new JarsCtClassIterable(archives, classPool), classPool);
	}

	List<CtClass> createListOfCtClasses(Supplier<Iterable<CtClass>> ctClasses, ReducibleClassPool classPool) {
		return loadAndFilterClasses(ctClasses, classPool, false);
	}

	private List<CtClass> loadAndFilterClasses(Supplier<Iterable<CtClass>> ctClasses, ReducibleClassPool classPool, boolean ignorePackageFilters) {
		// marks whether any package was found
		// if so we need to go over _all_ classes again
		boolean packageFilterEncountered = false;

		List<CtClass> classes = new LinkedList<>();
		for (CtClass ctClass : ctClasses.get()) {
			if (!packageFilterEncountered) {
				if (options.getFilters().includeClass(ctClass)) {
					classes.add(ctClass);
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.fine(String.format("Adding class '%s' with jar name '%s' to list.", ctClass.getName(), ctClass.getName()));
					}
				} else {
					classPool.remove(ctClass);
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
				? loadAndFilterClasses(ctClasses, classPool, true)
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
		if (newFilters.size() > 0) {
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
		if (newFilters.size() > 0) {
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

	private static class JarsCtClassIterable implements Iterable<CtClass>, Iterator<CtClass> {
		private final Iterator<File> archives;
		private final ClassPool classPool;

		private Iterator<CtClass> currentIterator = null;

		public JarsCtClassIterable(List<File> archives, ClassPool classPool) {
			this.archives = archives.iterator();
			this.classPool = classPool;
		}

		@Override
		public boolean hasNext() {
			if (currentIterator != null) {
				if (currentIterator.hasNext()) {
					return true;
				} else {
					currentIterator = null;
				}
			}
			if (archives.hasNext()) {
				final File archive = archives.next();
				currentIterator = new JarCtClassIterator(archive, classPool);
				return hasNext();
			}
			return false;
		}

		@Override
		public CtClass next() {
			return currentIterator.next();
		}

		@Override
		public Iterator<CtClass> iterator() {
			return this;
		}
	}

	private static class JarCtClassIterator implements Iterator<CtClass> {

		private final File archive;
		private final JarFile jarFile;
		private final Enumeration<JarEntry> entryEnumeration;
		private final ClassPool classPool;

		private CtClass next = null;

		public JarCtClassIterator(File archive, ClassPool classPool) {
			this.archive = archive;
			try {
				this.jarFile = new JarFile(archive);
			} catch (IOException e) {
				throw new JApiCmpException(Reason.IoException, String.format("Processing of jar file %s failed: %s", archive.getAbsolutePath(), e.getMessage()), e);
			}
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Loading classes from jar file '" + archive.getAbsolutePath() + "'");
			}
			this.entryEnumeration = jarFile.entries();
			this.classPool = classPool;
		}

		@Override
		public boolean hasNext() {
			if (next != null) {
				return true;
			}

			while (entryEnumeration.hasMoreElements()) {
				JarEntry jarEntry = entryEnumeration.nextElement();
				String name = jarEntry.getName();
				if (name.endsWith(".class")) {
					CtClass ctClass;
					try (InputStream classFile = jarFile.getInputStream(jarEntry)) {
						ctClass = classPool.makeClass(classFile);
					} catch (Exception e) {
						throw new JApiCmpException(Reason.IoException, String.format("Failed to load file from jar '%s' as class file: %s.", name, e.getMessage()), e);
					}
					next = ctClass;
					return true;
				} else {
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.fine(String.format("Skipping file '%s' because filename does not end with '.class'.", name));
					}
				}
			}
			try {
				jarFile.close();
			} catch (IOException e) {
				throw new JApiCmpException(Reason.IoException, String.format("Processing of jar file %s failed: %s", archive.getAbsolutePath(), e.getMessage()), e);
			}
			return false;
		}

		@Override
		public CtClass next() {
			try {
				return next;
			} finally {
				next = null;
			}
		}
	}
}
