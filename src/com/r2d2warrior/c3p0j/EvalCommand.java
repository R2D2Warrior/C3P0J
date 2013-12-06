package com.r2d2warrior.c3p0j;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserChannelDao;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.output.OutputChannel;
import org.pircbotx.output.OutputUser;

import bsh.EvalError;
import bsh.Interpreter;

public class EvalCommand extends ListenerAdapter<PircBotX>
{
	
	public void onMessage(MessageEvent<PircBotX> event)
	{
    	PircBotX bot = event.getBot();
    	Channel channel = event.getChannel();
    	User user = event.getUser();
    	OutputChannel c = channel.send();
		OutputUser u = user.send();
    	String fullMessage = event.getMessage();
    	UserChannelDao<User, Channel> dao = bot.getUserChannelDao();
    	
    	List<String> admins = bot.getConfiguration().getAdminAccounts();
    	if (admins.contains(user.getAccount()))
    	{
    	
	    	if (fullMessage.startsWith(".eval "))
	    	{
	    		String eval = "";
	    		if (fullMessage.length() > 6)
	    			eval = fullMessage.substring(6);
	    		if (eval.toLowerCase().contains("pass") || eval.toLowerCase().contains("data.data"))
	    			eval = "";
	    			
	    		Interpreter i = new Interpreter();
	    		String result = "";
	    		try
	    		{
	    			i.set("bot", bot);
	    			i.set("event",  event);
	    			i.set("user", user);
	    			i.set("channel", channel);
	    			i.set("chan", channel);
	    			i.set("dao",  dao);
	    			i.set("admins", admins.toString());
	    			
	    			i.eval("thing = " + eval);
	    			result = i.get("thing").toString();
	    		}
	    		catch (EvalError e)
	    		{
	    			u.notice("Error occurred while evaluating: \"" + eval + "\"");
	    			e.printStackTrace();
	    		}
	    		
	    		c.message(result);
	    	}
    	}
	}
}
