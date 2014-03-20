package com.r2d2warrior.c3p0j.commands;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.utils.WebUtils;

@Command(name="calc", desc="Calculates a math expression (duckduckgo.com)", syntax = "calc <expression>", requiresArgs=true)
public class Calc extends GenericCommand
{
	public Calc(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		try
		{
			String response = WebUtils.getCalculation(event.getArguments());
			event.respond(response);
		}
		catch (IOException | ParseException | IllegalArgumentException e)
		{
			event.respondToUser("An error occurred while calculating: " + event.getArguments());
			e.printStackTrace();
		}
	}
}
