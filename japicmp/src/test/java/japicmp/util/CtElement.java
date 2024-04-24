package japicmp.util;

import java.util.function.Function;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class CtElement {

	public final String name;
	public final Function<ConstPool, MemberValue> value;

	private CtElement(String name, Function<ConstPool, MemberValue> value) {
		this.name = name;
		this.value = value;
	}

	public CtElement(String name, boolean value) {
		this(name, cp -> new BooleanMemberValue(value, cp));
	}

	public CtElement(String name, byte value) {
		this(name, cp -> new ByteMemberValue(value, cp));
	}

	public CtElement(String name, char value) {
		this(name, cp -> new CharMemberValue(value, cp));
	}

	public CtElement(String name, short value) {
		this(name, cp -> new ShortMemberValue(value, cp));
	}

	public CtElement(String name, int value) {
		this(name, cp -> new IntegerMemberValue(cp, value));
	}

	public CtElement(String name, long value) {
		this(name, cp -> new LongMemberValue(value, cp));
	}

	public CtElement(String name, float value) {
		this(name, cp -> new FloatMemberValue(value, cp));
	}

	public CtElement(String name, double value) {
		this(name, cp -> new DoubleMemberValue(value, cp));
	}

	public CtElement(String name, Class<?> value) {
		this(name, cp -> new ClassMemberValue(value.getName(), cp));
	}

	public CtElement(String name, String value) {
		this(name, cp -> new StringMemberValue(value, cp));
	}
}
