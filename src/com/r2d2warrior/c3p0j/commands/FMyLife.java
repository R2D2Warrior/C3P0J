package com.r2d2warrior.c3p0j.commands;

import java.io.IOException;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.utils.WebUtils;

@Command(name="fml", desc="Gets a random quote from http://www.fmylife.com/random")
public class FMyLife extends GenericCommand
{
	public FMyLife(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		try
		{
			event.respond(WebUtils.getRandomFML());
		}
		catch (IOException e)
		{
			event.respondToUser("An error occurred while connecting to or reading URL.");
			e.printStackTrace();
		}
	}
}
