package japicmp.model;

public enum AccessModifier {
    PUBLIC(3), PACKAGE(2), PROTECTED(1), PRIVATE(0);

    private int level;

    AccessModifier(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
