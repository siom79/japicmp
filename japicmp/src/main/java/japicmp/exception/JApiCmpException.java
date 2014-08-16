package japicmp.exception;

public class JApiCmpException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final Reason reason;

    public JApiCmpException(Reason reason) {
        this.reason = reason;
    }

    public enum Reason {
        IllegalArgument, NormalTermination, CliMissingMandatoryOption, CliMissingArgumentForOption
    }

    public JApiCmpException(Reason reason, String msg) {
        super(msg);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
