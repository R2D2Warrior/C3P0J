package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

@Command(name="join", desc="Joins specified channel", syntax="join <#channel>", requiresArgs=true, minGroup="mod")
public class Join extends GenericCommand
{
	
	public Join(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		String chan = event.getArgumentList().get(0);
		
		if (!userChannelDao.channelExists(chan) && event.hasChannelArg())
		{
			event.respondToUser("Trying to join channel: " + chan + " ...");
			bot.sendIRC().joinChannel(chan);
		}
		else if (userChannelDao.channelExists(chan))
			event.respondToUser("Already in channel: " + chan);
		else
			event.respondToUser("Invalid channel name: " + chan);
	}
}
