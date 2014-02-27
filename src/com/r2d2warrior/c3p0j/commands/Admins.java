package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

@Command(name="admins", desc="Lists bot admin accounts")
public class Admins extends GenericCommand
{
	private CommandEvent<PircBotX> event;
	
	public Admins(CommandEvent<PircBotX> event)
	{
		super(event);
		this.event = event;
	}
	
	public void execute()
	{
		event.respond(event.getBot().getConfiguration().getAdminAccounts().toString());
	}
}
