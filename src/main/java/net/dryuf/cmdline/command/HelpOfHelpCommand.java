package net.dryuf.cmdline.command;

/**
 * Prints help of help command.
 */
public class HelpOfHelpCommand extends AbstractHelpCommand
{
	@Override
	public int helpThis(CommandContext context) throws Exception
	{
		System.out.print(""+
				"Usage: "+context.getCommandPath()+"command...\n"+
				"Prints help for specific command\n"
		);
		return 0;
	}
}
