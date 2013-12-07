package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

@Command(name="part", desc="Parts current or specified channel", adminOnly=true)
public class Part extends GenericCommand
{
	private CommandEvent<PircBotX> event;
	
	public Part(CommandEvent<PircBotX> event)
	{
		super(event);
		this.event = event;
	}
	
	// TODO accept part message
	public void execute()
	{
		String chan = (event.hasNoArgs()) ? event.getChannel().getName() : event.getCommandArgs().get(0);
		
		if (event.getBot().getUserChannelDao().channelExists(chan))
		{
			event.getBot().getUserChannelDao().getChannel(chan).send().part();
			event.respondToUser("Trying to part channel: " + chan);
		}
		else
			event.respondToUser("Not in channel: " + chan);
	}
}
