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
	
	protected CommandEvent<PircBotX> event;
	protected User user;
	protected Channel channel;
	protected PircBotX bot;
	protected Configuration<PircBotX> config;
	protected UserChannelDao<User, Channel> userChannelDao;
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
	
	public CommandInfo<GenericCommand> getCommandInfo()
	{
		return info;
	}
	
	public abstract void execute();
}
