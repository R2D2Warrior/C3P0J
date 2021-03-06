package com.r2d2warrior.c3p0j.commands;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;

import com.google.common.collect.BiMap;
import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.handling.CommandInfo;

@Command(name="help", desc="Displays command list or command information", syntax="help [commandName]")
public class Help extends GenericCommand
{
	
	public Help(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	@Command.Default
	public void execute()
	{
		if (event.hasNoArgs())
		{
			BiMap<String, String> prefixes = config.getPrefixes();
			
			event.respond("Valid command prefixes: " + prefixes.keySet().toString());
			event.respond("Syntax infomation -- Required: <arg>, Optional: [arg]");
			List<String> availCommands = bot.getCommandRegistry().getCommandsForGroup(user.getGroup().getName());
			event.respond("Commands available for your group (" + user.getGroup().getName().toUpperCase() + "): " + StringUtils.join(availCommands, ", "));
		}
		else
		{
			String cmd = event.getArgumentList().get(0);
			if (bot.getCommandRegistry().isCommand(cmd))
			{
				CommandInfo<GenericCommand> info = bot.getCommandRegistry().getCommandInfo(cmd);
				String desc = info.getDesc();
				String syntax = StringUtils.isEmpty(info.getSyntax()) ? info.getName() : info.getSyntax();
				event.respond(cmd.toUpperCase() + " - " + syntax + ": " + desc);
			}
			else
				event.respondToUser("No such command: " + cmd);
		}
	}
	
	@Command.Sub(name="group", requiresArgs=true)
	public void group()
	{
		String groupName = event.getArgumentList().get(0);
		if (bot.getPermissions().getGroup(groupName) != null)
			event.respond("Commands for minimum group " + groupName.toUpperCase() + ": " +
					StringUtils.join(bot.getCommandRegistry().getCommandsMinGroup(groupName), ", "));
		else
			event.respondToUser("Group does not exist: " + groupName);
	}
}
