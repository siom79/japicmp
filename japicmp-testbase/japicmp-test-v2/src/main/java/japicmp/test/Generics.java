package japicmp.test;

import java.util.List;
import java.util.Map;

public class Generics<T, U extends List<Long>>  {

	public List<Long> list;

	public Generics(GenericsParamTest<Short, Integer> p) {

	}

	public void method1(List<Integer> strings) {

	}

	private static class GenericsParamTest<T, U> {

		public void m(T t, U u) {

		}
	}

	private static class GenericsReturnValueTest<T, U> {

	}

	public GenericsReturnValueTest<String, Integer> methodWithGenericArgs(Map<GenericsParamTest<String, Integer>, Long> map, GenericsParamTest<Byte, Short> param) {
		return null;
	}

	public void methodWithArray(GenericsParamTest<Byte[], Short>[] params) {

	}

	public GenericsReturnValueTest<Integer, String> methodWithReturnType() {
		return null;
	}

	public void methodWithGenericExtends(List<? extends Long> l) {
	}

	public void methodWithGenericSuper(List<? super Integer> l) {
	}

	public void methodWithGenericQuestionMark(GenericsParamTest<?, Short>[] param, List<? extends Integer> l2) {
	}

	public <T extends Integer, U extends Short> U methodWithTemplate(T t, U u) {
		return null;
	}

	public <X extends List<Long>> X methodX(X test) {
		return null;
	}

	public interface MyInterface<T> {

	}

	public <X extends List<Integer> & MyInterface<Integer>> X methodWithAmpersandTemplate(X x) {
		return null;
	}
}
