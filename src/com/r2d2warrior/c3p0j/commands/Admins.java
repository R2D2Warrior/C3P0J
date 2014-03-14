package com.r2d2warrior.c3p0j.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import com.r2d2warrior.c3p0j.handling.CommandEvent;

@Command(name="admins", desc="Lists bot admin accounts")
public class Admins extends GenericCommand
{
	
	public Admins(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
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
}
