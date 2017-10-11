package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.BridgeModifier;
import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;
import japicmp.model.SyntheticModifier;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BridgeMethodsTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void testBridgeModifierSet() {
		JApiClass jApiClass = getJApiClass(jApiClasses, BridgeMethods.MyNode.class.getName());
		boolean setDataWithObjectArgFound = false;
		boolean setDataWithIntegerArgFound = false;
		boolean getDataWithObjectReturnTypeFound = false;
		boolean getDataWithIntegerReturnTypeFound = false;
		for (JApiMethod jApiMethod : jApiClass.getMethods()) {
			String name = jApiMethod.getName();
			if (name.equals("setData")) {
				if (jApiMethod.getParameters().get(0).getType().equals("java.lang.Object")) {
					setDataWithObjectArgFound = true;
					assertThat(jApiMethod.getBridgeModifier().getOldModifier().get(), is(BridgeModifier.BRIDGE));
					assertThat(jApiMethod.getBridgeModifier().getNewModifier().get(), is(BridgeModifier.BRIDGE));
					assertThat(jApiMethod.getSyntheticModifier().getOldModifier().get(), is(SyntheticModifier.SYNTHETIC));
					assertThat(jApiMethod.getSyntheticModifier().getNewModifier().get(), is(SyntheticModifier.SYNTHETIC));
				}
				if (jApiMethod.getParameters().get(0).getType().equals("java.lang.Integer")) {
					setDataWithIntegerArgFound = true;
					assertThat(jApiMethod.getBridgeModifier().getOldModifier().get(), is(BridgeModifier.NON_BRIDGE));
					assertThat(jApiMethod.getBridgeModifier().getNewModifier().get(), is(BridgeModifier.NON_BRIDGE));
					assertThat(jApiMethod.getSyntheticModifier().getOldModifier().get(), is(SyntheticModifier.NON_SYNTHETIC));
					assertThat(jApiMethod.getSyntheticModifier().getNewModifier().get(), is(SyntheticModifier.NON_SYNTHETIC));
				}
			}
			if (name.equals("getData")) {
				if (jApiMethod.getReturnType().getNewReturnType().equals("java.lang.Object")) {
					getDataWithObjectReturnTypeFound = true;
					assertThat(jApiMethod.getBridgeModifier().getNewModifier().get(), is(BridgeModifier.BRIDGE));
					assertThat(jApiMethod.getSyntheticModifier().getNewModifier().get(), is(SyntheticModifier.SYNTHETIC));
				}
				if (jApiMethod.getReturnType().getNewReturnType().equals("java.lang.Integer")) {
					getDataWithIntegerReturnTypeFound = true;
					assertThat(jApiMethod.getBridgeModifier().getNewModifier().get(), is(BridgeModifier.NON_BRIDGE));
					assertThat(jApiMethod.getSyntheticModifier().getNewModifier().get(), is(SyntheticModifier.NON_SYNTHETIC));
				}
			}
		}
		assertThat(setDataWithObjectArgFound, is(true));
		assertThat(setDataWithIntegerArgFound, is(true));
		assertThat(getDataWithObjectReturnTypeFound, is(true));
		assertThat(getDataWithIntegerReturnTypeFound, is(true));
	}
}
