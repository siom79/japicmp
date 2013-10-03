package japicmp.cmp;

import japicmp.model.JApiClass;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarArchiveComparator {
    private static final Logger logger = Logger.getLogger(JarArchiveComparator.class);

    public JarArchiveComparator(JarArchiveComparatorOptions options) {

    }

    public List<JApiClass> compare(File oldArchive, File newArchive) {
        ClassPool classPool = new ClassPool();
        try {
            ClassesComparator classesComparator = compareClassLists(oldArchive, newArchive, classPool);
            List<JApiClass> classList = classesComparator.getClasses();
            compareClasses(classList);
            return classList;
        } catch (Exception e) {
            System.err.println(String.format("Processing jar files '%s' and '%s' failed: %s.", oldArchive.getAbsolutePath(), newArchive.getAbsolutePath(), e.getMessage()));
            return new LinkedList<JApiClass>();
        }
    }

    private void compareClasses(List<JApiClass> classList) {
        for (JApiClass jApiClass : classList) {
            ClassComparator classComparator = new ClassComparator();
            classComparator.compare(jApiClass);
        }
    }

    private ClassesComparator compareClassLists(File oldArchive, File newArchive, ClassPool classPool) throws Exception {
        List<CtClass> oldClasses = createListOfCtClasses(oldArchive, classPool);
        List<CtClass> newClasses = createListOfCtClasses(newArchive, classPool);
        ClassesComparator classesComparator = new ClassesComparator();
        classesComparator.compare(oldClasses, newClasses);
        if (logger.isDebugEnabled()) {
            for (JApiClass jApiClass : classesComparator.getClasses()) {
                logger.debug(jApiClass);
            }
        }
        return classesComparator;
    }

    private List<CtClass> createListOfCtClasses(File archive, ClassPool classPool) throws Exception {
        List<CtClass> classes = new LinkedList<CtClass>();
        JarFile oldJar = null;
        try {
            oldJar = new JarFile(archive);
            Enumeration<JarEntry> entryEnumeration = oldJar.entries();
            while (entryEnumeration.hasMoreElements()) {
                JarEntry jarEntry = entryEnumeration.nextElement();
                String name = jarEntry.getName();
                if (name.endsWith(".class")) {
                    CtClass ctClass = null;
                    try {
                        ctClass = classPool.makeClass(oldJar.getInputStream(jarEntry));
                    } catch (Exception e) {
                        logger.error(String.format("Failed to load file from jar '%s' as class file: %s.", name, e.getMessage()));
                        throw e;
                    }
                    classes.add(ctClass);
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Adding class '%s' with jar name '%s' to list.", ctClass.getName(), name));
                    }
                } else {
                    logger.debug(String.format("Skipping file '%s' because filename does not end with '.class'.", name));
                }
            }
        } finally {
            if (oldJar != null) {
                oldJar.close();
            }
        }
        return classes;
    }
}
