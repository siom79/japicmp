package japicmp.model;

public enum JApiSemanticVersionLevel {
	MAJOR(2),
	MINOR(1),
	PATCH(0);

	private final int level;

	JApiSemanticVersionLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}
}
