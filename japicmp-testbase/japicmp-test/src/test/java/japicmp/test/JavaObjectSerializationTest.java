package japicmp.test;

import japicmp.util.Optional;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiJavaObjectSerializationCompatibility;
import japicmp.test.serialversion.CompatibleChanges;
import japicmp.test.serialversion.ExtendsSerializableClassModified;
import japicmp.test.serialversion.ExtendsSerializableClassUnchanged;
import japicmp.test.serialversion.IncompatibleChanges;
import japicmp.test.serialversion.ModifiedAndSerialVersionUidModified;
import japicmp.test.serialversion.ModifiedButSerialVersionUidUnchanged;
import japicmp.test.serialversion.ModifiedFieldAddedButSerialVersionUidUnchanged;
import japicmp.test.serialversion.NotSerializable;
import japicmp.test.serialversion.SerialVersionUnchanged;
import japicmp.test.serialversion.UnchangedWithSerialVersionUid;
import japicmp.test.serialversion.UnchangedWithSerialVersionUidChange;
import javassist.CtClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class JavaObjectSerializationTest {
	private static final Logger LOGGER = Logger.getLogger(JavaObjectSerializationTest.class.getName());
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setAccessModifier(AccessModifier.PRIVATE);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void testSerialVersionUnchanged() {
		JApiClass jApiClass = getJApiClass(jApiClasses, SerialVersionUnchanged.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(true));
	}

	@Test
	public void testNotSerializable() {
		JApiClass jApiClass = getJApiClass(jApiClasses, NotSerializable.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.NOT_SERIALIZABLE));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(false));
	}

	@Test
	public void testUnchangedWithSerialVersionUid() {
		JApiClass jApiClass = getJApiClass(jApiClasses, UnchangedWithSerialVersionUid.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(true));
	}

	@Test
	public void testUnchangedWithSerialVersionUidChange() {
		JApiClass jApiClass = getJApiClass(jApiClasses, UnchangedWithSerialVersionUidChange.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_SERIALVERSIONUID_MODIFIED));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(false));
	}

	@Test
	public void testExtendsSerializableClassUnchanged() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ExtendsSerializableClassUnchanged.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(true));
	}

	@Test
	public void testExtendsSerializableClassModified() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ExtendsSerializableClassModified.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_FIELD_REMOVED));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(false));
	}

	@Test
	public void testModifiedButSerialVersionUidUnchanged() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ModifiedButSerialVersionUidUnchanged.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_FIELD_REMOVED));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(true));
	}

	@Test
	public void testModifiedFieldAddedButSerialVersionUidUnchanged() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ModifiedFieldAddedButSerialVersionUidUnchanged.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(true));
	}

	@Test
	public void testModifiedAndSerialVersionUidModified() {
		JApiClass jApiClass = getJApiClass(jApiClasses, ModifiedAndSerialVersionUidModified.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_FIELD_REMOVED));
	}

	@Test
	public void testSerializableClassRemoved() {
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.test.serialversion.SerializableClassRemoved");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.REMOVED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_CLASS_REMOVED));
	}

	@Test
	public void testFieldRemoved() {
		JApiClass jApiClass = getJApiClass(jApiClasses, IncompatibleChanges.FieldRemoved.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_FIELD_REMOVED));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(true));
	}

	@Test
	public void testNonStaticFieldToStaticField() {
		JApiClass jApiClass = getJApiClass(jApiClasses, IncompatibleChanges.NonStaticFieldToStaticField.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_FIELD_CHANGED_FROM_NONSTATIC_TO_STATIC));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(false));
	}

	@Test
	public void testNonTransientFieldToTransientField() {
		JApiClass jApiClass = getJApiClass(jApiClasses, IncompatibleChanges.NonTransientFieldToTransientField.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_FIELD_CHANGED_FROM_NONTRANSIENT_TO_TRANSIENT));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(false));
	}

	@Test
	public void testTypeOfFieldChanges() {
		JApiClass jApiClass = getJApiClass(jApiClasses, IncompatibleChanges.TypeOfFieldChanges.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_FIELD_TYPE_MODIFIED));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(false));
	}

	@Test
	public void testSerializableToExternalizable() {
		JApiClass jApiClass = getJApiClass(jApiClasses, IncompatibleChanges.SerializableToExternalizable.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_CHANGED_FROM_SERIALIZABLE_TO_EXTERNALIZABLE));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(false));
	}

	@Test
	public void testExternalizableToSerializable() {
		JApiClass jApiClass = getJApiClass(jApiClasses, IncompatibleChanges.ExternalizableToSerializable.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_CHANGED_FROM_EXTERNALIZABLE_TO_SERIALIZABLE));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(false));
	}

	@Test
	public void testEnumToNonEnum() {
		JApiClass jApiClass = getJApiClass(jApiClasses, IncompatibleChanges.EnumToNonEnum.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_CLASS_TYPE_MODIFIED));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(false));
	}

	@Test
	public void testNonEnumToEnum() {
		JApiClass jApiClass = getJApiClass(jApiClasses, IncompatibleChanges.NonEnumToEnum.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_CLASS_TYPE_MODIFIED));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(false));
	}

	@Test
	public void testSerializableRemoved() {
		JApiClass jApiClass = getJApiClass(jApiClasses, IncompatibleChanges.SerializableRemoved.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_SERIALIZABLE_REMOVED));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(false));
	}

	@Test
	public void testExternalizableRemoved() {
		JApiClass jApiClass = getJApiClass(jApiClasses, IncompatibleChanges.ExternalizableRemoved.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_EXTERNALIZABLE_REMOVED));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(false));
	}

	@Test
	public void testFieldAddedClassWithoutSerialVersionUid() {
		JApiClass jApiClass = getJApiClass(jApiClasses, CompatibleChanges.FieldAddedClassWithoutSerialVersionUid.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_DEFAULT_SERIALVERSIONUID_CHANGED));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(false));
	}

	@Test
	public void testFieldAddedClassWithSerialVersionUid() {
		JApiClass jApiClass = getJApiClass(jApiClasses, CompatibleChanges.FieldAddedClassWithSerialVersionUid.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(true));
	}

	@Test
	public void testClassAdded() {
		JApiClass jApiClass = getJApiClass(jApiClasses, CompatibleChanges.ClassAdded.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.NEW));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE));
	}

	@Test
	public void testClassRemoved() {
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.test.serialversion.CompatibleChanges$ClassRemoved");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.REMOVED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_CLASS_REMOVED));
	}

	@Test
	public void testAddedSerializable() {
		JApiClass jApiClass = getJApiClass(jApiClasses, CompatibleChanges.AddedSerializable.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(false));
	}

	@Test
	public void testFieldAccessChangeFromPublicToPrivate() {
		JApiClass jApiClass = getJApiClass(jApiClasses, CompatibleChanges.FieldAccessChangeFromPublicToPrivate.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE));
		assertThat(serializationAndDeserializationSuccessful(jApiClass), is(true));
	}

	private boolean serializationAndDeserializationSuccessful(JApiClass jApiClass) {
		boolean successful = false;
		try {
			ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
			Optional<CtClass> oldClassOptional = jApiClass.getOldClass();
			if (oldClassOptional.isPresent()) {
				CtClass ctClass = oldClassOptional.get();
				Object oldClassInstance = ctClass.toClass(new URLClassLoader(new URL[]{}), JavaObjectSerializationTest.class.getProtectionDomain()).newInstance();
				ObjectOutputStream oos = new ObjectOutputStream(byteOutputStream);
				oos.writeObject(oldClassInstance);
				oos.flush();
				oos.close();
				byte[] bytes = byteOutputStream.toByteArray();
				ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
				Object readObject = ois.readObject();
				assertThat(readObject, notNullValue());
				successful = true;
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Serialization failed: " + e.getMessage(), e);
		}
		return successful;
	}
}
