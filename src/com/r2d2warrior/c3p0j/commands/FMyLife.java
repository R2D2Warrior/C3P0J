package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.utils.WebUtils;

@Command(name="fml", desc="Gets a random quote from FMyLife.com/random")
public class FMyLife extends GenericCommand
{
	public FMyLife(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		event.respond(WebUtils.getRandomFML());
	}
}
