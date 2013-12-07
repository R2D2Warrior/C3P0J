package com.r2d2warrior.c3p0j.handling;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter<PircBotX>
{

	@SuppressWarnings("unchecked")
	public void onCommand(CommandEvent<PircBotX> event)
	{
		event.getBot().getCommandRegistry().executeCommand(event);
	}
}
