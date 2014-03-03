package com.r2d2warrior.c3p0j.commands;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.handling.FactoidManager;

@Command(name="remember", desc="Saves a factoid to file. Call with ?<factoidName>", syntax="remember <name> <data>", requiresArgs = true, adminOnly = true)
public class Remember extends GenericCommand
{
	public Remember(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		String factoid = event.getArgumentsList().get(0);
		String data = event.getArgRange(1);
		
		if (!FactoidManager.factoidExists(factoid) && !StringUtils.isNotBlank(data))
		{	
			FactoidManager.addFactoid(factoid, data);
			event.respond("Saved factoid: " + factoid);
		}
		else if (FactoidManager.factoidExists(factoid))
			event.respondToUser("Error: Factoid \"" + factoid + "\" already exists.");
		else
			event.respondToUser("Error: Nothing to save.");
	}
}

@Command(name="forget", requiresArgs = true, adminOnly = true)
class Forget extends GenericCommand
{
	public Forget(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		String factoid = event.getArgumentsList().get(0);
		
		if (FactoidManager.factoidExists(factoid))
		{
			FactoidManager.removeFactoid(factoid);
			event.respond("Removed factoid: " + factoid);
		}
		else
			event.respondToUser("Error: Factoid \"" + factoid + "\" doesn't exist.");
	
	}
}
