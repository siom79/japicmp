package japicmp;

import io.airlift.command.ParseException;
import io.airlift.command.SingleCommand;
import japicmp.cli.JApiCli;
import japicmp.exception.JApiCmpException;

public class JApiCmp {

    public static void main(String[] args) {

        try {
            run(args);
        } catch (ParseException e) {
            System.err.println("E: " + e.getMessage());
            run(new String[] { "--help" });
            System.exit(-1);
        } catch (JApiCmpException e) {
            if (e.getReason() != JApiCmpException.Reason.NormalTermination) {
                System.err.println("E: " + e.getMessage());
                run(new String[] { "--help" });
                System.exit(-1);
            }
        } catch (Exception e) {
            System.err.println(String.format("Execution of %s failed: %s", JApiCmp.class.getSimpleName(), e.getMessage()));
            e.printStackTrace();
            System.exit(-2);
        }
    }

    private static void run(String[] args) {
        JApiCli.Compare cmd = SingleCommand.singleCommand(JApiCli.Compare.class).parse(args);
        if (!cmd.helpOption.showHelpIfRequested()) {
            cmd.run();
        }
    }

}
