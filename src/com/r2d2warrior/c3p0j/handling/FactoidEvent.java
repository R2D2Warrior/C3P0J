package com.r2d2warrior.c3p0j.handling;

import javax.annotation.Nullable;

import lombok.Getter;
import lombok.NonNull;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericChannelUserEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

@Getter
public class FactoidEvent<T extends PircBotX> extends Event<T> implements GenericMessageEvent<T>, GenericChannelUserEvent<T>
{
	protected Channel channel;
	protected User user;
	protected String message;
	protected String factoidName;
	
	public FactoidEvent(T bot, @Nullable Channel channel, @NonNull User user, @NonNull String message)
	{
		super(bot);
		this.channel = channel;
		this.user = user;
		this.message = message;
		this.factoidName = message.split(" ")[0].substring(1);
	}
	
	public void respond(String response)
	{
		if (channel != null)
			channel.send().message(response);
		else
			user.send().message(response);
	}
}
