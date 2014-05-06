package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

//Testing command for CommandEvent.completeNick(...)
@Command(name="ping", desc="Sends a PONG")
@Command(name="ping2", method="execute2")
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
	
	public void execute2()
	{
		if (event.hasNoArgs())
		{
			event.respond(user.getNick() + " Alternate PONG!");
		}
		else
		{
			String nick = event.completeNick(0);
			event.respond(nick + ": Alternate PONG!");
		}	}
}
