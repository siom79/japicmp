package japicmp.util;

public abstract class Optional<T> {
	public abstract boolean isPresent();

	public abstract T get();

	public abstract Optional<T> or(Optional<? extends T> secondChoice);

	public abstract T or(T secondChoice);

	public abstract int hashCode();

	public abstract boolean equals(Object object);

	private static final class Present<T> extends Optional<T> {
		private final T reference;

		Present(T reference) {
			this.reference = reference;
		}

		@Override
		public boolean isPresent() {
			return true;
		}

		@Override
		public T get() {
			return this.reference;
		}

		@Override
		public Optional<T> or(Optional<? extends T> secondChoice) {
			return this;
		}

		@Override
		public T or(T secondChoice) {
			return this.reference;
		}

		@Override
		public int hashCode() {
			return 0x42;
		}

		@Override
		public boolean equals(Object object) {
			return object instanceof Present && this.reference.equals(((Present) object).reference);
		}

		@Override
		public String toString() {
			return this.reference.toString();
		}
	}

	private static final class Absent<T> extends Optional<T> {
		@SuppressWarnings("rawtypes")
		private static final Absent<?> INSTANCE = new Absent();

		@Override
		public boolean isPresent() {
			return false;
		}

		@Override
		public T get() {
			throw new IllegalStateException("value is absent.");
		}

		@Override
		public Optional<T> or(Optional<? extends T> secondChoice) {
			return this;
		}

		@Override
		public T or(T secondChoice) {
			return secondChoice;
		}

		@Override
		public int hashCode() {
			return 0x42;
		}

		@Override
		public boolean equals(Object object) {
			return object == this;
		}

		@Override
		public String toString() {
			return "value absent";
		}
	}

	public static <T> Optional<T> of(T reference) {
		if (reference == null) {
			throw new IllegalArgumentException("reference should not be null.");
		}
		return new Present<>(reference);
	}

	public static <T> Optional<T> fromNullable(T reference) {
		if (reference == null) {
			return new Absent<>();
		}
		return new Present<>(reference);
	}

	@SuppressWarnings("unchecked")
	public static <T> Optional<T> absent() {
		return (Optional<T>) Absent.INSTANCE;
	}
}
