package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

@Command(name="join", desc="Joins specified channel", syntax="join <#channel>", adminOnly=true)
public class Join extends GenericCommand
{
	private CommandEvent<PircBotX> event;
	
	public Join(CommandEvent<PircBotX> event)
	{
		super(event);
		this.event = event;
	}
	
	public void execute()
	{
		if (event.hasNoArgs())
			return;
		String chan = event.getArgumentsList().get(0);
		if (!event.getBot().getUserChannelDao().channelExists(chan) && event.hasChannelArg())
		{
			event.respondToUser("Trying to join channel: " + chan + " ...");
			event.getBot().sendIRC().joinChannel(chan);
		}
		else if (event.getBot().getUserChannelDao().channelExists(chan))
			event.respondToUser("Already in channel: " + chan);
		else
			event.respondToUser("Invalid channel name: " + chan);
	}
}
