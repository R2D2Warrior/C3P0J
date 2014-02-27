package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

@Command(name="quit", desc="Disconnects the bot from the server", syntax = "quit [message]", adminOnly=true)
public class Quit extends GenericCommand
{
	private CommandEvent<PircBotX> event;
	
	public Quit(CommandEvent<PircBotX> event)
	{
		super(event);
		this.event = event;
	}
	
	public void execute()
	{
		String msg = (event.hasNoArgs()) ? " by " + event.getUser().getNick() : ": " + event.getArguments();
		event.respond("Disconnecting...");
		event.getBot().sendIRC().quitServer(msg);
	}
}
