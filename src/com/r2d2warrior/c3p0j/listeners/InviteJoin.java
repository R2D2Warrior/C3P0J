package com.r2d2warrior.c3p0j.listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.InviteEvent;

@AddListener
public class InviteJoin extends ListenerAdapter<PircBotX>
{
	public void onInvite(InviteEvent<PircBotX> event)
	{
		event.getBot().sendIRC().joinChannel(event.getChannel());
	}
}
