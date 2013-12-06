package com.r2d2warrior.c3p0j.old;

import java.util.Arrays;

import lombok.Getter;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@SuppressWarnings("rawtypes")
public class CommandHandler
{
	protected final PircBotX bot;
	protected final Event event;
	protected final ImmutableMap<String, String> prefixes;
	
	@Getter
	protected String respondCmd;
	
	public CommandHandler(Event event)
	{
		this.bot = event.getBot();
		this.event = event;
		this.prefixes = bot.getConfiguration().getPrefixes();
		this.respondCmd = "";
	}
	
	public void handleLine(String line)
	{
		ImmutableList<String> message = ImmutableList.copyOf(Arrays.asList(line.split(" ", 2)));
		String command = message.get(0).substring(1).toLowerCase();
		String prefix = message.get(0).substring(0, 1);
		
		/*
		 * Determine which type of message we are handling (private or in channel) and find the proper response type
		 * depending on the configuration
		 */
		if (event instanceof PrivateMessageEvent)
		{
			//Private messages always respond with NOTICE
			respondCmd = "NOTICE " + ((PrivateMessageEvent)event).getUser().getNick() + " :";
		}
		else if (event instanceof MessageEvent)
		{
			if (prefixes.containsKey(prefix))
			{
				//Either of these could mean they want a channel message response
				if (prefixes.get(prefix).equalsIgnoreCase("PRIVMSG") || prefixes.get(prefix).equalsIgnoreCase("MESSAGE")
						|| prefixes.get(prefix).equalsIgnoreCase("CHANNEL"))
				{
					respondCmd = "PRIVMSG " + ((MessageEvent)event).getChannel().getName() + " :";
				}
				
				//Any of these could mean they want a notice to a user
				else if (prefixes.get(prefix).equalsIgnoreCase("NOTICE") || prefixes.get(prefix).equalsIgnoreCase("USER")
						|| prefixes.get(prefix).equalsIgnoreCase("NICK"))
				{
					respondCmd = "NOTICE " + ((MessageEvent)event).getUser().getNick() + " :";
				}
				
				//Default to NOTICE for now
				// TODO this
				else
				{
					respondCmd = "NOTICE " + ((MessageEvent)event).getUser().getNick() + " :";
				}
			}
		}
		
		/*
		 * Main command handling part
		 */
		if (command.equals("say"))
			SayCommand.call(event, respondCmd, message.get(1));
	}
}
