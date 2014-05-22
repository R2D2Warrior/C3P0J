package com.r2d2warrior.c3p0j.commands;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.utils.Permissions;

@Command(name="perm", desc="Manage Permissions", syntax="perm <set|rem|list|get> [user] <groupName>")
public class PermCommand extends GenericCommand
{
	public PermCommand(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	@Command.Default
	public void help()
	{
		event.respondToUser("SYNTAX:" + event.getCommandInfo().getSyntax());
	}
	
	@Command.Sub(name="get", requiresArgs=true)
	public void get()
	{
		String account = event.getArgumentList().get(0);
		Permissions.Group group = bot.getPermissions().getUserGroup(account);
		event.respond(account + " is in group " + group.getName());
	}
	
	@Command.Sub(name="list", alias="l")
	public void list()
	{
		if (event.hasNoArgs())
		{
			event.respond("Groups: " + StringUtils.join(bot.getPermissions().getGroupNames(), ", "));
		}
		else
		{
			String groupName = event.getArgumentList().get(0);
			if (bot.getPermissions().getGroupNames().contains(groupName))
			{
				event.respond("Users in group " + groupName.toUpperCase() + ": " +
						StringUtils.join(bot.getPermissions().getGroup(groupName).getUsers(), ", "));
			}
			else
				event.respondToUser("No such group: " + groupName.toUpperCase());
		}
	}
	
	@Command.Sub(name="set", requiresArgs=true, minGroup="owner")
	public void set()
	{
		if (event.getArgumentList().size() < 2)
			event.respondToUser("Error: Not enough arguments. SYNTAX:" + event.getCommandInfo().getSyntax());
		else
		{
			String account = event.getArgumentList().get(0);
			String groupName = event.getArgumentList().get(1);
			bot.getPermissions().setUserGroup(account, groupName);
			event.respondToUser("Set group of " + account + " to " + groupName.toUpperCase() + ".");
		}
	}
	
	@Command.Sub(name="remove", alias={"rem", "del", "delete"}, requiresArgs=true, minGroup="owner")
	public void remove()
	{
		String account = event.getArgumentList().get(0);
		String oldGroup = bot.getPermissions().getUserGroup(account).getName().toUpperCase();
		bot.getPermissions().removeUser(account);
		if (oldGroup.equals(Permissions.DEFAULT_GROUP))
			event.respondToUser("Error: " + account + " is not in a group.");
		else
			event.respondToUser("Removed " + account + " from group " + oldGroup);
	}
}
