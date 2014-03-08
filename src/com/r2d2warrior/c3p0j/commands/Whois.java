package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.WhoisEvent;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.utils.Utils;

@Command(name="whois", desc="Gets information on a nick without the user of User.class", syntax="whois <nick>", requiresArgs=true)
public class Whois extends GenericCommand
{
	public Whois(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		WhoisEvent<PircBotX> whois = Utils.getWhoisInfo(event.getBot(), event.getArgumentList().get(0));
		
		String response =
				String.format("%s!%s@%s#%s, Logged in as %s on server %s",
					whois.getNick(), whois.getLogin(), whois.getHostname(),
					whois.getRealname(), whois.getRegisteredAs(), whois.getServer()
				);
		
		event.respond(response);
	}
}
