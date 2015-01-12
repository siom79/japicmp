package japicmp.cli;

import static com.google.common.collect.Lists.newArrayList;
import static io.airlift.command.UsageHelper.toSynopsisUsage;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.airlift.command.Option;
import io.airlift.command.UsageHelper;
import io.airlift.command.UsagePrinter;
import io.airlift.command.model.ArgumentsMetadata;
import io.airlift.command.model.CommandMetadata;
import io.airlift.command.model.OptionMetadata;

public class ShortHelpOption {

	@Inject
	public CommandMetadata commandMetadata;

	@Option(name = { "-h", "--help" }, description = "Display help information")
	public Boolean help = false;

	public boolean showHelpIfRequested() {
		if (help) {
			help(commandMetadata);
		}
		return help;
	}

	static void help(CommandMetadata metadata) {
		StringBuilder stringBuilder = new StringBuilder();
		usage(metadata.getName(), metadata, newUsagePrinter(stringBuilder));
		System.out.println(stringBuilder.toString());
	}

	private static UsagePrinter newUsagePrinter(StringBuilder stringBuilder) {
		return new UsagePrinter(stringBuilder, 79);
	}

	public static void shortHelp(CommandMetadata metadata) {
		StringBuilder stringBuilder = new StringBuilder();
		new SynopsisPrint(metadata.getName(), metadata, newUsagePrinter(stringBuilder)) //
				.invoke(0, 2);
		System.out.println(stringBuilder);
	}

	static void usage(String commandName, CommandMetadata metadata, UsagePrinter out) {
		//
		// NAME
		//
		out.append("NAME").newline();

		out.newIndentedPrinter(8).append(commandName).append("-").append(metadata.getDescription())
				.newline().newline();

		//
		// SYNOPSIS
		//
		out.append("SYNOPSIS").newline();
		SynopsisPrint synopsisPrint = new SynopsisPrint(commandName, metadata, out).invoke(8, 8);
		List<OptionMetadata> options = synopsisPrint.getOptions();
		ArgumentsMetadata arguments = synopsisPrint.getArguments();

		//
		// OPTIONS
		//
		if (options.size() > 0 || arguments != null) {
			options = sortOptions(options);

			out.append("OPTIONS").newline();

			for (OptionMetadata option : options) {
				// skip hidden options
				if (option.isHidden()) {
					continue;
				}

				// option names
				UsagePrinter optionPrinter = out.newIndentedPrinter(8);
				optionPrinter.append(UsageHelper.toDescription(option)).newline();

				// description
				UsagePrinter descriptionPrinter = optionPrinter.newIndentedPrinter(4);
				descriptionPrinter.append(option.getDescription()).newline();

				descriptionPrinter.newline();
			}

			if (arguments != null) {
				// "--" option
				UsagePrinter optionPrinter = out.newIndentedPrinter(8);
				optionPrinter.append("--").newline();

				// description
				UsagePrinter descriptionPrinter = optionPrinter.newIndentedPrinter(4);
				descriptionPrinter.append(
						"This option can be used to separate command-line options from the " +
								"list of argument, (useful when arguments might be mistaken for command-line options")
						.newline();
				descriptionPrinter.newline();

				// arguments name
				optionPrinter.append(UsageHelper.toDescription(arguments)).newline();

				// description
				descriptionPrinter.append(arguments.getDescription()).newline();
				descriptionPrinter.newline();
			}
		}

	}

	private static List<OptionMetadata> sortOptions(List<OptionMetadata> options) {
		options = new ArrayList<>(options);
		Collections.sort(options, UsageHelper.DEFAULT_OPTION_COMPARATOR);
		return options;
	}

	private static class SynopsisPrint {
		private String commandName;
		private CommandMetadata metadata;
		private UsagePrinter out;
		private List<OptionMetadata> options;
		private ArgumentsMetadata arguments;

		public SynopsisPrint(String commandName, CommandMetadata metadata, UsagePrinter out) {
			this.commandName = commandName;
			this.metadata = metadata;
			this.out = out;
		}

		public List<OptionMetadata> getOptions() {
			return options;
		}

		public ArgumentsMetadata getArguments() {
			return arguments;
		}

		public SynopsisPrint invoke(int firstLevelIndent, int secondLevelIndent) {
			UsagePrinter synopsis =
					out.newIndentedPrinter(firstLevelIndent).newPrinterWithHangingIndent(secondLevelIndent);
			options = newArrayList();

			synopsis.append(commandName)
					.appendWords(toSynopsisUsage(sortOptions(metadata.getCommandOptions())));
			options.addAll(metadata.getCommandOptions());

			// command arguments (optional)
			arguments = metadata.getArguments();
			if (arguments != null) {
				synopsis.append("[--]").append(UsageHelper.toUsage(arguments));
			}
			synopsis.newline();
			synopsis.newline();
			return this;
		}
	}
}
