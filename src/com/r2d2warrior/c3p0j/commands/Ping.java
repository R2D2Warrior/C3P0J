package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.commands.Command.Sub;
import com.r2d2warrior.c3p0j.handling.CommandEvent;

@Command(name="ping", desc="Sends a PONG")
public class Ping extends GenericCommand
{
	public Ping(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	@Sub(name="sub1")
	public void execute1()
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
	
	@Sub(name="sub2")
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
