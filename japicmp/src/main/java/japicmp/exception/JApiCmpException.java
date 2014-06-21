package japicmp.exception;

public class JApiCmpException extends RuntimeException {
    private final Reason reason;

    public JApiCmpException(Reason reason) {
        this.reason = reason;
    }

    public enum Reason {
        IllegalArgument, NormalTermination
    }

    public JApiCmpException(Reason reason, String msg) {
        super(msg);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
