package japicmp.util;

import japicmp.model.AccessModifier;
import javassist.Modifier;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;

class ModifierHelperTest {

	@Test
	void publicToPublic() {
		MatcherAssert.assertThat(ModifierHelper.matchesModifierLevel(Modifier.setPublic(0), AccessModifier.PUBLIC), is(true));
	}

	@Test
	void publicToProtected() {
		MatcherAssert.assertThat(ModifierHelper.matchesModifierLevel(Modifier.setPublic(0), AccessModifier.PROTECTED), is(true));
	}

	@Test
	void publicToPrivate() {
		MatcherAssert.assertThat(ModifierHelper.matchesModifierLevel(Modifier.setPublic(0), AccessModifier.PRIVATE), is(true));
	}

	@Test
	void privateToPublic() {
		MatcherAssert.assertThat(ModifierHelper.matchesModifierLevel(Modifier.setPrivate(0), AccessModifier.PUBLIC), is(false));
	}

	@Test
	void privateToProtected() {
		MatcherAssert.assertThat(ModifierHelper.matchesModifierLevel(Modifier.setPrivate(0), AccessModifier.PROTECTED), is(false));
	}

	@Test
	void privateToPrivate() {
		MatcherAssert.assertThat(ModifierHelper.matchesModifierLevel(Modifier.setPrivate(0), AccessModifier.PRIVATE), is(true));
	}
}
