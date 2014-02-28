package com.r2d2warrior.c3p0j.listeners;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

public class CommandListener extends ListenerAdapter<PircBotX>
{

	public void onCommand(CommandEvent<PircBotX> event)
	{
		String result = event.getBot().getCommandRegistry().executeCommand(event);
		
		if (!StringUtils.isBlank(result))
			event.respondToUser(result);
	}
}
