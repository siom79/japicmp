package japicmp.test.semver.removedInterfaceFromPublic;

public class AClassImplements implements Comparable<String> {
	@Override
	public int compareTo(String o) {
		return 0;
	}
}
