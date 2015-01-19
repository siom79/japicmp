package japicmp.test.semver.removedInterfaceFromDefault;

class AClassImplements implements Comparable<String> {
	@Override
	public int compareTo(String o) {
		return 0;
	}
}
