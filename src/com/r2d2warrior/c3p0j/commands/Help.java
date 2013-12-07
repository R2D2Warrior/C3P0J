package com.r2d2warrior.c3p0j.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.handling.CommandInfo;

@Command(name="help", desc="Displays command list or command information")
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
			event.respondToUser("Admin Commands: " + StringUtils.join(adminCommands, ", "));
		}
		else
		{
			String cmd = event.getCommandArgs().get(0);
			if (event.getBot().getCommandRegistry().isCommand(cmd))
			{
				String desc = event.getBot().getCommandRegistry().getCommandInfo(cmd).getDesc();
				event.respond(cmd.toLowerCase() + " - " + desc);
			}
			else
				event.respondToUser("No such command: " + cmd);
		}
	}
}
