package com.r2d2warrior.c3p0j;

import org.pircbotx.PircBotX;
import org.pircbotx.Configuration;

import com.r2d2warrior.c3p0j.handling.CommandListener;

public class C3P0J
{

	public static void main(String[] args) throws Exception
	{

 		Configuration<PircBotX> config = new Configuration.Builder<PircBotX>()
 				
 				//Login info
			.setName("C3P0J")
			.setLogin("R2D2")
			.setRealName("C3P0J (PircBotX) - by R2D2Warrior")
			
				//Booleans
			.setAutoNickChange(true)
			.setCapEnabled(true)
			
				//Listeners
			.addListener(new CommandListener())
			
				//Command management
			.addAdminAccount("R2D2Warrior")
			.addAdminAccount("CHCMATT")
			.addPrefix(".",  "MESSAGE")
			.addPrefix("@", "NOTICE")
			
				//Server info
			.setServerHostname("irc.esper.net")
			.setChannelPrefixes("#")
			
			.addAutoJoinChannel("#C3P0")
			
			.buildConfiguration();
 		
        PircBotX bot = new PircBotX(config);
                
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