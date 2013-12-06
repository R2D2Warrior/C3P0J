package com.r2d2warrior.c3p0j.old;

import org.pircbotx.hooks.Event;

@SuppressWarnings("rawtypes")
public class SayCommand
{
	public static void call(Event event, String defaultCommand, String args)
	{
		event.getBot().sendRaw().rawLine(defaultCommand + args);
	}
}
