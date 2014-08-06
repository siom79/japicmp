package japicmp.test;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static japicmp.test.util.Helper.getJApiImplementedInterface;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;

import java.util.List;

import org.junit.Test;

public class IntefacesTest {

	@Test
	public void test() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setModifierLevel(AccessModifier.PRIVATE);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		JApiClass interfaceToNoInterfaceClass = getJApiClass(jApiClasses, Interfaces.InterfaceToNoInterfaceClass.class.getName());
		assertThat(getJApiImplementedInterface(interfaceToNoInterfaceClass.getInterfaces(), replaceLastDotWith$(Interfaces.TestInterface.class.getCanonicalName())).getChangeStatus(), is(JApiChangeStatus.REMOVED));
		JApiClass interfaceChangesClass = getJApiClass(jApiClasses, Interfaces.InterfaceChangesClass.class.getName());
		assertThat(getJApiImplementedInterface(interfaceChangesClass.getInterfaces(), replaceLastDotWith$(Interfaces.TestInterface.class.getCanonicalName())).getChangeStatus(), is(JApiChangeStatus.REMOVED));
		assertThat(getJApiImplementedInterface(interfaceChangesClass.getInterfaces(), replaceLastDotWith$(Interfaces.SecondTestInterface.class.getCanonicalName())).getChangeStatus(), is(JApiChangeStatus.NEW));
		JApiClass interfaceRemainsInterfaceClass = getJApiClass(jApiClasses, Interfaces.InterfaceRemainsInterfaceClass.class.getName());
		assertThat(getJApiImplementedInterface(interfaceRemainsInterfaceClass.getInterfaces(), replaceLastDotWith$(Interfaces.TestInterface.class.getCanonicalName())).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
	}
	
	private String replaceLastDotWith$(String str) {
		int lastIndex = str.lastIndexOf('.');
		if (lastIndex > -1) {
			if (lastIndex == 0) {
				if (str.length() > 1) {
					str = "$" + str.substring(1);
				} else {
					str = "$";
				}
			} else {
				if (str.length() > lastIndex + 1) {
					str = str.substring(0, lastIndex) + "$" + str.substring(lastIndex + 1);
				} else {
					str = str.substring(0, lastIndex) + "$";
				}
			}
		}
		return str;
	}
}
