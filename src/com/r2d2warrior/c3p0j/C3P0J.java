package com.r2d2warrior.c3p0j;

import lombok.Getter;

import org.pircbotx.PircBotX;
import org.pircbotx.Configuration;
import org.pircbotx.cap.EnableCapHandler;

import com.r2d2warrior.c3p0j.commands.GenericCommand;
import com.r2d2warrior.c3p0j.handling.CommandRegistry;
import com.r2d2warrior.c3p0j.handling.FactoidManager;
import com.r2d2warrior.c3p0j.utils.ConfigFile;

public class C3P0J extends PircBotX
{
	@Getter
	protected final CommandRegistry<GenericCommand> commandRegistry;
	@Getter
	protected final FactoidManager factoidManager;
	
	public C3P0J(Configuration<? extends PircBotX> configuration)
	{
		super(configuration);
		this.commandRegistry = new CommandRegistry<>(this);
		this.factoidManager = new FactoidManager();
	}

	public static void main(String[] args)
	{
		ConfigFile c = new ConfigFile("config.json");
		
 		Configuration.Builder<PircBotX> builder =
 				new Configuration.Builder<PircBotX>(c.buildBotConfiguration())
 				
 				.addCapHandler(new EnableCapHandler("extended-join", true))
 				.addCapHandler(new EnableCapHandler("account-notify", true));

        PircBotX bot = new PircBotX(builder.buildConfiguration());
                
        try
        {
        	bot.startBot();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
	}
}