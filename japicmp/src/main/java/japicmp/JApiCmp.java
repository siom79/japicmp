package japicmp;

import io.airlift.command.Help;
import io.airlift.command.ParseException;
import io.airlift.command.SingleCommand;
import japicmp.cli.JApiCli;
import japicmp.exception.JApiCmpException;

public class JApiCmp {

	public static void main(String[] args) {
		SingleCommand<JApiCli.Compare> singleCommand = SingleCommand.singleCommand(JApiCli.Compare.class);
		try {
			JApiCli.Compare cmd = singleCommand.parse(args);
			if (!cmd.helpOption.showHelpIfRequested()) {
				cmd.run();
			}
		} catch (ParseException e) {
			System.err.println("E: " + e.getMessage());
			Help.help(singleCommand.getCommandMetadata());
			System.exit(-1);
		} catch (JApiCmpException e) {
			if (e.getReason() != JApiCmpException.Reason.NormalTermination) {
				System.err.println("E: " + e.getMessage());
				Help.help(singleCommand.getCommandMetadata());
				System.exit(-1);
			}
		} catch (Exception e) {
			System.err.println(String.format("Execution of %s failed: %s", JApiCmp.class.getSimpleName(), e.getMessage()));
			e.printStackTrace();
			System.exit(-2);
		}
	}
}
