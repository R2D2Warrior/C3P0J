package com.r2d2warrior.c3p0j.listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

public class CommandListener extends ListenerAdapter<PircBotX>
{

	public void onCommand(CommandEvent<PircBotX> event)
	{
		boolean worked = event.getBot().getCommandRegistry().executeCommand(event);
		if (!worked)
			event.respondToUser("Error while executing command: " + event.getCommandName());
	}
}
