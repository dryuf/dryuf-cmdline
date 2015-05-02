package net.dryuf.cmdline.command;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Core command implementation.
 */
public abstract class AbstractCommand implements Command
{
	@Getter
	@Setter
	private boolean isHelp;

	public static void runMain(String[] args, MainFunction runner)
	{
		try {
			System.exit(runner.apply(args));
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			System.exit(EXIT_FAILURE);
		}
	}

	/**
	 * Helper to run full command lifecycle.  This should be executed only by main entrypoint.
	 *
	 * @param commandContext
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
	protected int run(CommandContext commandContext, List<String> args) throws Exception
	{
		int ret = setup(commandContext, args);
		if (ret != EXIT_CONTINUE)
			return ret;
		return execute();
	}

	/**
	 * Initializes the instance.
	 *
	 * @param context
	 * 	command context
	 */
	protected void initialize(CommandContext context)
	{
		createOptions(context);
	}

	@Override
	public int setup(CommandContext commandContext, List<String> args) throws Exception
	{
		ListIterator<String> argsIterator = args.listIterator();
		initialize(commandContext);
		int ret = processArguments(commandContext, argsIterator);
		if (ret != EXIT_CONTINUE) {
			return ret;
		}
		if (argsIterator.hasNext()) {
			return usage(commandContext, "Unexpected argument: "+argsIterator.next());
		}
		return EXIT_CONTINUE;
	}

	/**
	 * Processes arguments.  This is a wrapper which calls the following:
	 * - createOptions
	 * - parseOptions
	 * - validateOptions
	 *
	 * @param context
	 * 	command context
	 * @param args
	 * 	remaining arguments
	 *
	 * @return
	 * 	EXIT_CONTINUE or error code
	 *
	 * @throws Exception
	 * 	in case of error
	 */
	protected int processArguments(CommandContext context, ListIterator<String> args) throws Exception
	{
		int ret = parseOptions(context, args);
		if (ret != EXIT_CONTINUE)
			return ret;
		return validateOptions(context, args);
	}

	/**
	 * Parses options.
	 *
	 * Default implementation which can be replaced by some options parsing framework.
	 *
	 * @param context
	 * 	command context
	 * @param args
	 * 	remaining arguments
	 *
	 * @return
	 * 	EXIT_CONTINUE or error code
	 *
	 * @throws Exception
	 * 	in case of error
	 */
	protected int parseOptions(CommandContext context, ListIterator<String> args) throws Exception
	{
		String enforceOptionPrefix = configEnforceOptionPrefix(context);
		String optionsEnd = configOptionsEnd(context);
		while (args.hasNext()) {
			String arg = args.next();
			boolean consumed;
			try {
				consumed = parseOption(context, arg, args);
			}
			catch (IllegalArgumentException ex) {
				return usage(context, "Option " + arg + ": " + ex.getMessage());
			}
			if (!consumed) {
				if (arg.equals(optionsEnd))
					break;
				if (enforceOptionPrefix != null && arg.startsWith(enforceOptionPrefix)) {
					return usage(context, "Unknown option: "+arg);
				}
				args.previous();
				break;
			}
		}
		if (isHelp()) {
			return parseHelp(context, args);
		}
		return parseNonOptions(context, args);
	}

	/**
	 * Called by parseOptions when parsing starts.  Useful when inheriting options, so it is created only once.
	 *
	 * @param context
	 * 	command context
	 */
	protected void createOptions(CommandContext context)
	{
	}

	/**
	 * Parses next option.
	 *
	 * @param context
	 * 	command context
	 * @param arg
	 * 	argument to process
	 * @param args
	 * 	pending arguments
	 *
	 * @return
	 * 	true if argument was processed, false otherwise
	 *
	 * @throws IllegalArgumentException
	 * 	if incorrect value or generally usage was encountered.
	 * @throws Exception
	 * 	in case of error.
	 */
	protected boolean parseOption(CommandContext context, String arg, ListIterator<String> args) throws Exception
	{
		if (configHelpArgument(context).contains(arg)) {
			isHelp = true;
			return true;
		}
		return false;
	}

	/**
	 * Processes non-options parts (program arguments, subcommand, non-understood options etc).
	 *
	 * @param context
	 * 	command context
	 * @param args
	 * 	remaining arguments
	 *
	 * @return
	 * 	EXIT_CONTINUE to continue, error code otherwise.
	 *
	 * @throws Exception
	 * 	in case of error.
	 */
	protected int parseNonOptions(CommandContext context, ListIterator<String> args) throws Exception
	{
		if (args.hasNext())
			return usage(context, "Unexpected argument: " + args.next());
		return EXIT_CONTINUE;
	}

	protected int parseHelp(CommandContext context, ListIterator<String> args) throws Exception
	{
		return helpThis(context);
	}

	/**
	 * Validates options for regular call (non-help subcommand).
	 *
	 * @param context
	 * 	command context
	 * @param args
	 * 	remaining arguments
	 *
	 * @return
	 * 	EXIT_CONTINUE or exit code
	 *
	 * @throws Exception
	 * 	in case of error
	 */
	protected int validateOptions(CommandContext context, ListIterator<String> args) throws Exception
	{
		return EXIT_CONTINUE;
	}

	@Override
	public int help(CommandContext context, List<String> args) throws Exception
	{
		if (!args.isEmpty()) {
			return usage(context, "No subcommand supported: " + args.get(0));
		}
		return helpThis(context);
	}

