package com.r2d2warrior.c3p0j.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.utils.Config;

@Command(name="admins", desc="Lists bot admin accounts")
public class Admins extends GenericCommand
{
	
	public Admins(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	@Command.Default
	@Command.Sub(name="list")
	public void list()
	{
		List<String> onlineAdmins = new ArrayList<>();
		List<String> offlineAdmins = new ArrayList<>(config.getAdminAccounts());
		for (User user : userChannelDao.getAllUsers())
		{
			if (user.isAdmin())
			{
				String admin = user.getNick();
				admin += (!user.getNick().equalsIgnoreCase(user.getAccount())) ? " (" + user.getAccount() + ")" : "";
				onlineAdmins.add(admin);
				offlineAdmins.remove(user.getAccount());
			}
		}
		event.respond(
				"Online: " + Colors.setColor(StringUtils.join(onlineAdmins, ", "), Colors.GREEN + Colors.BOLD) +
				" | Offline: " + Colors.setColor(StringUtils.join(offlineAdmins, " ,"), Colors.RED + Colors.BOLD));
	}
	
	@Command.Sub(name="add", requiresArgs=true, adminOnly=true)
	public void add()
	{
		Config c = new Config("config.json");
		c.getStringList("bot", "adminAccounts").addAll(event.getArgumentList());
		c.update(bot);
		event.respondToUser("Added admins: " + StringUtils.join(event.getArgumentList(), ", "));
	}
	
	@Command.Sub(name="remove", requiresArgs=true, adminOnly=true)
	public void remove()
	{
		Config c = new Config("config.json");
		c.getStringList("bot", "adminAccounts").removeAll(event.getArgumentList());
		c.update(bot);
		event.respondToUser("Removed admins: " + StringUtils.join(event.getArgumentList(), ", "));
	}
}
