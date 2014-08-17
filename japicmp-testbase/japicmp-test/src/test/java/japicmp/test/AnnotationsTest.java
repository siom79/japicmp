package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiAnnotation;
import japicmp.model.JApiAnnotationElement;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AnnotationsTest {
    private static List<JApiClass> jApiClasses;

    @BeforeClass
    public static void beforeClass() {
        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
        jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
    }

    @Test
    public void testAnnotationChanges() {
        JApiClass authorAnnotationChanges = getJApiClass(jApiClasses, Annotations.AuthorAnnotationChanges.class.getName());
        JApiAnnotation authorAnnotation = getJApiAnnotation(authorAnnotationChanges.getAnnotations(), Annotations.Author.class.getName());
        assertThat(authorAnnotation.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
    }

    @Test
    public void testAnnotationGetsNewValue() {
        JApiClass authorAnnotationGetsNewValue = getJApiClass(jApiClasses, Annotations.AuthorAnnotationGetsNewValue.class.getName());
        JApiAnnotation authorAnnotation = getJApiAnnotation(authorAnnotationGetsNewValue.getAnnotations(), Annotations.Author.class.getName());
        assertThat(authorAnnotation.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
        assertThat(authorAnnotation.getNewAnnotation().isPresent(), is(true));
        JApiAnnotationElement language = getJApiAnnotationElement(authorAnnotation.getElements(), "language");
        assertThat(language.getValueOld(), is("n.a."));
        assertThat(language.getValueNew(), is("de"));
    }
}
