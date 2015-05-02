package net.dryuf.cmdline.test.homecontrol;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import net.dryuf.cmdline.app.AppContext;
import net.dryuf.cmdline.app.BeanFactory;
import net.dryuf.cmdline.app.CommonAppContext;
import net.dryuf.cmdline.app.guice.GuiceBeanFactory;
import net.dryuf.cmdline.app.guice.GuiceBeanFactoryModule;
import net.dryuf.cmdline.command.AbstractParentCommand;
import net.dryuf.cmdline.command.Command;
import net.dryuf.cmdline.command.CommandContext;
import net.dryuf.cmdline.command.HelpOfHelpCommand;
import net.dryuf.cmdline.command.RootCommandContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.ListIterator;
import java.util.Map;


/**
 * Example of command line utility with the framework.
 * Further use of command line options is recommended, here keeping it simple to avoid Maven dependencies.
 */
public class HomeControlCommand extends AbstractParentCommand
{
	private static final Map<String, Class<? extends Command>> COMMANDS = ImmutableMap.of(
			"help", HelpOfHelpCommand.class,
			"door", DoorCommand.class,
			"sell", SellCommand.class
	);

	private Options options;

	/**
	 * Entry point.
	 *
	 * @param args
	 * 	program arguments
	 *
	 * @throws Exception
	 * 	in case of error.
	 */
	public static void main(String[] args) throws Exception
	{
		AppContext appContext = new CommonAppContext(Guice.createInjector(new GuiceBeanFactoryModule()).getInstance(BeanFactory.class));
		System.exit(new HomeControlCommand().run(
				new RootCommandContext(appContext).createChild(null, "homecontrol", null),
				Arrays.asList(args)
		));
	}

	@Override
	protected void createOptions(CommandContext context)
	{
		this.options = new Options();
	}

	@Override
	protected Map<String, String> configOptionsDescription(CommandContext context)
	{
		return ImmutableMap.of(
				"--id homeId", "id of home, according to your list of homes you own or control"
		);
	}

	@Override
	protected Map<String, String> configCommandsDescription(CommandContext context)
	{
		return ImmutableMap.of(
				"help [command]", "Prints help or help of subcommand",
				"door arguments...", "Controls door",
				"sell", "Sells the home"
		);
	}

	@Override
	protected Map<String, Class<? extends Command>> configSubCommands(CommandContext context)
	{
		return COMMANDS;
	}

	@Override
	protected CommandContext createChildContext(CommandContext commandContext, String name, boolean isHelp)
	{
		return commandContext.createChild(this, name, Collections.singletonMap(Options.class, options));
	}

	@Override
	protected boolean parseOption(CommandContext context, String arg, ListIterator<String> args) throws Exception
	{
		switch (arg) {
		case "--id":
			options.id = needArgsParam(options.id, args);
			return true;

		default:
			return super.parseOption(context, arg, args);
		}
	}

	@Override
	protected int validateOptions(CommandContext context, ListIterator<String> args) throws Exception
	{
		if (options.id == null) {
			return usage(context, "Option --id not specified");
		}
		return EXIT_CONTINUE;
	}

	public static class Options
	{
		String id;
	}
}
