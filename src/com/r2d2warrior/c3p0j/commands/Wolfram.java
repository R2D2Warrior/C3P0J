package com.r2d2warrior.c3p0j.commands;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.ParseException;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.WaitForQueue;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.handling.CommandInfo;
import com.r2d2warrior.c3p0j.utils.Utils;
import com.r2d2warrior.c3p0j.utils.WebUtils;
import com.r2d2warrior.c3p0j.utils.WolframQuery;
import com.wolfram.alpha.WAException;

@Command(name="wolfram", alt="wa", desc="Queries Wolfram|Alpha and formats results.", syntax="wolfram <input>", requiresArgs=true)
public class Wolfram extends GenericCommand
{
	
	public Wolfram(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		event.respondToUser("Executing query for \"" + event.getArguments() + "\"...");
		try
		{
			int messageLimit = Utils.getMessageLimit(event);
			messageLimit *= .75; // Take 75% of the message limit
			StringBuilder sb = new StringBuilder(messageLimit);
			
			WolframQuery wa = new WolframQuery(event.getArguments());
			sb.append(Colors.setBold("[Input: ") + wa.getInputInterpretation() + Colors.setBold("]") + " ");
			sb.append(Colors.setBold("Result: ") + wa.getBestResult() + " ");
			
			String url = wa.getBasicURL();
			try
			{
				url = WebUtils.shortenURL(url);
			}
			catch (IOException | ParseException e)
			{
				e.printStackTrace();
			}
			
			sb.append("(" + Colors.setColor(url, Colors.BLUE) + ")");
			
			sb.trimToSize();
			event.respond(sb.toString());
			
			if (!wa.getOtherIDs().isEmpty())
			{
				event.respondToUser(Colors.setBold("Other Results: ") + StringUtils.join(wa.getOtherIDs(), ", "));
				event.respondToUser("Use " + Colors.setBold(event.getPrefix() + event.getCommandName() + " [other result type]") +
						" to see that result.");
				
				CommandEvent<PircBotX> cmd = waitForCommand();
				String args = cmd.getArguments();
				if (StringUtils.isNumeric(args) && wa.getDefinitions() != null)
					cmd.respond(wa.getOtherDefinition(Integer.parseInt(args)));
				else if (args.length() > 1)
					cmd.respond(wa.getOtherResult(args));
			}
		}
		catch (WAException e)
		{
			event.respondToUser("An error occurred while getting results.");
			e.printStackTrace();
		}
	}
	
	private CommandEvent<PircBotX> waitForCommand()
	{
		
		for (CommandInfo<GenericCommand> info : bot.getCommandRegistry().getCommands())
		{
			if (info.equals(event.getCommandInfo()))
			{
				bot.getCommandRegistry().getCommands().remove(info);
				break;
			}
		}
		
		try
		{
			@SuppressWarnings({ "unchecked", "resource" })
			CommandEvent<PircBotX> cmd = new WaitForQueue(bot).waitFor(CommandEvent.class);
			bot.getCommandRegistry().getCommands().add(event.getCommandInfo());
			return cmd;

		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
