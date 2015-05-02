package net.dryuf.cmdline.test.homecontrol;

import net.dryuf.cmdline.command.AbstractCommand;
import net.dryuf.cmdline.command.CommandContext;


/**
 * Sell command, example of no-arguments command.
 */
public class SellCommand extends AbstractCommand
{
	@Override
	public String configHelpTitle(CommandContext context)
	{
		return "Sells the home\n";
	}

	@Override
	public int execute() throws Exception
	{
		System.out.println("Home sold");
		return 0;
	}
}
