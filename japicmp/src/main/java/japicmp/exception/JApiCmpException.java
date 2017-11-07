package japicmp.exception;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;

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
		IllegalArgument,
		XsltError,
		IncompatibleChange
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

	public static JApiCmpException cliError(String format, Object... args) {
		return of(Reason.CliError, format, args);
	}

	public static JApiCmpException of(Reason reason, String format, Object... args) {
		String msg = String.format(format, args);
		return new JApiCmpException(reason, msg);
	}

	public static JApiCmpException forClassLoading(Exception e, String name, JarArchiveComparator jarArchiveComparator) {
		String classPathAsString = "(CLASSPATH=" + jarArchiveComparator.getCommonClasspathAsString() + ")";
		if (jarArchiveComparator.getJarArchiveComparatorOptions().getClassPathMode() == JarArchiveComparatorOptions.ClassPathMode.TWO_SEPARATE_CLASSPATHS) {
			classPathAsString = "(OLD CLASSPATH=" + jarArchiveComparator.getOldClassPathAsString() + " / NEW CLASSPATH=" + jarArchiveComparator.getNewClassPathAsString() + ")";
		}
		return new JApiCmpException(JApiCmpException.Reason.ClassLoading, "Could not load '" + name + "': " + e.getMessage() + ". Please make sure that all libraries have been added to the classpath " + classPathAsString + " or try the option '--ignore-missing-classes'.", e);
	}

	public static JApiCmpException forClassLoading(String name, JarArchiveComparator jarArchiveComparator) {
		String classPathAsString = "(CLASSPATH=" + jarArchiveComparator.getCommonClasspathAsString() + ")";
		if (jarArchiveComparator.getJarArchiveComparatorOptions().getClassPathMode() == JarArchiveComparatorOptions.ClassPathMode.TWO_SEPARATE_CLASSPATHS) {
			classPathAsString = "(OLD CLASSPATH=" + jarArchiveComparator.getOldClassPathAsString() + " / NEW CLASSPATH=" + jarArchiveComparator.getNewClassPathAsString() + ")";
		}
		return new JApiCmpException(JApiCmpException.Reason.ClassLoading, "Could not load '" + name + "'. Please make sure that all libraries have been added to the classpath " + classPathAsString + " or try the option '--ignore-missing-classes'.");
	}
}
