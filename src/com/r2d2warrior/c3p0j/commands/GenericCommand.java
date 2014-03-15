package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserChannelDao;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.handling.CommandInfo;

public abstract class GenericCommand
{
	/** The {@link CommandEvent} event instance*/
	protected CommandEvent<PircBotX> event;
	/** The {@link User} instance that triggered the command*/
	protected User user;
	/** The {@link Channel} instance where this command was triggered. Will be <code>null</code> for private message commands*/
	protected Channel channel;
	/** The {@link PircBotX} instance (<code>event.getBot()</code>)*/
	protected PircBotX bot;
	/** The {@link Configuration} instance (<code>bot.getConfiguration()</code>)*/
	protected Configuration<PircBotX> config;
	/** The {@link UserChannelDao} instance (<code>bot.getUserChannelDao()</code>)*/
	protected UserChannelDao<User, Channel> userChannelDao;
	/** The {@link CommandInfo} object that holds command information*/
	protected CommandInfo<GenericCommand> info;
	
	public GenericCommand(CommandEvent<PircBotX> event)
	{
		this.event = event;
		this.user = event.getUser();
		this.channel = event.getChannel();
		this.bot = event.getBot();
		this.config = bot.getConfiguration();
		this.userChannelDao = bot.getUserChannelDao();
		this.info = event.getBot().getCommandRegistry().getCommandInfo(event);
	}
	
	/**
	 * Executes the command
	 */
	public abstract void execute();
}
