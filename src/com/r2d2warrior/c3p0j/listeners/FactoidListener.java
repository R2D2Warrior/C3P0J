package com.r2d2warrior.c3p0j.listeners;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import com.r2d2warrior.c3p0j.handling.FactoidEvent;
import com.r2d2warrior.c3p0j.handling.FactoidManager;

@AddListener
public class FactoidListener extends ListenerAdapter<PircBotX>
{
	public void onMessage(MessageEvent<PircBotX> event)
	{
		PircBotX bot = event.getBot();
		String message = event.getMessage();
		Channel channel = event.getChannel();
		User user = event.getUser();
		
		String factoidPrefix = event.getBot().getConfiguration().getFactoidPrefix();
		if (event.getMessage().substring(0, 1).equals(factoidPrefix))
			event.getBot().getConfiguration().getListenerManager().dispatchEvent(new FactoidEvent<PircBotX>(bot, channel, user, message));
	}
	
	public void onFactoid(FactoidEvent<PircBotX> event)
	{
		String name = event.getFactoidName();
		if (FactoidManager.factoidExists(name))
			event.respond(FactoidManager.getFactoidData(name));
	}
}
