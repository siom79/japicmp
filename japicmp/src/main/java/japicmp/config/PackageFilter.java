package japicmp.config;

import java.util.regex.Pattern;

import com.google.common.base.Objects;

public class PackageFilter {
    private final Pattern pattern;

    public PackageFilter(String packageName) {
        String regEx = packageName.replace(".", "\\.");
        regEx = regEx.replace("*", ".*");
        regEx = regEx + "(\\.[^\\.]+)*";
        pattern = Pattern.compile(regEx);
    }

    public boolean matches(String str) {
        return pattern.matcher(str).matches();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
        .add("pattern", pattern.pattern()) //
        .toString();
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(pattern.pattern());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PackageFilter) {
            PackageFilter that = (PackageFilter) obj;
            return java.util.Objects.equals(that.pattern.pattern(), this.pattern.pattern());
        } else {
            return false;
        }
    }
}
