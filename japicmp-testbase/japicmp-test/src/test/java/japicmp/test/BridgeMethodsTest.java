package japicmp.test;

import japicmp.model.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.compareTestV1WithTestV2;
import static japicmp.test.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BridgeMethodsTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		jApiClasses = compareTestV1WithTestV2(AccessModifier.PROTECTED);
	}

	@Test
	public void testBridgeModiferSet() {
		JApiClass jApiClass = getJApiClass(jApiClasses, BridgeMethods.MyNode.class.getName());
		boolean setDataWithObjectArgFound = false;
		boolean setDataWithIntegerArgFound = false;
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
		}
		assertThat(setDataWithObjectArgFound, is(true));
		assertThat(setDataWithIntegerArgFound, is(true));
	}
}
