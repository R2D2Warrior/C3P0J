package com.r2d2warrior.c3p0j.handling;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import lombok.Getter;
import lombok.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrTokenizer;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericChannelUserEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import com.r2d2warrior.c3p0j.commands.GenericCommand;
import com.r2d2warrior.c3p0j.utils.Utils;

@Getter
public class CommandEvent<T extends PircBotX> extends Event<T> implements GenericMessageEvent<T>, GenericChannelUserEvent<T>
{
	protected Channel channel;
	protected User user;
	protected String message;
	protected String prefix;
	protected String commandName;
	protected String arguments;
	protected CommandInfo<GenericCommand> commandInfo;
	
	public CommandEvent(T bot, @Nullable Channel channel, @NonNull User user, @NonNull String message)
	{
		super(bot);
		this.user = user;
		this.channel = channel;
		this.message = message;
		
		List<String> msg = new StrTokenizer(message).getTokenList();
		this.arguments = (msg.size() > 1) ? StringUtils.split(message, " ", 2)[1] : "";
		
		if (channel != null)
		{
			this.prefix = msg.get(0).substring(0, 1);
			this.commandName = msg.get(0).substring(1);
		}
		else
		{
			this.prefix = null;
			this.commandName = msg.get(0);
		}
		
		this.commandInfo = bot.getCommandRegistry().getCommandInfo(commandName);
	}

	@Override
	public void respond(String response)
	{
		if (prefix != null)
		{
			if (getBot().getConfiguration().getPrefixes().get(prefix).equals("NOTICE"))
				getUser().send().notice(response);
			else if (getBot().getConfiguration().getPrefixes().get(prefix).equals("MESSAGE"))
				getChannel().send().message(response);
		}
		else
		{
			getUser().send().message(response);
		}
	}
	
	public void respondToUser(String response)
	{
		getUser().send().notice(response);
	}
	
	public String completeNick(String oldNick)
	{
		List<User> usersInChannel = bot.getUserChannelDao().getUsers(channel).asList();
		List<String> matchUsers = new ArrayList<>();
		for (User u : usersInChannel)
		{
			if (u.getNick().toLowerCase().startsWith(oldNick.toLowerCase()))
				matchUsers.add(u.getNick());
		}
		
		if (matchUsers.size() > 1 && matchUsers.size() <= 5)
		{
			respondToUser("Did you mean " + Utils.commaWithOr(matchUsers));
			throw new IllegalArgumentException("Nick completion failed.");
		}
		else if (matchUsers.size() > 5)
		{
			respondToUser("More than 5 matches for \"" + oldNick + ",\" be more specific.");
			throw new IllegalArgumentException("Nick completion failed.");
		}
		else if (matchUsers.size() == 1)
		{
			return matchUsers.get(0);
		}
		else
		{
			throw new IllegalArgumentException("Nick completion failed.");
		}
	}
	
	public String completeNick(int argIndex)
	{
		return completeNick(getArgumentList().get(argIndex));
	}
	
	public List<String> getArgumentList()
	{
		return new StrTokenizer(arguments).getTokenList();
	}
	
	public boolean hasNoArgs()
	{
		return StringUtils.isBlank(arguments);
	}
	
	public boolean hasChannelArg()
	{
		return !hasNoArgs() && getBot().getConfiguration().getChannelPrefixes().contains(getArgumentList().get(0).substring(0, 1));
	}
	
	public String getArgRange(int start)
	{
		return Utils.getRange(getArgumentList(), start);
	}
	
	public String getArgRange(int start, int end)
	{
		return Utils.getRange(getArgumentList(), start, end);
	}
}
