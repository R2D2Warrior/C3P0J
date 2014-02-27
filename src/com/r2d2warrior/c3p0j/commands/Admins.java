package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

@Command(name="admins", desc="Lists bot admin accounts")
public class Admins extends GenericCommand
{
	
	public Admins(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		event.respond(config.getAdminAccounts().toString());
	}
}
