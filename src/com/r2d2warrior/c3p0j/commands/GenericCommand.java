package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.handling.CommandInfo;

public abstract class GenericCommand
{
	
	protected CommandEvent<PircBotX> event;
	protected CommandInfo<GenericCommand> info;
	
	public GenericCommand(CommandEvent<PircBotX> event)
	{
		this.event = event;
		this.info = event.getBot().getCommandRegistry().getCommandInfo(event);
	}
	
	public CommandInfo<GenericCommand> getCommandInfo()
	{
		return info;
	}
	
	public abstract void execute();
}
