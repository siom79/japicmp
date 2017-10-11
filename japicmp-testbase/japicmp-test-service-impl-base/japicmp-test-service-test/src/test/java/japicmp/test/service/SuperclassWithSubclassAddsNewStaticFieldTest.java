package japicmp.test.service;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiField;
import japicmp.model.JApiSuperclass;
import japicmp.util.Optional;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static japicmp.test.service.util.Helper.createClassPath;
import static japicmp.test.service.util.Helper.getArchive;
import static japicmp.test.service.util.Helper.getJApiClass;
import static japicmp.test.service.util.Helper.getJApiField;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SuperclassWithSubclassAddsNewStaticFieldTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		initLogging();
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setClassPathMode(JarArchiveComparatorOptions.ClassPathMode.TWO_SEPARATE_CLASSPATHS);
		options.setOldClassPath(createClassPath("v1"));
		options.setNewClassPath(createClassPath("v2"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-service-impl-v1.jar"), getArchive("japicmp-test-service-impl-v2.jar"));
	}

	private static void initLogging() {
		Logger root = Logger.getLogger("");
		for (Handler handler : root.getHandlers()) {
			handler.setLevel(Level.FINE);
		}
	}

	/**
	 * The class SuperclassWithSubclassAddsNewStaticField gets in version v2 a new field of type int. The subclass
	 * SubclassAddsNewStaticField gets in version v2 a new static(!) field with the same name. This violates the rule
	 * 13.4.8 of the "Java Language Specification" (https://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html).
	 */
	@Test
	public void testStaticFieldInSubclassBinaryIncompatible() {
		JApiClass jApiClass = getJApiClass(jApiClasses, SubclassAddsNewStaticField.class.getName());
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiSuperclass superclass = jApiClass.getSuperclass();
		Optional<JApiClass> superclassJApiClass = superclass.getJApiClass();
		assertThat(superclassJApiClass.get().getFields().size(), is(1));
		JApiField field = getJApiField(superclassJApiClass.get().getFields(), "field");
		assertThat(field.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
	}
}
