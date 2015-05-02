package net.dryuf.cmdline.test.homecontrol;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import net.dryuf.cmdline.command.AbstractCommand;
import net.dryuf.cmdline.command.AbstractParentCommand;
import net.dryuf.cmdline.command.Command;
import net.dryuf.cmdline.command.CommandContext;

import javax.inject.Inject;
import java.util.Collections;
import java.util.ListIterator;
import java.util.Map;


/**
 * Sell command, example of no-arguments command.
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DoorCommand extends AbstractParentCommand
{
	private static final Map<String, Class<? extends Command>> COMMANDS = ImmutableMap.of(
			"open", OpenCommand.class,
			"close", CloseCommand.class
	);

	private final HomeControlCommand.Options homeOptions;

	private Options options;

	@Override
	protected void createOptions(CommandContext context)
	{
		this.options = new DoorCommand.Options();
	}

	@Override
	protected boolean parseOption(CommandContext context, String arg, ListIterator<String> args) throws Exception
	{
		switch (arg) {
		case "--door":
			options.doorId = needArgsParam(options.doorId, args);
			return true;

		default:
			return super.parseOption(context, arg, args);
		}
	}

	@Override
	protected int validateOptions(CommandContext context, ListIterator<String> args) throws Exception
	{
		if (options.doorId == null) {
			return usage(context, "Option --door not specified");
		}
		return EXIT_CONTINUE;
	}

	@Override
	protected String configHelpTitle(CommandContext context)
	{
		return "Controls door.";
	}

	@Override
	protected Map<String, String> configOptionsDescription(CommandContext context)
	{
		return ImmutableMap.of(
				"--door doorId", "id of door to control"
		);
	}

	@Override
	protected Map<String, String> configCommandsDescription(CommandContext context)
	{
		return ImmutableMap.of(
				"open", "Opens door, provide some really long description so we can see the words wrapping",
				"close", "Close door"
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
		return commandContext.createChild(this, name, Collections.singletonMap(DoorCommand.Options.class, options));
	}

	public static class Options
	{
		String doorId;
	}

	@RequiredArgsConstructor(onConstructor = @__(@Inject))
	public static class OpenCommand extends AbstractCommand
	{
		private final DoorCommand.Options doorOptions;

		@Override
		public String configHelpTitle(CommandContext context)
		{
			return "Opens door";
		}

		@Override
		public int execute() throws Exception
		{
			System.out.println("Door open: " + doorOptions.doorId);
			return EXIT_SUCCESS;
		}
	}

	@RequiredArgsConstructor(onConstructor = @__(@Inject))
	public static class CloseCommand extends AbstractCommand
	{
		private final DoorCommand.Options doorOptions;

		@Override
		public String configHelpTitle(CommandContext context)
		{
			return "Closes door";
		}

		@Override
		public int execute() throws Exception
		{
			System.out.println("Door closed: " + doorOptions.doorId);
			return EXIT_SUCCESS;
		}
	}
}
