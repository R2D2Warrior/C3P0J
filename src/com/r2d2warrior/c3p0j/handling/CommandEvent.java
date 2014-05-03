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
	
	/**
	 * Create a command event. Supply <code>null</code> for <code>channel</code> argument for private messages
	 * 
	 * @param bot A {@link PircBotX} instance.
	 * @param channel  The {@link Channel} instance where the command was sent. Can be <code>null</code>.
	 * @param user The {@link User} instance that executed this command.
	 * @param message The message that was sent.
	 */
	public CommandEvent(T bot, @Nullable Channel channel, @NonNull User user, @NonNull String message)
	{
		super(bot);
		this.user = user;
		this.channel = channel;
		this.message = message;
		
		List<String> msg = new StrTokenizer(message).getTokenList();
		this.arguments = (msg.size() > 1) ? Utils.getRange(msg, 1) : "";
		
		if (channel != null)
		{
			if (msg.get(0).equalsIgnoreCase(bot.getNick() + ","))
			{
				this.prefix = bot.getNick() + ",";
				this.commandName = msg.get(1);
				this.arguments = Utils.getRange(msg, 2);
			}
			else
			{
				this.prefix = msg.get(0).substring(0, 1);
				this.commandName = msg.get(0).substring(1);
			}
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
			if (prefix.equalsIgnoreCase(bot.getNick() + ","))
				getChannel().send().message(response);
			else if (getBot().getConfiguration().getPrefixes().get(prefix).equals("NOTICE"))
				getUser().send().notice(response);
			else if (getBot().getConfiguration().getPrefixes().get(prefix).equals("MESSAGE"))
				getChannel().send().message(response);
		}
		else
		{
			getUser().send().message(response);
		}
	}
	
	/**
	 * Send a response, as a notice, to the user who sent the command
	 * @param response The response message
	 */
	public void respondToUser(String response)
	{
		getUser().send().notice(response);
	}
	
	/**
	 * Attempts to complete the nick <code>oldNick</code> using all nicks in current channel
	 * @param oldNick The partial nick to be compelted
	 * @return A completed nick <i>if and only if</i> there is only one match
	 */
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
	
	/**
	 * Utility method for {@link #completeNick(String oldNick)}, supplying the argument at <code>argIndex</code> for <code>oldNick</code>
	 * @param argIndex The argument number to supply to {@link #completeNick(String oldNick)}
	 * @return A completed nick <i>if and only if</i> there is only one match
	 */
	public String completeNick(int argIndex)
	{
		return completeNick(getArgumentList().get(argIndex));
	}
	
	/**
	 * Gets a list of command arguments
	 * @return A <code>List{@code<String>}</code> of command arguments
	 */
	public List<String> getArgumentList()
	{
		return new StrTokenizer(arguments).getTokenList();
	}
	
	/**
	 * Check if the command has arguments or not
	 * @return <code>true</code> if the command has arguments, otherwise <code>false</code>
	 */
	public boolean hasNoArgs()
	{
		return StringUtils.isBlank(arguments);
	}
	
	/**
	 * Check if the first argument is a channel
	 * @return <code>true</code> if the command has arguments <i>and</i> the first argument starts with a channel prefix, otherwise <code>false</code>
	 */
	public boolean hasChannelArg()
	{
		return !hasNoArgs() && getBot().getConfiguration().getChannelPrefixes().contains(getArgumentList().get(0).substring(0, 1));
	}
	
	/**
	 * Utility method for {@link #getArgRange(int start, int end)}, where <code>end</code> is the end of all arguments
	 * @param start The index to start at
	 * @return Arguments from <code>start</code> to the end of the message
	 */
	public String getArgRange(int start)
	{
		return Utils.getRange(getArgumentList(), start);
	}
	
	/**
	 * Get a range of arguments from <code>start</code> to <code>end</code>, both inclusive
	 * @param start The index to start at
	 * @param end The index to end at
	 * @return Arguments from <code>start</code> to <code>end</code>
	 */
	public String getArgRange(int start, int end)
	{
		return Utils.getRange(getArgumentList(), start, end);
	}
}
