package japicmp.test.annotation.filter;

public class ClassWithMembersToExclude {
	@Exclude
	public String CONSTANT = "C";

	public void methodIncluded() {

	}

	@Exclude
	public void methodToExclude() {

	}
}
