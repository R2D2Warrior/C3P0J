package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

@Command(name="cycle", desc="Parts and rejoins current or specified channel", syntax="cycle [#channel]", adminOnly=true)
public class Cycle extends GenericCommand
{
	private CommandEvent<PircBotX> event;
	
	public Cycle(CommandEvent<PircBotX> event)
	{
		super(event);
		this.event = event;
	}
	
	public void execute()
	{
		String chan = (event.hasChannelArg()) ? event.getArgumentsList().get(0) : event.getChannel().getName();
		
		if (event.getBot().getUserChannelDao().channelExists(chan))
		{			
			event.respondToUser("Trying to cycle channel: " + chan);
			event.getBot().getUserChannelDao().getChannel(chan).send().part("Rejoining...");
			event.getBot().sendIRC().joinChannel(chan);
		}
		else
			event.respondToUser("Not in channel: " + chan);
	}
}