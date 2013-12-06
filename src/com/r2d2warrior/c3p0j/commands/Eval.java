package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import bsh.EvalError;
import bsh.Interpreter;

import com.r2d2warrior.c3p0j.command.CommandEvent;

@Command(name="eval", info="Evaluate a method within PircBotX", adminOnly=true)
public class Eval extends GenericCommand
{
	private CommandEvent<PircBotX> event;
	
	public Eval(CommandEvent<PircBotX> event)
	{
		super(event);
		this.event = event;
	}
	
	@Override
	public void execute()
	{
    	PircBotX bot = event.getBot();
    	Channel channel = event.getChannel();
    	User user = event.getUser();
		
		String eval = event.getArgString();
			
		Interpreter i = new Interpreter();
		String result = "";
		try
		{
			i.set("bot", bot);
			i.set("event",  event);
			i.set("user", user);
			i.set("channel", channel);
			i.set("conf", bot.getConfiguration());
			i.set("dao",  bot.getUserChannelDao());
			i.set("admins", bot.getConfiguration().getAdminAccounts().toString());
			
			i.eval("thing = " + eval);
			result = i.get("thing").toString();
		}
		catch (EvalError e)
		{
			user.send().notice("Error occurred while evaluating: \"" + eval + "\"");
			e.printStackTrace();
		}
		
		event.respond(result);
	}
}
