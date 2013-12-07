package com.r2d2warrior.c3p0j.handling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericChannelUserEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class CommandEvent<T extends PircBotX> extends Event<T> implements GenericMessageEvent<T>, GenericChannelUserEvent<T>
{
	@Getter(onMethod = @_(@Override))
	protected Channel channel;
	@Getter(onMethod = @_(@Override))
	protected User user;
	@Getter(onMethod = @_(@Override))
	protected String message;
	@Getter
	protected String prefix;
	@Getter
	protected String commandName;
	@Getter
	protected List<String> commandArgs;
	
	public CommandEvent(T bot, @NonNull Channel channel, @NonNull User user, @NonNull String message)
	{
		super(bot);
		this.user = user;
		this.channel = channel;
		String[] msgSplit = message.split(" ", 2);
		
		this.prefix = msgSplit[0].substring(0, 1);
		this.commandName = msgSplit[0].substring(1);
		
		if (msgSplit.length > 1)
			this.commandArgs = Arrays.asList(msgSplit[1].split(" "));
		else
			this.commandArgs = new ArrayList<String>();
	}

	@Override
	public void respond(String response)
	{
		if (getBot().getConfiguration().getPrefixes().get(prefix).equals("NOTICE"))
			getUser().send().notice(response);
		else if (getBot().getConfiguration().getPrefixes().get(prefix).equals("MESSAGE"))
			getChannel().send().message(response);
	}
	
	public void respondToUser(String response)
	{
		getUser().send().notice(response);
	}
	
	public boolean hasNoArgs()
	{
		return commandArgs.isEmpty();
	}
	
	public String getArgString()
	{
		return StringUtils.join(this.commandArgs, " ");
	}
}
