package com.r2d2warrior.c3p0j.commands;

import java.util.ArrayList;
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
	
	public void execute()
	{
		if (event.hasNoArgs())
		{
			BiMap<String, String> prefixes = config.getPrefixes();
			
			event.respond("Valid command prefixes: " + prefixes.keySet().toString());
			event.respond("Syntax infomation -- Required: <arg>, Optional: [arg]");
			
			List<String> commandList = new ArrayList<String>();
			List<String> adminCommands = new ArrayList<String>();
			
			for (CommandInfo<GenericCommand> info : event.getBot().getCommandRegistry().getCommands())
			{
				if (!info.isAdminOnly())
					commandList.add(info.getName());
				else if (event.getUser().isAdmin())
					adminCommands.add(info.getName());
			}
			
			event.respond("Commands: " + StringUtils.join(commandList, ", "));
			
			if (event.getUser().isAdmin())
				event.respondToUser("Admin Commands: " + StringUtils.join(adminCommands, ", "));
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
}
