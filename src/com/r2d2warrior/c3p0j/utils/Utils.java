package com.r2d2warrior.c3p0j.utils;

import java.util.List;
import org.apache.commons.lang3.text.WordUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericChannelEvent;
import org.pircbotx.hooks.types.GenericUserEvent;

import bsh.EvalError;
import bsh.Interpreter;

// TODO Nick compeletion?
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
			
			i.eval("import com.r2d2warrior.c3p0j.utils.WebUtils");
			i.eval("import org.pircbotx.*");
		}
		catch (EvalError e)
		{
			e.printStackTrace();
			return null;
		}
		return i;
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
}
