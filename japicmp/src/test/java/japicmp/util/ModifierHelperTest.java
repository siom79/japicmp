package japicmp.util;

import japicmp.model.AccessModifier;
import javassist.Modifier;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ModifierHelperTest {

	@Test
	public void publicToPublic() {
		assertThat(ModifierHelper.matchesModifierLevel(Modifier.setPublic(0), AccessModifier.PUBLIC), is(true));
	}

	@Test
	public void publicToProtected() {
		assertThat(ModifierHelper.matchesModifierLevel(Modifier.setPublic(0), AccessModifier.PROTECTED), is(true));
	}

	@Test
	public void publicToPrivate() {
		assertThat(ModifierHelper.matchesModifierLevel(Modifier.setPublic(0), AccessModifier.PRIVATE), is(true));
	}

	@Test
	public void privateToPublic() {
		assertThat(ModifierHelper.matchesModifierLevel(Modifier.setPrivate(0), AccessModifier.PUBLIC), is(false));
	}

	@Test
	public void privateToProtected() {
		assertThat(ModifierHelper.matchesModifierLevel(Modifier.setPrivate(0), AccessModifier.PROTECTED), is(false));
	}

	@Test
	public void privateToPrivate() {
		assertThat(ModifierHelper.matchesModifierLevel(Modifier.setPrivate(0), AccessModifier.PRIVATE), is(true));
	}
}
