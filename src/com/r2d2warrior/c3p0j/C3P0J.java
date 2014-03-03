package com.r2d2warrior.c3p0j;

import org.pircbotx.PircBotX;
import org.pircbotx.Configuration;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.Listener;
import org.reflections.Reflections;

import com.r2d2warrior.c3p0j.listeners.AddListener;
import com.r2d2warrior.c3p0j.utils.Config;

public class C3P0J
{

	public static void main(String[] args)
	{
		// TODO Improve config file
		Config c = new Config("config.json");
		String password = c.getPassword("nickserv");
		
 		Configuration.Builder<PircBotX> builder = new Configuration.Builder<PircBotX>()
 				
 				//Login info
			.setName("C3P0J")
			.setLogin("R2D2")
			.setRealName("C3P0J (PircBotX) - by R2D2Warrior")
			
				//Booleans
			.setAutoNickChange(true)
			.setCapEnabled(true)
			
			.addCapHandler(new EnableCapHandler("extended-join", true))
			.addCapHandler(new EnableCapHandler("account-notify", true))
			

				//Command management
			.addAdminAccounts("R2D2Warrior", "CHCMATT", "Vgr255", "ChasedSpade")
			.addPrefix(".", "MESSAGE")
			.addPrefix("@", "NOTICE")
			.setFactoidPrefix("?")
			
				//Server info
			.setServerHostname("irc.esper.net")
			.setChannelPrefixes("#")
			
			.addAutoJoinChannel("#C3P0")
			
			.addBlockedChannels("#help", "#lobby")
			
			.setNickservPassword(password);
			

 		try
 		{
			Reflections reflections = new Reflections("com.r2d2warrior.c3p0j.listeners");
			for (Class<?> cls : reflections.getTypesAnnotatedWith(AddListener.class))
			{
				@SuppressWarnings("unchecked")
				Listener<PircBotX> listener = (Listener<PircBotX>) cls.newInstance();
				builder.addListener(listener);
			}
 		}
 		catch (IllegalAccessException | InstantiationException e)
 		{
 			e.printStackTrace();
 		}


        PircBotX bot = new PircBotX(builder.buildConfiguration());
                
        try
        {
        	bot.startBot();
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        }
	}
}