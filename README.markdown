# Dryuf CmdLine

## Command line structure

Support for building command line applications, including hierarchical with subcommands.

```
$ ./target/homecontrol --help
Usage: homecontrol options... command...

Options:
--id homeId    id of home, according to your list of homes you own or control

Commands:
help [command]       Prints help or help of subcommand
door arguments...    Controls door
sell                 Sells the home

$ ./target/homecontrol --id my door --door front open
Door open: front
```

The source code:

```
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
```

The classes are created automatically by bean container and are being injected their properties as declared in their relevant modules.


## License

The code is released under version 2.0 of the [Apache License][].


## Stay in Touch

Zbynek Vyskovsky

Feel free to contact me at kvr000@gmail.com and http://github.com/kvr000

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
