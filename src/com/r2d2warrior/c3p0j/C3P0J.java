package com.r2d2warrior.c3p0j;

import org.pircbotx.PircBotX;
import org.pircbotx.Configuration;

import com.r2d2warrior.c3p0j.command.CommandListener;

@SuppressWarnings({"unchecked", "rawtypes"})
public class C3P0J
{

	public static void main(String[] args) throws Exception
	{

 		Configuration config = new Configuration.Builder()
			.setName("C3P0J")
			.setLogin("R2D2")
			.setRealName("C3P0J (PircBotX) - by R2D2Warrior")
			
			.setAutoNickChange(true)
			.setCapEnabled(true)
			
			.addListener(new CommandListener())
			
			.addAdminAccount("R2D2Warrior")
			.addAdminAccount("CHCMATT")
			.addPrefix(".",  "MESSAGE")
			.addPrefix("@", "NOTICE")
			
			.setServerHostname("irc.esper.net")
			.addAutoJoinChannel("#C3P0")
			//.setNickservPassword(new Scanner(new File("C:\\data.data")).nextLine())
			
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