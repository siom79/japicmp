package japicmp.exception;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import javassist.NotFoundException;

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

	public static JApiCmpException forClassNotFound(NotFoundException e, String name, JarArchiveComparator jarArchiveComparator) {
		return new JApiCmpException(JApiCmpException.Reason.ClassLoading, "Could not load '" + name + "': " + e.getMessage() + ". Please make sure that all libraries have been added to the classpath (CLASSPATH=" + jarArchiveComparator.getClasspath() + ") or try the option '--ignore-missing-classes'.", e);
	}
}
