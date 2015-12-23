package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClassTypeTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void testClassToClass() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassType.ClassToClass.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getClassType().getOldType(), is("CLASS"));
		assertThat(jApiClass.getClassType().getNewType(), is("CLASS"));
	}

	@Test
	public void testClassToInterface() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassType.ClassToInterface.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getClassType().getOldType(), is("CLASS"));
		assertThat(jApiClass.getClassType().getNewType(), is("INTERFACE"));
	}

	@Test
	public void testClassToAnnotation() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassType.ClassToAnnotation.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getClassType().getOldType(), is("CLASS"));
		assertThat(jApiClass.getClassType().getNewType(), is("ANNOTATION"));
	}

	@Test
	public void testClassToEnum() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassType.ClassToEnum.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getClassType().getOldType(), is("CLASS"));
		assertThat(jApiClass.getClassType().getNewType(), is("ENUM"));
	}

	@Test
	public void testInterfaceToClass() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassType.InterfaceToClass.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getClassType().getOldType(), is("INTERFACE"));
		assertThat(jApiClass.getClassType().getNewType(), is("CLASS"));
	}

	@Test
	public void testInterfaceToInterface() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassType.InterfaceToInterface.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getClassType().getOldType(), is("INTERFACE"));
		assertThat(jApiClass.getClassType().getNewType(), is("INTERFACE"));
	}

	@Test
	public void testInterfaceToAnnotation() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassType.InterfaceToAnnotation.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getClassType().getOldType(), is("INTERFACE"));
		assertThat(jApiClass.getClassType().getNewType(), is("ANNOTATION"));
	}

	@Test
	public void testInterfaceToEnum() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassType.InterfaceToEnum.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getClassType().getOldType(), is("INTERFACE"));
		assertThat(jApiClass.getClassType().getNewType(), is("ENUM"));
	}

	@Test
	public void testAnnotationToClass() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassType.AnnotationToClass.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getClassType().getOldType(), is("ANNOTATION"));
		assertThat(jApiClass.getClassType().getNewType(), is("CLASS"));
	}

	@Test
	public void testAnnotationToInterface() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassType.AnnotationToInterface.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getClassType().getOldType(), is("ANNOTATION"));
		assertThat(jApiClass.getClassType().getNewType(), is("INTERFACE"));
	}

	@Test
	public void testAnnotationToAnnotation() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassType.AnnotationToAnnotation.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getClassType().getOldType(), is("ANNOTATION"));
		assertThat(jApiClass.getClassType().getNewType(), is("ANNOTATION"));
	}

	@Test
	public void testAnnotationToEnum() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassType.AnnotationToEnum.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getClassType().getOldType(), is("ANNOTATION"));
		assertThat(jApiClass.getClassType().getNewType(), is("ENUM"));
	}

	@Test
	public void testEnumToClass() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassType.EnumToClass.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getClassType().getOldType(), is("ENUM"));
		assertThat(jApiClass.getClassType().getNewType(), is("CLASS"));
	}

	@Test
	public void testEnumToInterface() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassType.EnumToInterface.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getClassType().getOldType(), is("ENUM"));
		assertThat(jApiClass.getClassType().getNewType(), is("INTERFACE"));
	}

	@Test
	public void testEnumToAnnotation() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassType.EnumToAnnotation.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getClassType().getOldType(), is("ENUM"));
		assertThat(jApiClass.getClassType().getNewType(), is("ANNOTATION"));
	}

	@Test
	public void testEnumToEnum() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ClassType.EnumToEnum.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getClassType().getOldType(), is("ENUM"));
		assertThat(jApiClass.getClassType().getNewType(), is("ENUM"));
	}
}
