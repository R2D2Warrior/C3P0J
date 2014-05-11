package com.r2d2warrior.c3p0j;

import org.pircbotx.PircBotX;
import org.pircbotx.Configuration;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.Listener;
import org.reflections.Reflections;

import com.r2d2warrior.c3p0j.listeners.AddListener;
import com.r2d2warrior.c3p0j.utils.Config;
import com.r2d2warrior.c3p0j.utils.Utils;

public class C3P0J
{

	public static void main(String[] args)
	{
		// TODO Improve config file
		Config c = new Config("config.json");
		
 		Configuration.Builder<PircBotX> builder =
 				new Configuration.Builder<PircBotX>(c.buildBotConfiguration())
 				
 				.addCapHandler(new EnableCapHandler("extended-join", true))
 				.addCapHandler(new EnableCapHandler("account-notify", true));




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