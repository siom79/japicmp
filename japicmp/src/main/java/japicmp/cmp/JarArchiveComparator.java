package japicmp.cmp;

import japicmp.config.PackageFilter;
import japicmp.exception.JApiCmpException;
import japicmp.exception.JApiCmpException.Reason;
import japicmp.model.BinaryCompatibility;
import japicmp.model.JApiClass;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class JarArchiveComparator {
    private static final Logger logger = Logger.getLogger(JarArchiveComparator.class.getName());
    private final ClassPool classPool = new ClassPool();
    private String classPath = "";
    private JarArchiveComparatorOptions options;

    public JarArchiveComparator(JarArchiveComparatorOptions options) {
        this.options = options;
        setupClasspath();
    }

    public List<JApiClass> compare(File oldArchive, File newArchive) {
        ClassesComparator classesComparator = compareClassLists(oldArchive, newArchive, classPool, options);
        List<JApiClass> classList = classesComparator.getClasses();
        checkBinaryCompatibility(classList);
		return classList;
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
                throw new JApiCmpException(Reason.ClassLoading, "Could not find class path entry '" + classPathEntry + "': " + e.getMessage(), e);
            }
        }
    }

    public String getClasspath() {
        return classPath;
    }

    private void checkBinaryCompatibility(List<JApiClass> classList) {
    	BinaryCompatibility binaryCompatibility = new BinaryCompatibility();
		binaryCompatibility.evaluate(classList);
	}

	private ClassesComparator compareClassLists(File oldArchive, File newArchive, ClassPool classPool, JarArchiveComparatorOptions options) {
        List<CtClass> oldClasses = createListOfCtClasses(oldArchive, classPool, options);
        List<CtClass> newClasses = createListOfCtClasses(newArchive, classPool, options);
        ClassesComparator classesComparator = new ClassesComparator(this);
        classesComparator.compare(oldClasses, newClasses);
        if (logger.isLoggable(Level.FINE)) {
            for (JApiClass jApiClass : classesComparator.getClasses()) {
                logger.fine(jApiClass.toString());
            }
        }
        return classesComparator;
    }

    private List<CtClass> createListOfCtClasses(File archive, ClassPool classPool, JarArchiveComparatorOptions options) {
        List<CtClass> classes = new LinkedList<CtClass>();
        try (JarFile jarFile = new JarFile(archive)) {
            Enumeration<JarEntry> entryEnumeration = jarFile.entries();
            while (entryEnumeration.hasMoreElements()) {
                JarEntry jarEntry = entryEnumeration.nextElement();
                String name = jarEntry.getName();
                if (name.endsWith(".class")) {
                    CtClass ctClass = null;
                    try {
                        ctClass = classPool.makeClass(jarFile.getInputStream(jarEntry));
                    } catch (Exception e) {
                    	throw new JApiCmpException(Reason.IoException, String.format("Failed to load file from jar '%s' as class file: %s.", name, e.getMessage()), e);
                    }
                    if(packageMatches(options, ctClass)) {
                        classes.add(ctClass);
                    }
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine(String.format("Adding class '%s' with jar name '%s' to list.", ctClass.getName(), name));
                    }
                } else {
                	if (logger.isLoggable(Level.FINE)) {
                		logger.fine(String.format("Skipping file '%s' because filename does not end with '.class'.", name));
                	}
                }
            }
        } catch (IOException e) {
        	throw new JApiCmpException(Reason.IoException, String.format("Processing of jar file %s failed: %s", archive.getAbsolutePath(), e.getMessage()), e);
		}
        return classes;
    }

    private boolean packageMatches(JarArchiveComparatorOptions options, CtClass ctClass) {
        String packageName = ctClass.getPackageName();
        for (PackageFilter packageFilter : options.getPackagesInclude()) {
            if (packageFilter.matches(packageName)) {
                return true;
            }
        }
        for (PackageFilter packageFilter : options.getPackagesExclude()) {
            if (packageFilter.matches(packageName)) {
                return false;
            }
        }
        int noInclude = options.getPackagesInclude().size();
        if (noInclude > 0) {
            return false;
        }
        return true;
    }

    public JarArchiveComparatorOptions getJarArchiveComparatorOptions() {
        return this.options;
    }
}
