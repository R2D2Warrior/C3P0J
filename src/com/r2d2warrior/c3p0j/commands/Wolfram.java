package com.r2d2warrior.c3p0j.commands;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.ParseException;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.utils.WebUtils;
import com.r2d2warrior.c3p0j.utils.WolframQuery;
import com.wolfram.alpha.WAException;

@Command(name="wolfram", alt="wa", requiresArgs=true)
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
			int messageLimit = 512; // :nick!user@host PRIVMSG #targetname :text here
			
			User userBot = bot.getUserBot();
			messageLimit -= userBot.getNick().length() + userBot.getLogin().length() + userBot.getHostmask().length() +  3; // 3 for the ":" "!" and "@"
			messageLimit -= event.getChannel().getName().length() + "PRIVMSG".length() + 4; // 4 accounts for the 3 spaces and last colon
			
			messageLimit *= .75;
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
			}
		}
		catch (WAException e)
		{
			event.respondToUser("An error occurred while getting results.");
			e.printStackTrace();
		}
	}
}
