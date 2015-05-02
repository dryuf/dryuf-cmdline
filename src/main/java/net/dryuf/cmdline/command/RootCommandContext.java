package net.dryuf.cmdline.command;

import lombok.Getter;
import net.dryuf.cmdline.app.AppContext;
import net.dryuf.cmdline.app.CommonAppContext;

import java.util.Map;


/**
 * CommandContext in the calling chain.
 */
@Getter
public class RootCommandContext implements CommandContext
{
	private final AppContext appContext;

	public RootCommandContext(AppContext appContext)
	{
		this.appContext = appContext;
	}

	@Override
	public CommandContext getParentContext()
	{
		return null;
	}

	@Override
	public String getCommandPath()
	{
		return "";
	}

	@Override
	public CommandContext createChild(Command command, String commandName, Map<Class<?>, Object> beans)
	{
		return new ChildCommandContext(appContext.createChild(beans), this, command, commandName);
	}
}
