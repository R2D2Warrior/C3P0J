package com.r2d2warrior.c3p0j.commands;

import java.util.regex.Pattern;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.WhoisEvent;

import com.google.common.collect.ImmutableList;
import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.utils.Utils;

@Command(name="unban", desc="Removes any matching bans on a nick", syntax="unban <nick>", requiresArgs=true)
public class SmartUnban extends GenericCommand
{
	public SmartUnban(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		WhoisEvent<PircBotX> whois = Utils.getWhoisInfo(bot, event.getArgumentList().get(0));
		String hostMask = whois.getNick() + "!" + whois.getLogin() + "@" + whois.getHostname();
		
		/*
		 *  Looping through channel.getBanList() while also removing bans will cause a
		 *  ConcurrentModificationException, so create a copy and loop through it instead
		 */
		ImmutableList<String> banListSnapshot = ImmutableList.copyOf(channel.getBanList());
		
		for (String banMask : banListSnapshot)
		{
			// Replace the stars in the banMask with their equivalent for regular expressions
			String regexMask = banMask.replace("*", ".+");
			Pattern pattern = Pattern.compile(regexMask, Pattern.CASE_INSENSITIVE);
			if (pattern.matcher(hostMask).matches()) // If the banMask (with wildcards) matches the hostmask in question, remove it
				channel.send().unBan(banMask);
		}
	}
}
