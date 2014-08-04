package japicmp.test;

public class Fields {
	private int privateToPublicField;
	private int privateToProtectedField;
	private int privateToPackageProctedField;
	private int privateRemainsPrivateField;
	public int publicToPrivateField;
	public int publicToProtectedField;
	public int publicToPackageProtectedField;
	public int publicRemainsPublicField;
	public static int staticToNonStaticField;
	public int nonStaticToStaticField;
	public int nonStaticStaysNonStaticField;
	public static int staticRemainsStaticField;
	public final int finalToNonFinalField = 0;
	public int nonfinalToFinalField;
	public int nonfinalStaysNonFinalField;
	public final int finalRemainsFinalField = 0;
}
