package com.r2d2warrior.c3p0j.commands;

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
		String factoid = event.getArgumentList().get(0);
		String data = event.getArgRange(1);
		
		if (!FactoidManager.factoidExists(factoid) && StringUtils.isNotBlank(data))
		{	
			FactoidManager.addFactoid(factoid, data);
			event.respond("Saved factoid: " + factoid);
		}
		else if (FactoidManager.factoidExists(factoid))
			event.respondToUser("Error: Factoid \"" + factoid + "\" already exists.");
		else
			event.respondToUser("Error: Nothing to save.");
	}
	
	public void forget()
	{
		String factoid = event.getArgumentList().get(0);
		
		if (FactoidManager.factoidExists(factoid))
		{
			FactoidManager.removeFactoid(factoid);
			event.respond("Removed factoid: " + factoid);
		}
		else
			event.respondToUser("Error: Factoid \"" + factoid + "\" doesn't exist.");
	}
}
