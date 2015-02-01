package japicmp.test.semver.finalfield;

public class PublicClassWithFinalField {

	private final String fieldChangesFromPrivateToPrivateFinal;

	public PublicClassWithFinalField(String fieldChangesFromPrivateToPrivateFinal) {
		this.fieldChangesFromPrivateToPrivateFinal = fieldChangesFromPrivateToPrivateFinal;
	}
}
