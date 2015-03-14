package japicmp.test;

import com.google.common.base.Optional;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiJavaObjectSerializationCompatibility;
import japicmp.test.serialversion.*;
import javassist.CannotCompileException;
import javassist.CtClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class JavaObjectSerializationTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void testSerialVersionUnchanged() {
		JApiClass jApiClass = getJApiClass(jApiClasses, SerialVersionUnchanged.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE));
		assertThat(testSerialization(jApiClass), is(true));
	}

	@Test
	public void testNotSerializable() {
		JApiClass jApiClass = getJApiClass(jApiClasses, NotSerializable.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.NOT_SERIALIZABLE));
		assertThat(testSerialization(jApiClass), is(false));
	}

	@Test
	public void testUnchangedWithSerialVersionUid() {
		JApiClass jApiClass = getJApiClass(jApiClasses, UnchangedWithSerialVersionUid.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE));
		assertThat(testSerialization(jApiClass), is(true));
	}

	@Test
	public void testUnchangedWithSerialVersionUidChange() {
		JApiClass jApiClass = getJApiClass(jApiClasses, UnchangedWithSerialVersionUidChange.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE));
		assertThat(testSerialization(jApiClass), is(false));
	}

	@Test
	public void testExtendsSerializableClassUnchanged() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ExtendsSerializableClassUnchanged.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE));
		assertThat(testSerialization(jApiClass), is(true));
	}

	@Test
	public void testExtendsSerializableClassModified() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ExtendsSerializableClassModified.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE));
		assertThat(testSerialization(jApiClass), is(false));
	}

	@Test
	public void testModifiedButSerialVersionUidUnchanged() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ModifiedButSerialVersionUidUnchanged.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_BUT_SUID_EQUAL));
		assertThat(testSerialization(jApiClass), is(true));
	}

	@Test
	public void testModifiedFieldAddedButSerialVersionUidUnchanged() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ModifiedFieldAddedButSerialVersionUidUnchanged.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE));
		assertThat(testSerialization(jApiClass), is(true));
	}

	@Test
	public void testModifiedAndSerialVersionUidModified() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ModifiedAndSerialVersionUidModified.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE));
	}

	@Test
	public void testSerializableClassRemoved() {
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.test.serialversion.SerializableClassRemoved");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.REMOVED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE));
	}

	private boolean testSerialization(JApiClass jApiClass) {
		boolean successful = false;
		try {
			Optional<CtClass> oldClassOptional = jApiClass.getOldClass();
			if (oldClassOptional.isPresent()) {
				CtClass ctClass = oldClassOptional.get();
				Object oldClassInstance = ctClass.toClass(new URLClassLoader(new URL[]{}), JavaObjectSerializationTest.class.getProtectionDomain()).newInstance();
				ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(byteOutputStream);
				oos.writeObject(oldClassInstance);
				oos.flush();
				oos.close();
				ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(byteOutputStream.toByteArray()));
				Object readObject = ois.readObject();
				assertThat(readObject, notNullValue());
				successful = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return successful;
	}
}
