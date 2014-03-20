package com.r2d2warrior.c3p0j.commands;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.ParseException;
import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.utils.WebUtils;

@Command(name="geoip", desc="Get location information from an IP address", syntax="geoip <ip>", requiresArgs=true)
public class GeoIP extends GenericCommand
{
	public GeoIP(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{	
		
		String arg = event.getArgumentList().get(0);
		String ip = (arg.contains(".") || StringUtils.isNumeric(arg)) ? arg : userChannelDao.getUser(arg).getHostmask();
		
		try
		{
			Map<String, String> results = WebUtils.getLocationData(ip);
			if (results.get("country_name").equals("Reserved"))
				event.respondToUser("Error: That IP is Reserved");
			else
			{
				event.respond(String.format("Location of %s: [City, State, Country]: %s, %s (%s) [Lat/Long]: %s/%s",
						results.get("ip"), results.get("city"), 
						results.get("region_name"), results.get("country_code"),
						results.get("latitude"), results.get("longitude")));
			}
		}
		catch (IOException e)
		{
			event.respondToUser("Error while connecting to URL: " + e.getMessage().split("URL: ")[1]);
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			event.respondToUser("Error while parsing results.");
			e.printStackTrace();
		}
	}
}
