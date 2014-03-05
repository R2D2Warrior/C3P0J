package com.r2d2warrior.c3p0j.commands;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.utils.WebUtils;

@Command(name="define", desc="Gets the definition for a word (ninjawords.com)", syntax="define <word> [def #]", requiresArgs=true)
public class Define extends GenericCommand
{
	public Define(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		List<String> args = event.getArgumentsList();
		String lastArg = args.get(args.size()-1);
		
		int defNum = 1;
		String searchTerm = event.getArguments();
		if (StringUtils.isNumeric(lastArg))
		{
			defNum = Integer.parseInt(lastArg);
			searchTerm = event.getArgRange(0, args.size()-2);
		}
		
		try
		{
			event.respond(WebUtils.getDefinition(searchTerm, defNum));
		}
		catch (IOException e)
		{
			event.respondToUser("Error while connecting to http://ninjawords.com/");
			e.printStackTrace();
		}
	}
}
