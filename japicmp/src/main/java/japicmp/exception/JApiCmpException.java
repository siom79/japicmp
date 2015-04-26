package japicmp.exception;

public class JApiCmpException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final Reason reason;

	public enum Reason {
		CliError,
		NormalTermination,
		IoException,
		JaxbException,
		ClassLoading,
		IllegalState,
		XsltError
	}

	public JApiCmpException(Reason reason, String msg) {
		super(msg);
		this.reason = reason;
	}

	public JApiCmpException(Reason reason, String msg, Throwable t) {
		super(msg, t);
		this.reason = reason;
	}

	public Reason getReason() {
		return reason;
	}

	public static JApiCmpException of(Reason reason, String format, Object... args) {
		String msg = String.format(format, args);
		return new JApiCmpException(reason, msg);
	}
}
