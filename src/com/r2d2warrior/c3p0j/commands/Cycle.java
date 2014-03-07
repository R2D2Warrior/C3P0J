package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

@Command(name="cycle", desc="Parts and rejoins current or specified channel", syntax="cycle [#channel]", adminOnly=true)
public class Cycle extends GenericCommand
{
	
	public Cycle(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		String chan = (event.hasChannelArg()) ? event.getArgumentList().get(0) : channel.getName();
		
		if (event.getBot().getUserChannelDao().channelExists(chan))
		{
			event.respondToUser("Trying to cycle channel: " + chan);
			userChannelDao.getChannel(chan).send().cycle();
		}
		else
			event.respondToUser("Not in channel: " + chan);
	}
}
