package app.saikat.iiLang.interfaces;

import java.util.EnumSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public enum CmdlineOptions {

	FILE_NAME(Option.builder("i").desc("Input file location").longOpt("input").required().build()),
	DUMP_TOKENS(Option.builder("dumpTokens").desc("prints tokens after they are scanned").required(false).build()),
	DUMP_AST(Option.builder("dumpAst").desc("prints ast after parsing").required(false).build());

	private final Option option;

	private CmdlineOptions(Option option) {
		this.option = option;
	}

	public String getOptionValue() {
		return commandLine.getOptionValue(this.option.getOpt());
	}

	public Option getOption() {
		return option;
	}

	/** Options selected at commandline */
	public static EnumSet<CmdlineOptions> selectedOptions;

	private static CommandLine commandLine;
	public static void setCommandLine(CommandLine line) {
		commandLine = line;
	}
}
