package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import bsh.EvalError;
import bsh.Interpreter;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

@Command(name="exec", desc="Execute a method within PircBotX", adminOnly=true)
public class Exec extends GenericCommand
{
	private CommandEvent<PircBotX> event;
	
	public Exec(CommandEvent<PircBotX> event)
	{
		super(event);
		this.event = event;
	}
	
	public void execute()
	{
		String arg = event.getArgString();
		PircBotX bot = event.getBot();
		
		Interpreter i = new Interpreter();
		
		try
		{
			i.set("bot", bot);
			i.set("event", event);
			i.set("user", event.getUser());
			i.set("channel", event.getChannel());
			i.set("config", bot.getConfiguration());
			i.set("dao",  bot.getUserChannelDao());
			i.set("admins", bot.getConfiguration().getAdminAccounts().toString());
			i.set("cmdReg", bot.getCommandRegistry());
			
			event.respondToUser("Trying to run \"" + arg + "\"");
			i.eval(arg);
		}
		catch (EvalError e)
		{
			e.printStackTrace();
			event.respondToUser("Error while running \"" + arg + "\"");
		}
	}
}
