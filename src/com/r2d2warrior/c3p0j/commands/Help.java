package com.r2d2warrior.c3p0j.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.handling.CommandInfo;

@Command(name="help", desc="Displays command list or command information", syntax="help [commandName]")
public class Help extends GenericCommand
{
	private CommandEvent<PircBotX> event;
	
	public Help(CommandEvent<PircBotX> event)
	{
		super(event);
		this.event = event;
	}
	
	public void execute()
	{
		if (event.hasNoArgs())
		{
			event.respond("Valid command prefixes: " + event.getBot().getConfiguration().getPrefixes().toString());
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
			String cmd = event.getArgumentsList().get(0);
			if (event.getBot().getCommandRegistry().isCommand(cmd))
			{
				CommandInfo<GenericCommand> info = event.getBot().getCommandRegistry().getCommandInfo(cmd);
				String desc = info.getDesc();
				String syntax = StringUtils.isEmpty(info.getSyntax()) ? cmd : info.getSyntax();
				if (info.isAdminOnly())
					event.respondToUser(cmd.toUpperCase() + " - " + syntax + ": " + desc);
				else
					event.respond(cmd.toUpperCase() + " - " + syntax + ": " + desc);
			}
			else
				event.respondToUser("No such command: " + cmd);
		}
	}
}
