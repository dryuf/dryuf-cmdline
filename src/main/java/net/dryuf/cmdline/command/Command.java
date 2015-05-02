package net.dryuf.cmdline.command;

import java.util.List;


/**
 * Command object interface.
 */
public interface Command
{
	/** Continue execution. */
	int EXIT_CONTINUE = -1;
	/** Exit with success. */
	int EXIT_SUCCESS = 0;
	/** Exit with fatal failure. */
	int EXIT_FAILURE = 121;
	/** Exit due to incorrect usage. */
	int EXIT_USAGE = 122;

	/**
	 * Sets up the Command object.
	 *
	 * @param context
	 * 	command context
	 * @param args
	 * 	command arguments
	 *
	 * @return
	 * 	-1 to continue, exit code otherwise.
	 *
	 * @throws Exception
	 * 	in case of error.
	 */
	int setup(CommandContext context, List<String> args) throws Exception;

	/**
	 * Prints help.
	 *
	 * @param context
	 * 	command context
	 * @param args
	 * 	command arguments
	 *
	 * @return
	 * 	exit code (must be non-negative).
	 *
	 * @throws Exception
	 * 	in case of error.
	 */
	int help(CommandContext context, List<String> args) throws Exception;

	/**
	 * Request help of subcommand.  This passes the setup() initialization and goes directly to help
	 *
	 * @param context
	 * 	command context
	 * @param args
	 * 	command arguments
	 *
	 * @return
	 * 	exit code (must be non-negative).
	 *
	 * @throws Exception
	 * 	in case of error.
	 */
	int subHelp(CommandContext context, List<String> args) throws Exception;

	/**
	 * Executes the command.
	 *
	 * @return
	 * 	exit code (must be non-negative).
	 *
	 * @throws Exception
	 * 	in case of error.
	 */
	int execute() throws Exception;
}
