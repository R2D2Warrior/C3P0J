package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

@Command(name="part", desc="Parts current or specified channel", syntax="part [#channel]", adminOnly=true)
public class Part extends GenericCommand
{
	private CommandEvent<PircBotX> event;
	
	public Part(CommandEvent<PircBotX> event)
	{
		super(event);
		this.event = event;
	}
	
	public void execute()
	{
		String chan = (event.hasChannelArg()) ? event.getArgumentsList().get(0) : event.getChannel().getName();
		String defaultMsg = "Parted by " + event.getUser().getNick();
		String msg = "";
		
		if (event.hasNoArgs())
		{
			msg = defaultMsg;
		}
		else
		{
			if (event.hasChannelArg())
			{
				msg = (event.getArgumentsList().size() > 1) ? event.getArgRange(1) : defaultMsg;
			}
			else
				msg = event.getArguments();
		}
		
		if (event.getBot().getUserChannelDao().channelExists(chan))
		{
			event.respondToUser("Trying to part channel: " + chan);
			event.getBot().getUserChannelDao().getChannel(chan).send().part(msg);
		}
		else
			event.respondToUser("Not in channel: " + chan);
	}
}
