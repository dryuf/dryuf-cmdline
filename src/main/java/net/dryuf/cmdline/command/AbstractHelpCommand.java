package net.dryuf.cmdline.command;


import java.util.List;


/**
 * Core command implementation.
 */
public abstract class AbstractHelpCommand extends AbstractCommand
{
	@Override
	public int setup(CommandContext context, List<String> args) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected int usage(CommandContext context, String errorMessage) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int execute() throws Exception
	{
		throw new UnsupportedOperationException();
	}
}
