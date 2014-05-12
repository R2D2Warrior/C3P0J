package com.r2d2warrior.c3p0j.commands;

import java.sql.SQLException;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.handling.FactoidManager;

@Command(name="factoids", desc="Factoid management command.", syntax="factoids <add|del|list> [factoid] [data]", requiresArgs=true)
public class FactoidCommands extends GenericCommand
{
	public FactoidCommands(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	@Command.Default
	public void info()
	{
		event.respondToUser("SYNTAX: " + event.getCommandInfo().getSyntax());
	}
	
	@Command.Sub(name="add", adminOnly=true, requiresArgs=true)
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
	
	@Command.Sub(name="del", adminOnly=true, requiresArgs=true)
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
	
	@Command.Sub(name="list")
	public void list()
	{
		FactoidManager factoidManager = bot.getFactoidManager();

		Set<String> factoidNames = factoidManager.getAllFactoids().keySet();
		String factoidList = StringUtils.join(factoidNames, ", ");
		event.respond("Available factoids: " + factoidList);
	}
}
