package japicmp.test.semver.finalfield;

public class PublicClassWithFinalField {

	private String fieldChangesFromPrivateToPrivateFinal;

	public PublicClassWithFinalField(String fieldChangesFromPrivateToPrivateFinal) {
		this.fieldChangesFromPrivateToPrivateFinal = fieldChangesFromPrivateToPrivateFinal;
	}
}