	@Override
	public int subHelp(CommandContext context, List<String> args) throws Exception
	{
		initialize(context);
		return help(context, args);
	}

	/**
	 * Prints help for this parent command.
	 *
	 * @param context
	 * 	command context
	 *
	 * @return
	 * 	EXIT_SUCCESS
	 *
	 * @throws Exception
	 * 	in case of error.
	 */
	protected int helpThis(CommandContext context) throws Exception
	{
		Map<String, String> options = configOptionsDescription(context);
		Map<String, String> parameters = configParametersDescription(context);
		System.out.print("Usage: "+context.getCommandPath()+( options.isEmpty() ? "" : "options... ")+
				String.join(" ", parameters.keySet())+"\n"+
				wrapWithNewLine(configHelpTitle(context))+
				(options.isEmpty() ? "" : ("\nOptions:\n"+formatOptions(context, options)))+
				(parameters.isEmpty() ? "" : ("\nParameters:\n"+formatOptions(context, parameters)))
		);
		return EXIT_SUCCESS;
	}

	/**
	 * Prints usage, due to error.
	 *
	 * @param context
	 * 	command context
	 * @param errorMessage
	 * 	error message due to incorrect usage
	 *
	 * @return
	 * 	EXIT_USAGE
	 *
	 * @throws Exception
	 * 	in case of error
	 */
	protected int usage(CommandContext context, String errorMessage) throws Exception
	{
		if (errorMessage != null)
			System.err.println(errorMessage);
		System.err.println(
				"Usage: " + context.getCommandPath() + "options "+
						String.join(" ", configParametersDescription(context).keySet())+"\n" +
				"Type " + Iterables.getFirst(configHelpArgument(context), null) + " for help"
		);
		return EXIT_USAGE;
	}

	/**
	 * Provides brief explanation of command.
	 *
	 * @param context
	 * 	command context
	 *
	 * @return
	 * 	brief explanation of command.
	 */
	protected String configHelpTitle(CommandContext context)
	{
		return "";
	}

	/**
	 * Provides list of supported options.
	 *
	 * @param context
	 * 	command context
	 *
	 * @return
	 * 	map of options, name to description.
	 */
	protected Map<String, String> configOptionsDescription(CommandContext context)
	{
		return Collections.emptyMap();
	}

	/**
	 * Provides description of parameters.
	 *
	 * @param context
	 * 	command context
	 *
	 * @return
	 * 	map of parameters, name to description.
	 */
	protected Map<String, String> configParametersDescription(CommandContext context)
	{
		return Collections.emptyMap();
	}

	/**
	 * Provides help argument.
	 *
	 * @param context
	 * 	command context
	 *
	 * @return
	 * 	help argument command name.
	 */
	protected Collection<String> configHelpArgument(CommandContext context)
	{
		return ImmutableSet.of("-h", "--help");
	}

	/**
	 * Provides options end argument.
	 *
	 * @param context
	 * 	command context
	 *
	 * @return
	 * 	argument marking end of options.
	 */
	protected String configOptionsEnd(CommandContext context)
	{
		return "--";
	}

	/**
	 * Provides whether to avoid processing arguments starting with prefix as non-options arguments.
	 *
	 * @param context
	 * 	command context
	 *
	 * @return
	 * 	indicator whether to enforce prefix as an option.
	 */
	protected String configEnforceOptionPrefix(CommandContext context)
	{
		return "-";
	}

	/**
	 * Utility method to read option value.
	 *
	 * @param existing
	 * 	existing object of option value
	 * @param args
	 * 	remaining arguments
	 *
	 * @return
	 * 	the value of option
	 *
	 * @throws IllegalArgumentException
	 * 	in case of no value provided, value specified twice
	 */
	protected String needArgsParam(Object existing, ListIterator<String> args)
	{
		if (existing != null)
			throw new IllegalArgumentException("option specified twice");
		if (!args.hasNext())
			throw new IllegalArgumentException("option need a value");
		return args.next();
	}

	/**
	 * Utility method to format commands or options output.
	 *
	 * @param options
	 * 	map of option name and its explanation.
	 *
	 * @return
	 * 	formatted output.
	 */
	protected String formatOptions(CommandContext context, Map<String, String> options)
	{
		int screenSize = 80;
		int leftLength =
				options.keySet().stream().mapToInt(String::length).reduce(Math::max).orElse(0)+4;
		int rightLength = screenSize-leftLength;
		return options.entrySet().stream()
				.map(e -> String.format("%-"+leftLength+"s%s", e.getKey(), indentNextLines(wrapWithNewLine(WordUtils.wrap(e.getValue(), rightLength)), leftLength)))
				.collect(Collectors.joining());
	}

	/**
	 * Wraps string new line unless already done.
	 *
	 * @param content
	 * 	input string
	 *
	 * @return
	 * 	input string wrapped with new line unless already done.
	 */
	protected String wrapWithNewLine(String content)
	{
		return content.isEmpty() || content.endsWith("\n") ? content : (content+"\n");
	}

	/**
	 * Indents input string additional lines (except first one).
	 *
	 * @param content
	 * 	input string
	 *
	 * @return
	 * 	indented string.
	 */
	protected String indentNextLines(String content, int indentation)
	{
		return content.replaceAll("\n(.)", String.format("\n%"+indentation+"s$1", ""));
	}

	@FunctionalInterface
	public interface MainFunction
	{
		int apply(String[] args) throws Exception;
	}
}
