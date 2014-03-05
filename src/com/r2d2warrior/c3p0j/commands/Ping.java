package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.Colors;
import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

@Command(name="ping", desc="Sends a PONG")
public class Ping extends GenericCommand
{
	public Ping(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		event.respond(user.getNick() + Colors.setColor(" PONG PONG", Colors.RED + Colors.BOLD));
	}
}
