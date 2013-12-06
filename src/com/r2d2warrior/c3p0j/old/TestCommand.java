package com.r2d2warrior.c3p0j.old;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.events.MessageEvent;

@SuppressWarnings("rawtypes")
public class TestCommand
{

	@MessageCommand(name="say", info="Sends message to current channel", adminOnly=true)
	public void sayCommand(MessageEvent event, String[] args)
	{
		String message = StringUtils.join(args, " ");
		event.getChannel().send().message(message);
	}
}
