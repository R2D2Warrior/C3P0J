package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

@Command(name="say", desc="Sends message to channel", syntax="say <stuff>", requiresArgs=true, minGroup="user")
public class Say extends GenericCommand
{
	
	public Say(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		event.respond(event.getArguments());
	}
}
