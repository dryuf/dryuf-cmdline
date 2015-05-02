package net.dryuf.cmdline.command;

import net.dryuf.cmdline.app.AppContext;
import net.dryuf.cmdline.app.CommonAppContext;

import java.util.Map;


/**
 * Basic command context.
 */
public interface CommandContext
{
	AppContext getAppContext();

	CommandContext getParentContext();

	String getCommandPath();

	CommandContext createChild(Command command, String commandName, Map<Class<?>, Object> beans);
}
