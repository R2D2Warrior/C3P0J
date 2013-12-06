package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.command.CommandEvent;

public abstract class GenericCommand
{
	protected CommandEvent<PircBotX> event;
	
	public GenericCommand(CommandEvent<PircBotX> event)
	{
		this.event = event;
	}
	
	public abstract void execute();
}
