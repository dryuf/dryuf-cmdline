package net.dryuf.cmdline.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


/**
 * Partial implementation for parent command distributing calls to subcommands.
 */
public abstract class AbstractParentCommand extends AbstractCommand
{
	private Command childCommand;
	private CommandContext childContext;

	@Override
	public int setup(CommandContext context, List<String> args) throws Exception
	{
		ListIterator<String> argsIterator = args.listIterator();
		initialize(context);
		int ret = processArguments(context, argsIterator);
		if (ret != EXIT_CONTINUE)
			return ret;
		return childCommand.setup(childContext, args.subList(argsIterator.nextIndex(), args.size()));
	}

	@Override
	protected int parseNonOptions(CommandContext context, ListIterator<String> args) throws Exception
	{
		if (!args.hasNext()) {
			return usage(context, "Missing subcommand.  Type "+configHelpArgument(context)+" for help");
		}
		String name = args.next();
		Class<? extends Command> subCommandClass;
		Map<String, Class<? extends Command>> subCommands = configSubCommands(context);
		if ((subCommandClass = subCommands.get(name)) == null) {
			return usage(
					context,
					"" +
							"Unknown option or command: "+name+"\n" +
							"Type "+configHelpArgument(context)+" for supported options and commands"
			);
		}
		if (subCommandClass == subCommands.get(Iterables.getFirst(configHelpArgument(context), null))) {
			return this.help(context, ImmutableList.copyOf(args));
		}
		childContext = createChildContext(context, name, false);
		childCommand = childContext.getAppContext().getBeanFactory().getBean(subCommandClass);
		return EXIT_CONTINUE;
	}

	@Override
	protected boolean parseOption(CommandContext context, String arg, ListIterator<String> args) throws Exception
	{
		if (arg.equals(Iterables.getFirst(configHelpArgument(context), null))) {
			return false;
		}
		return super.parseOption(context, arg, args);
	}

	@Override
	protected int parseHelp(CommandContext context, ListIterator<String> args) throws Exception
	{
		return this.help(context, ImmutableList.copyOf(args));
	}

	@Override
	public int help(CommandContext context, List<String> args) throws Exception
	{
		if (args.isEmpty()) {
			return helpThis(context);
		}
		else {
			String name = args.get(0);
			Class<? extends Command> childCommandClass = configSubCommands(context).get(name);
			if (childCommandClass == null) {
				return usage(context, "" +
						"Unsupported command " + name + "\n" +
						"Type "+configHelpArgument(context)+" for list of supported commands.\n"
				);
			}
			CommandContext childContext = createChildContext(context, name, true);
			this.childCommand = childContext.getAppContext().getBeanFactory().getBean(childCommandClass);
			return this.childCommand.subHelp(
					childContext,
					args.subList(1, args.size())
			);
		}

	}

	@Override
	public int helpThis(CommandContext context) throws Exception
	{
		System.out.print("" +
				"Usage: " + context.getCommandPath() + "options... command...\n" +
				wrapWithNewLine(configHelpTitle(context)) +
				"\nOptions:\n" +
				formatOptions(context, configOptionsDescription(context)) +
				"\nCommands:\n" +
				formatOptions(context, configCommandsDescription(context))
		);
		return EXIT_SUCCESS;

	}

	@Override
	public int execute() throws Exception
	{
		return childCommand.execute();
	}

	/**
	 * Gets supported commands.
	 *
	 * @param context
	 * 	command context
	 *
	 * @return
	 * 	supported commands, name to implementing class.
	 */
	protected abstract Map<String, Class<? extends Command>> configSubCommands(CommandContext context);

	/**
	 * {@inheritDoc}
	 *
	 * ParentCommand should provide the subcommand first ("help"), possibly listing other options.
	 */
	@Override
	protected Collection<String> configHelpArgument(CommandContext context)
	{
		return ImmutableSet.of("help", "--help", "-h");
	}

	/**
	 * Provides commands description.
	 *
	 * @param context
	 * 	command context
	 *
	 * @return
	 * 	command descriptions.
	 */
	protected Map<String, String> configCommandsDescription(CommandContext context)
	{
		return Collections.emptyMap();
	}

	/**
	 * Prints error about incorrect usage.
	 *
	 * @param context
	 * 	command context
	 * @param errorMessage
	 * 	error message
	 *
	 * @return
	 * 	error exit code (should be positive).
	 */
	@Override
	protected int usage(CommandContext context, String errorMessage)
	{
		System.err.println(errorMessage);
		System.err.print("" +
				"Usage: " + context.getCommandPath() + "options... command...\n" +
				"Type " + configHelpArgument(context) + " for help\n"
		);
		return EXIT_USAGE;
	}

	/**
	 * Creates child context for subcommand.
	 *
	 * @param commandContext
	 * 	command context
	 * @param name
	 * 	name of subcommand.
	 * @param isHelp
	 * 	indicator whether it is help command
	 *
	 * @return
	 * 	child command context.
	 */
	protected CommandContext createChildContext(CommandContext commandContext, String name, boolean isHelp)
	{
		return commandContext.createChild(this, name, null);
	}
}
