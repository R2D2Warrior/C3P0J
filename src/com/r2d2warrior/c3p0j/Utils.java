package com.r2d2warrior.c3p0j;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericChannelEvent;
import org.pircbotx.hooks.types.GenericUserEvent;

import bsh.EvalError;
import bsh.Interpreter;

public class Utils
{
	@SuppressWarnings("unchecked")
	public static Interpreter createDefaultInterpreter(Event<PircBotX> event) throws EvalError
	{
		Interpreter i = new Interpreter();
		PircBotX bot = event.getBot();
		
		i.set("bot", bot);
		i.set("event", event);
		
		if (event instanceof GenericUserEvent)
			i.set("user", ((GenericUserEvent<PircBotX>)event).getUser());
		
		if (event instanceof GenericChannelEvent)
			i.set("channel", ((GenericChannelEvent<PircBotX>)event).getChannel());
		
		i.set("config", bot.getConfiguration());
		i.set("dao",  bot.getUserChannelDao());
		i.set("admins", bot.getConfiguration().getAdminAccounts().toString());
		i.set("cmdReg", bot.getCommandRegistry());
		
		return i;
	}
}
