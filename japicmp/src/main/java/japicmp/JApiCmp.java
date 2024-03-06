package japicmp;

import japicmp.cli.JApiCli;
import japicmp.exception.JApiCmpException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JApiCmp {
	private static final Logger LOGGER = Logger.getLogger(JApiCmp.class.getName());
	static final String USE_HELP_OR_H_FOR_MORE_INFORMATION = "See '--help' or '-h' for more information.";
	private static final String CAUGHT_EXCEPTION = "Caught exception: ";

	public interface SystemExit {
		void exit(int status);
	}

	public static class SystemExitDefault implements SystemExit {

		@Override
		public void exit(int status) {
			System.exit(status);
		}
	}

	static SystemExit systemExit = new SystemExitDefault();

	private JApiCmp() {
		throw new IllegalAccessError("Illegal access.");
	}

	public static void main(String[] args) {
		try {
			JApiCli jApiCli = new JApiCli();
			jApiCli.run(args);
		} catch (JApiCmpException e) {
			LOGGER.log(Level.FINE, CAUGHT_EXCEPTION + e.getLocalizedMessage(), e);
			if (e.getReason() != JApiCmpException.Reason.NormalTermination) {
				System.err.println("E: " + e.getMessage());
				System.out.println(USE_HELP_OR_H_FOR_MORE_INFORMATION);
				systemExit.exit(1);
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, CAUGHT_EXCEPTION + e.getLocalizedMessage(), e);
			System.err.printf("Execution of %s failed: %s%n", JApiCmp.class.getSimpleName(), e.getMessage());
			e.printStackTrace();
			systemExit.exit(1);
		}
	}
}
