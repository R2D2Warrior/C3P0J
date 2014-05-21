package com.r2d2warrior.c3p0j;

import lombok.Getter;

import org.pircbotx.PircBotX;
import com.r2d2warrior.c3p0j.commands.GenericCommand;
import com.r2d2warrior.c3p0j.core.Configuration;
import com.r2d2warrior.c3p0j.handling.CommandRegistry;
import com.r2d2warrior.c3p0j.handling.FactoidManager;
import com.r2d2warrior.c3p0j.utils.ConfigFile;

public class C3P0J extends PircBotX
{
	@Getter
	protected final CommandRegistry<GenericCommand> commandRegistry;
	@Getter
	protected final FactoidManager factoidManager;
	protected final Configuration<C3P0J> configuration;
	
	@SuppressWarnings("unchecked")
	public C3P0J(Configuration<? extends C3P0J> configuration)
	{
		super(configuration);
		this.configuration = (Configuration<C3P0J>) configuration;
		this.commandRegistry = new CommandRegistry<>(this);
		this.factoidManager = new FactoidManager();
	}

	public static void main(String[] args)
	{
		ConfigFile c = new ConfigFile("config.json");
		
 		Configuration.Builder<C3P0J> builder =
 				new Configuration.Builder<C3P0J>(c.buildBotConfiguration());

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