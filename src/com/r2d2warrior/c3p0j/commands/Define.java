package com.r2d2warrior.c3p0j.commands;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.ParseException;
import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.utils.Utils;
import com.r2d2warrior.c3p0j.utils.WebUtils;

@Command(name="define", desc="Gets the definition for a word (duckduckgo.com)", syntax="define <word> [def #]", requiresArgs=true)
public class Define extends GenericCommand
{
	public Define(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		List<String> args = event.getArgumentList();
		String lastArg = args.get(args.size()-1);
		
		int defNum = 1;
		String searchTerm = event.getArguments();
		if (StringUtils.isNumeric(lastArg))
		{
			defNum = Integer.parseInt(lastArg);
			if (defNum < 1)
			{
				event.respondToUser("Definition number must be above 0. Returning first definition.");
				defNum = 1;
			}
			searchTerm = event.getArgRange(0, args.size()-2);
		}
		
		try
		{
			List<Map<String, String>> defs = WebUtils.getDefinitions(searchTerm);
			if (defs.size() == 0)
			{
				event.respondToUser("No definitions found for \"" + searchTerm + "\".");
				return;
			}
			else if (defNum > defs.size())
			{
				event.respondToUser("There are only " + defs.size() + " definitions. Returning last definition.");
				defNum = defs.size();
			}
			String def = defs.get(defNum-1).get("Text");
			String url = defs.get(defNum-1).get("FirstURL");
			event.respond(Utils.toSentenceCase(searchTerm) + ": [" + defNum + "/" + defs.size() + "] " + def + " [" + url + "]");
		}
		catch (IOException | ParseException e)
		{
			event.respondToUser("An error occurred while getting definition.");
			e.printStackTrace();
		}
	}
}
