package net.dryuf.cmdline.command;

import lombok.Getter;
import net.dryuf.cmdline.app.AppContext;
import net.dryuf.cmdline.app.CommonAppContext;

import java.util.Map;
import java.util.Optional;


/**
 * CommandContext in the calling chain.
 */
@Getter
public class ChildCommandContext implements CommandContext
{
	private final AppContext appContext;
	private final CommandContext parentContext;
	private final Command parentCommand;
	private final String commandName;

	public ChildCommandContext(CommandContext parentContext, Command parentCommand, String commandName)
	{
		this(parentContext.getAppContext(), parentContext, parentCommand, commandName);
	}

	public ChildCommandContext(
			AppContext appContext,
			CommandContext parentContext,
			Command parentCommand,
			String commandName)
	{
		this.appContext = appContext;
		this.parentContext = parentContext;
		this.parentCommand = parentCommand;
		this.commandName = commandName;
	}

	@Override
	public AppContext getAppContext()
	{
		return appContext;
	}

	@Override
	public String getCommandPath()
	{
		return getParentContext().getCommandPath() +
				Optional.ofNullable(commandName).map(s -> s + " ").orElse("");
	}

	@Override
	public CommandContext createChild(Command command, String commandName, Map<Class<?>, Object> beans)
	{
		return new ChildCommandContext(appContext.createChild(beans), this, command, commandName);
	}
}
