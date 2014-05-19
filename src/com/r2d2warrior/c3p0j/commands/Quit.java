package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

// TODO Restart bot command? (possible?)
@Command(name="quit", desc="Disconnects the bot from the server", syntax = "quit [message]", minGroup="admin")
public class Quit extends GenericCommand
{
	
	public Quit(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		String msg = (event.hasNoArgs()) ? " by " + user.getNick() : ": " + event.getArguments();
		event.respond("Disconnecting...");
		bot.sendIRC().quitServer(msg);
	}
}
