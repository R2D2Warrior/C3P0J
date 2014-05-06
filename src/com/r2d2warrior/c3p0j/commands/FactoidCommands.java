package com.r2d2warrior.c3p0j.commands;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.handling.FactoidManager;

@Command(name="remember", alt="r", desc="Saves, or \"remembers\", a factoid. Call with ?<factoidName>", syntax="remember <name> <data>", method="remember", requiresArgs=true, adminOnly=true)
@Command(name="forget", alt="f", desc="Removes, or \"forgets\", a factoid", syntax="forget <factoidName>", method="forget", requiresArgs=true, adminOnly=true)
public class FactoidCommands extends GenericCommand
{
	public FactoidCommands(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void remember()
	{
		FactoidManager factoidManager = bot.getFactoidManager();

		String factoid = event.getArgumentList().get(0);
		String data = event.getArgRange(1);
		
		try
		{
			if (!factoidManager.factoidExists(factoid) && StringUtils.isNotBlank(data))
			{	
				factoidManager.addFactoid(factoid, data);
				event.respond("Saved factoid: " + factoid);
			}
			else if (factoidManager.factoidExists(factoid))
				event.respondToUser("Error: Factoid \"" + factoid + "\" already exists.");
			else
				event.respondToUser("Error: Nothing to save.");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			event.respondToUser("An SQL error occurred while adding factoid.");
		}
	}
	
	public void forget()
	{
		FactoidManager factoidManager = bot.getFactoidManager();
		
		String factoid = event.getArgumentList().get(0);
		
		try
		{
			if (factoidManager.factoidExists(factoid))
			{
				factoidManager.removeFactoid(factoid);
				event.respond("Removed factoid: " + factoid);
			}
			else
				event.respondToUser("Error: Factoid \"" + factoid + "\" doesn't exist.");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			event.respondToUser("An SQL error occurred while adding factoid.");
		}
	}
}
