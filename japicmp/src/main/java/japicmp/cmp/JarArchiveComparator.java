package japicmp.cmp;

import japicmp.exception.JApiCmpException;
import japicmp.exception.JApiCmpException.Reason;
import japicmp.model.BinaryCompatibility;
import japicmp.model.JApiClass;
import japicmp.model.JavaObjectSerializationCompatibility;
import japicmp.output.OutputFilter;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the basic methods to compare the classes within to jar archives.
 */
public class JarArchiveComparator {
    private static final Logger LOGGER = Logger.getLogger(JarArchiveComparator.class.getName());
    private final ClassPool classPool = new ClassPool();
    private String classPath = "";
    private JarArchiveComparatorOptions options;

    /**
     * Constructs an instance of this class and does a setup of the classpath
     * @param options the options used in the further processing
     */
    public JarArchiveComparator(JarArchiveComparatorOptions options) {
        this.options = options;
        setupClasspath();
    }

    /**
	 * Compares the two given jar archives.
	 * @param oldArchive the old version of the archive
	 * @param newArchive the new version of the archive
	 * @return a list which contains one instance of {@link japicmp.model.JApiClass} for each class found in one of the two archives
	 * @throws JApiCmpException if the comparison fails
	 */
	public List<JApiClass> compare(File oldArchive, File newArchive) {
		return compare(Collections.singletonList(oldArchive), Collections.singletonList(newArchive));
	}

	/**
	 * Compares the two given list of jar archives.
	 * @param oldArchives the old versions of the archives
	 * @param newArchives the new versions of the archives
	 * @return a list which contains one instance of {@link japicmp.model.JApiClass} for each class found in one of the archives
	 * @throws JApiCmpException if the comparison fails
	 */
	public List<JApiClass> compare(List<File> oldArchives, List<File> newArchives) {
		return createAndCompareClassLists(oldArchives, newArchives, classPool, options);
	}

	private void checkJavaObjectSerializationCompatibility(List<JApiClass> jApiClasses) {
		JavaObjectSerializationCompatibility javaObjectSerializationCompatibility = new JavaObjectSerializationCompatibility();
		javaObjectSerializationCompatibility.evaluate(jApiClasses);
	}

	private void setupClasspath() {
        classPool.appendSystemPath();
        classPath += System.getProperty("java.class.path");
        for (String classPathEntry : options.getClassPathEntries()) {
            try {
                classPool.appendClassPath(classPathEntry);
                if (!classPath.endsWith(File.pathSeparator)) {
                    classPath += File.pathSeparator;
                }
                classPath += classPathEntry;
            } catch (NotFoundException e) {
                throw JApiCmpException.forClassNotFound(e, classPathEntry, this);
            }
        }
    }

    /**
     * Returns the classpath used by {@link japicmp.cmp.JarArchiveComparator}
     * @return the classpath as String
     */
    public String getClasspath() {
        return classPath;
    }

    private void checkBinaryCompatibility(List<JApiClass> classList) {
    	BinaryCompatibility binaryCompatibility = new BinaryCompatibility();
		binaryCompatibility.evaluate(classList);
	}

	private List<JApiClass> createAndCompareClassLists(List<File> oldArchives, List<File> newArchives, ClassPool classPool, JarArchiveComparatorOptions options) {
        List<CtClass> oldClasses = createListOfCtClasses(oldArchives, classPool);
        List<CtClass> newClasses = createListOfCtClasses(newArchives, classPool);
        return compareClassLists(options, oldClasses, newClasses);
    }

    /**
     * Compares the two lists with CtClass objects using the provided options instance.
     * @param options the options to use
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
				LOGGER.log(Level.FINE, "Processing jar archive " + archive.getAbsolutePath() + ".");
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

    /**
     * Returns the instance of {@link japicmp.cmp.JarArchiveComparatorOptions} that is used.
     * @return an instance of {@link japicmp.cmp.JarArchiveComparatorOptions}
     */
    public JarArchiveComparatorOptions getJarArchiveComparatorOptions() {
        return this.options;
    }

    /**
     * Returns the javassist ClassPool instance that is used by this instance. This can be used in unit tests to define
     * artificial CtClass instances for the same ClassPool.
     * @return an instance of ClassPool
     */
	public ClassPool getClassPool() {
		return classPool;
	}
}
