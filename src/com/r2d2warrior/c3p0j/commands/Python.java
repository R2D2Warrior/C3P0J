package com.r2d2warrior.c3p0j.commands;

import java.io.IOException;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.utils.WebUtils;

@Command(name="python", alias="py", requiresArgs=true)
public class Python extends GenericCommand
{
	
	public Python(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		String code = event.getArguments();
		try
		{
			event.respond(WebUtils.evaluatePython(code));
		}
		catch (IOException e)
		{
			event.respondToUser("An error occurred while evaluating python code");
			e.printStackTrace();
		}
	}
	
}
