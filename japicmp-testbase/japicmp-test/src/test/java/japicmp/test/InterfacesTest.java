package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiField;
import japicmp.model.JApiMethod;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Serializable;
import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static japicmp.test.util.Helper.getJApiField;
import static japicmp.test.util.Helper.getJApiImplementedInterface;
import static japicmp.test.util.Helper.getJApiMethod;
import static japicmp.test.util.Helper.replaceLastDotWith$;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InterfacesTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void testInterfaceToNoInterface() {
		JApiClass interfaceToNoInterfaceClass = getJApiClass(jApiClasses, Interfaces.InterfaceToNoInterfaceClass.class.getName());
		assertThat(interfaceToNoInterfaceClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(interfaceToNoInterfaceClass.getInterfaces().size(), is(1));
		assertThat(getJApiImplementedInterface(interfaceToNoInterfaceClass.getInterfaces(), replaceLastDotWith$(Interfaces.TestInterface.class.getCanonicalName())).getChangeStatus(), is(JApiChangeStatus.REMOVED));
	}

	@Test
	public void testInterfaceChangesClass() {
		JApiClass interfaceChangesClass = getJApiClass(jApiClasses, Interfaces.InterfaceChangesClass.class.getName());
		assertThat(interfaceChangesClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(interfaceChangesClass.getInterfaces().size(), is(2));
		assertThat(getJApiImplementedInterface(interfaceChangesClass.getInterfaces(), replaceLastDotWith$(Interfaces.TestInterface.class.getCanonicalName())).getChangeStatus(), is(JApiChangeStatus.REMOVED));
		assertThat(getJApiImplementedInterface(interfaceChangesClass.getInterfaces(), replaceLastDotWith$(Interfaces.SecondTestInterface.class.getCanonicalName())).getChangeStatus(), is(JApiChangeStatus.NEW));
	}

	@Test
	public void testInterfaceRemainsInterface() {
		JApiClass interfaceRemainsInterfaceClass = getJApiClass(jApiClasses, Interfaces.InterfaceRemainsInterfaceClass.class.getName());
		assertThat(interfaceRemainsInterfaceClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(interfaceRemainsInterfaceClass.getInterfaces().size(), is(1));
		assertThat(getJApiImplementedInterface(interfaceRemainsInterfaceClass.getInterfaces(), replaceLastDotWith$(Interfaces.TestInterface.class.getCanonicalName())).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
	}

	@Test
	public void testNoInterfaceToWithInterface() {
		JApiClass noInterfaceToWithInterfaceClass = getJApiClass(jApiClasses, Interfaces.NoInterfaceToInterfaceClass.class.getName());
		assertThat(noInterfaceToWithInterfaceClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(noInterfaceToWithInterfaceClass.getInterfaces().size(), is(1));
		assertThat(getJApiImplementedInterface(noInterfaceToWithInterfaceClass.getInterfaces(), replaceLastDotWith$(Interfaces.TestInterface.class.getCanonicalName())).getChangeStatus(), is(JApiChangeStatus.NEW));
	}

	@Test
	public void testNoInterfaceToSerializableInterface() {
		JApiClass jApiClass = getJApiClass(jApiClasses, Interfaces.NoInterfaceToSerializableInterface.class.getName());
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getInterfaces().size(), is(1));
		assertThat(getJApiImplementedInterface(jApiClass.getInterfaces(), Serializable.class.getCanonicalName()).getChangeStatus(), is(JApiChangeStatus.NEW));
	}

	@Test
	public void testNewClassWithNewInterface() {
		JApiClass newClassWithNewInterface = getJApiClass(jApiClasses, "japicmp.test.Interfaces$NewClassWithNewInterface");
		assertThat(getJApiImplementedInterface(newClassWithNewInterface.getInterfaces(), Interfaces.TestInterface.class.getName()).getChangeStatus(), is(JApiChangeStatus.NEW));
		assertThat(getJApiImplementedInterface(newClassWithNewInterface.getInterfaces(), Interfaces.TestInterface.class.getName()).isBinaryCompatible(), is(true));
	}

	@Test
	public void testMethodPulledUpToSuperInterface() {
		JApiClass jApiClass = getJApiClass(jApiClasses, Interfaces.MethodPulledToSuperInterfaceBase.class.getName());
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		JApiMethod methodPulledUp = getJApiMethod(jApiClass.getMethods(), "methodPulledUp");
		assertThat(methodPulledUp.isBinaryCompatible(), is(true));
		jApiClass = getJApiClass(jApiClasses, Interfaces.MethodPulledToSuperInterfaceChild.class.getName());
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		methodPulledUp = getJApiMethod(jApiClass.getMethods(), "methodPulledUp");
		assertThat(methodPulledUp.isBinaryCompatible(), is(true));
	}

	@Test
	public void testFieldsOfInterface() {
		JApiClass interfaceWithFields = getJApiClass(jApiClasses, "japicmp.test.Interfaces$InterfaceWithFields");
		List<JApiField> fields = interfaceWithFields.getFields();
		assertThat(fields.size(), is(3));
		JApiField jApiField = getJApiField(fields, "ADDED");
		assertThat(jApiField.getChangeStatus(), is(JApiChangeStatus.NEW));
		jApiField = getJApiField(fields, "REMOVED");
		assertThat(jApiField.getChangeStatus(), is(JApiChangeStatus.REMOVED));
		jApiField = getJApiField(fields, "UNCHANGED");
		assertThat(jApiField.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(interfaceWithFields.isBinaryCompatible(), is(false));
	}

	@Test
	public void testInterfaceMethodAdded() {
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.test.Interfaces$InterfaceAddMethod");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(false));
	}
}
