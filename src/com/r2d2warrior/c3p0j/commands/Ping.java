package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

//Testing command for CommandEvent.completeNick(...)
@Command(name="ping", desc="Sends a PONG")
public class Ping extends GenericCommand
{
	public Ping(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		if (event.hasNoArgs())
		{
			event.respond(user.getNick() + " PONG!");
		}
		else
		{
			String nick = event.completeNick(0);
			event.respond(nick + ": PONG!");
		}
	}
}
