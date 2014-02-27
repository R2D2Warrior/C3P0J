package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

@Command(name="say", desc="Sends message to channel", syntax="say <stuff>", adminOnly=true)
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
		String msg = (event.hasNoArgs()) ? "Enter a message." : event.getArguments();
		event.respond(msg);
	}
}
