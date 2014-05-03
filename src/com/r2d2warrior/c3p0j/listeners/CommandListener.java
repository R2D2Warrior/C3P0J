package com.r2d2warrior.c3p0j.listeners;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

@AddListener
public class CommandListener extends ListenerAdapter<PircBotX>
{
	
	public void onCommand(CommandEvent<PircBotX> event)
	{
		String result = event.getBot().getCommandRegistry().executeCommand(event);
		
		if (!StringUtils.isBlank(result))
			event.respondToUser(result);
	}
	
	public void onMessage(MessageEvent<PircBotX> event)
	{
		PircBotX bot = event.getBot();
		String message = event.getMessage();
		Channel channel = event.getChannel();
		User user = event.getUser();
		
		if (bot.getConfiguration().getPrefixes().containsKey(message.substring(0, 1)))
			// Messages starts with a valid prefix -- call command event
			bot.getConfiguration().getListenerManager().dispatchEvent(new CommandEvent<PircBotX>(bot, channel, user, message));
		else
		{
			String[] args = message.split(" ");
			if (args[0].equalsIgnoreCase(bot.getNick() + ","))
				bot.getConfiguration().getListenerManager().dispatchEvent(new CommandEvent<PircBotX>(bot, channel, user, message));
		}
		
	}
	
	public void onPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
		PircBotX bot = event.getBot();
		String message = event.getMessage();
		User user = event.getUser();
		
		if (bot.getCommandRegistry().isCommand(message.split(" ")[0]))
			// First word of the private message is a command -- call command event with null channel
			bot.getConfiguration().getListenerManager().dispatchEvent(new CommandEvent<PircBotX>(bot, null, user, message));
		
	}

}