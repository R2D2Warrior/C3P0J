package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.command.CommandEvent;

@Command(name="say", info="Sends message to channel", adminOnly=true)
public class Say extends GenericCommand
{
	private CommandEvent<PircBotX> event;
	
	public Say(CommandEvent<PircBotX> event)
	{
		super(event);
		this.event = event;
	}
	
	@Override
	public void execute()
	{
		event.respond(event.getArgString());
	}
}
