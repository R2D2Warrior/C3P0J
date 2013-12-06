package com.r2d2warrior.c3p0j.old;

import java.util.Arrays;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import com.google.common.collect.ImmutableList;

@SuppressWarnings("rawtypes")
public class OldCommandListener extends ListenerAdapter
{
	public void onMessage(MessageEvent event)
	{
		ImmutableList<String> msgSplit = ImmutableList.copyOf(Arrays.asList(event.getMessage().split(" ", 2)));
		String prefix = msgSplit.get(0).substring(0, 1);
		String cmd = msgSplit.get(0).substring(1);
		
/*		if (event.getBot().getConfiguration().getPrefixes().containsKey(prefix)
			&& event.getBot().getConfiguration().getCommands().contains(cmd));
		{
			CommandHandler ch = new CommandHandler(event);
			ch.handleLine(event.getMessage());
		}*/
	}
	
	// TODO: Make Private Messages not require a prefix
	public void onPrivateMessage(PrivateMessageEvent event)
	{
		ImmutableList<String> msgSplit = ImmutableList.copyOf(Arrays.asList(event.getMessage().split(" ", 2)));
		String prefix = msgSplit.get(0).substring(0, 1);
		String cmd = msgSplit.get(0).substring(1);
		
/*		if (event.getBot().getConfiguration().getPrefixes().containsKey(prefix)
				&& event.getBot().getConfiguration().getCommands().contains(cmd));
			{
				CommandHandler ch = new CommandHandler(event);
				ch.handleLine(event.getMessage());
			}*/
	}
}
