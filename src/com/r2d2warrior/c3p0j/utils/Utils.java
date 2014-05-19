package com.r2d2warrior.c3p0j.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.ReplyConstants;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.pircbotx.hooks.events.WhoisEvent;
import org.pircbotx.hooks.types.GenericChannelEvent;
import org.pircbotx.hooks.types.GenericUserEvent;

import bsh.EvalError;
import bsh.Interpreter;

public class Utils
{
	
	@SuppressWarnings("unchecked")
	public static Interpreter createDefaultInterpreter(Event<PircBotX> event)
	{
		Interpreter i = new Interpreter();
		PircBotX bot = event.getBot();
		
		try
		{
			i.set("bot", bot);
			i.set("event", event);
			
			if (event instanceof GenericUserEvent)
				i.set("user", ((GenericUserEvent<PircBotX>)event).getUser());
			
			if (event instanceof GenericChannelEvent)
				i.set("channel", ((GenericChannelEvent<PircBotX>)event).getChannel());
			
			i.set("config", bot.getConfiguration());
			i.set("dao",  bot.getUserChannelDao());
			i.set("userChannelDao", bot.getUserChannelDao());
			i.set("admins", bot.getConfiguration().getAdminAccounts());
			i.set("cmdReg", bot.getCommandRegistry());
			
			i.eval("import com.r2d2warrior.c3p0j.utils.Utils");
			i.eval("import com.r2d2warrior.c3p0j.utils.WebUtils");
			i.eval("import com.r2d2warrior.c3p0j.utils.Permissions");
			i.eval("import org.pircbotx.*");
			i.eval("import java.lang.reflect.*");
			i.eval("import java.util.*");
			i.eval("import org.apache.commons.lang3.StringUtils");
		}
		catch (EvalError e)
		{
			e.printStackTrace();
			return null;
		}
		return i;
	}
	
	public static WhoisEvent<PircBotX> getWhoisInfo(PircBotX bot, String nick)
	{
		
		if (!isOnline(bot, nick))
			// This returns a WhoisEvent with all variables set to null
			return new WhoisEvent.Builder<PircBotX>().generateEvent(bot);
		
		/*  Sending the nick twice to WHOIS ensures an idle time response.
		 *  Only sending the nick once while not on the same server as them will not return an idle time. */
		bot.sendRaw().rawLineNow("WHOIS " + nick + " " + nick);
		WaitForQueue queue = new WaitForQueue(bot);

		try
		{
			@SuppressWarnings("unchecked")
			WhoisEvent<PircBotX> whois = queue.waitFor(WhoisEvent.class); // This will get a WhoisEvent when a WHOIS reply ends
			queue.close();
			return whois;
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			queue.close();
			return null;
		}
	}
	
	public static boolean isOnline(PircBotX bot, String nick)
	{
		bot.sendRaw().rawLineNow("ISON " + nick);
		WaitForQueue queue = new WaitForQueue(bot);
		
		try
		{
			@SuppressWarnings("unchecked")
			ServerResponseEvent<PircBotX> serv = queue.waitFor(ServerResponseEvent.class);
			queue.close();
			if (serv.getCode() == ReplyConstants.RPL_ISON)
			{
				// .getRawLine() = ":chaos.esper.net 303 Botnick :Nick"
				String[] line = serv.getRawLine().split(":"); // = ["", "chaos.esper.net 303 Botnick ", " Nick "]
				
				//line.length == 2 when the nick isn't online
				return line.length > 2 && StringUtils.strip(line[2]).equalsIgnoreCase(nick);
			}
			return false;
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			queue.close();
			return false;
		}
	}
	
	public static int getMessageLimit(GenericChannelEvent<PircBotX> event)
	{
		
		
		User userBot = event.getBot().getUserBot();
		String messageFormat = String.format( // :nick!user@host PRIVMSG #targetname :text here
					":%s!%s@%s PRIVMSG %s :",
					userBot.getNick(), userBot.getLogin(),
					userBot.getHostmask(), event.getChannel().getName()
				);
		
		return 512 - messageFormat.length();
	}
	
	public static String getPackageName(Class<?> c)
	{
		String name = c.getName();
		int lastDot = name.lastIndexOf('.');
		if (lastDot == -1)
			return "";
		return name.substring(0, lastDot);
	}
	
	public static String getRange(List<String> list, int start)
	{
		return getRange(list, start, list.size()-1);
	}
	
	public static String getRange(List<String> list, int start, int end)
	{
		if (end >= list.size())
			end = list.size()-1;
		if (start < 0)
			start = 0;
		
		String ret = "";
		for (int x = start; x <= end; x++)
		{
			ret += list.get(x);
			ret += (x == end) ? "" : " ";
		}
		return ret;
	}
	
	public static String toSentenceCase(String s)
	{
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}
	
	public static String toTitleCase(String s)
	{
		return WordUtils.capitalizeFully(s, ' ', '\t', '\n');
	}
	
	public static String commaWithOr(List<String> list)
	{
		if (list.size() == 1)
			return list.get(0);
		else if (list.size() == 2)
			return list.get(0) + " or " + list.get(1);
		else
		{
			return StringUtils.join(getRange(list, 0, list.size()-2), ", ") + " or " + list.get(list.size()-1);
		}
	}
	
	public static String commaWithOr(String... list)
	{
		return commaWithOr(Arrays.asList(list));
	}
}
