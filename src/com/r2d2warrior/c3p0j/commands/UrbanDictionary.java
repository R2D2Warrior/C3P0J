package com.r2d2warrior.c3p0j.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.utils.Utils;
import com.r2d2warrior.c3p0j.utils.WebUtils;

@Command(name="ud", desc="Searches UrbanDictionary for a term", syntax="ud <term> [definition number]")
public class UrbanDictionary extends GenericCommand
{
	public UrbanDictionary(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		if (event.hasNoArgs())
		{
			event.respondToUser("SYNTAX: " + info.getSyntax());
			return;
		}
		
		List<String> args = event.getArgumentsList();
		int defNum = 1;
		String searchTerm = "";
		
		if (args.size() > 1 && StringUtils.isNumeric(args.get(args.size()-1)))
		{
			defNum = Integer.parseInt(args.get(args.size()-1));
			if (defNum < 1)
			{
				event.respondToUser("Definition number must be positive");
				return;
			}
			searchTerm = event.getArgRange(0, args.size()-2);
		}
		else
		{
			defNum = 1;
			searchTerm = event.getArguments();
		}
		
		List<Map<String, String>> defs = WebUtils.getUrbanDictionDefinitions(searchTerm);
		if (defNum > defs.size())
		{
			event.respondToUser("There are only " + defs.size() + " definitions.");
			return;
		}
		String def = defs.get(defNum-1).get("definition");
		String url = defs.get(defNum-1).get("permalink");
		event.respond(Utils.firstCap(searchTerm) + ": [" + defNum + "/" + defs.size() + "] " + def + " [" + url + "]");
	}
}
