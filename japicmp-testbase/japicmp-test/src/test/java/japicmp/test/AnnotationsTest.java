package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiAnnotation;
import japicmp.model.JApiAnnotationElement;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiField;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiAnnotation;
import static japicmp.test.util.Helper.getJApiAnnotationElement;
import static japicmp.test.util.Helper.getJApiClass;
import static japicmp.test.util.Helper.getJApiField;
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
		assertThat(language.getOldElementValues().size(), is(0));
		assertThat(language.getNewElementValues().size(), is(1));
		assertThat(language.getNewElementValues().get(0).getValueString(), is("de"));
	}

	@Test
	public void testFieldAnnotationRemoved() {
		JApiClass annotations = getJApiClass(jApiClasses, Annotations.class.getName());
		JApiField fieldAnnotationRemoved = getJApiField(annotations.getFields(), "fieldAnnotationRemoved");
		assertThat(fieldAnnotationRemoved.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		JApiAnnotation fieldAnnotation = getJApiAnnotation(fieldAnnotationRemoved.getAnnotations(), Annotations.FieldAnnotation.class.getName());
		assertThat(fieldAnnotation.getChangeStatus(), is(JApiChangeStatus.REMOVED));
	}

	@Test
	public void testFieldAnnotationAdded() {
		JApiClass annotations = getJApiClass(jApiClasses, Annotations.class.getName());
		JApiField fieldAnnotationAdded = getJApiField(annotations.getFields(), "fieldAnnotationAdded");
		assertThat(fieldAnnotationAdded.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		JApiAnnotation fieldAnnotation = getJApiAnnotation(fieldAnnotationAdded.getAnnotations(), Annotations.FieldAnnotation.class.getName());
		assertThat(fieldAnnotation.getChangeStatus(), is(JApiChangeStatus.NEW));
	}

	@Test
	public void testFieldAnnotationRemains() {
		JApiClass annotations = getJApiClass(jApiClasses, Annotations.class.getName());
		JApiField fieldAnnotationRemains = getJApiField(annotations.getFields(), "fieldAnnotationRemains");
		assertThat(fieldAnnotationRemains.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		JApiAnnotation fieldAnnotation = getJApiAnnotation(fieldAnnotationRemains.getAnnotations(), Annotations.FieldAnnotation.class.getName());
		assertThat(fieldAnnotation.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
	}

	@Test
	public void testFieldAnnotationValueNew() {
		JApiClass annotations = getJApiClass(jApiClasses, Annotations.class.getName());
		JApiField fieldAnnotationValueNew = getJApiField(annotations.getFields(), "fieldAnnotationValueNew");
		assertThat(fieldAnnotationValueNew.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		JApiAnnotation fieldAnnotation = getJApiAnnotation(fieldAnnotationValueNew.getAnnotations(), Annotations.FieldAnnotation.class.getName());
		assertThat(fieldAnnotation.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		JApiAnnotationElement element = getJApiAnnotationElement(fieldAnnotation.getElements(), "value");
		assertThat(element.getChangeStatus(), is(JApiChangeStatus.NEW));
	}

	@Test
	public void testFieldAnnotationValueRemoved() {
		JApiClass annotations = getJApiClass(jApiClasses, Annotations.class.getName());
		JApiField fieldAnnotationValueRemoved = getJApiField(annotations.getFields(), "fieldAnnotationValueRemoved");
		assertThat(fieldAnnotationValueRemoved.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		JApiAnnotation fieldAnnotation = getJApiAnnotation(fieldAnnotationValueRemoved.getAnnotations(), Annotations.FieldAnnotation.class.getName());
		assertThat(fieldAnnotation.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		JApiAnnotationElement element = getJApiAnnotationElement(fieldAnnotation.getElements(), "value");
		assertThat(element.getChangeStatus(), is(JApiChangeStatus.REMOVED));
	}
}
