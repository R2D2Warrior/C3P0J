package com.r2d2warrior.c3p0j.commands;

import java.util.Map;

import org.pircbotx.PircBotX;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.utils.Utils;
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
		
		// TODO Change to use Liam's ip lookup
		String arg = event.getArgumentsList().get(0);
		Map<String, String> results;
		
		if (arg.contains("."))
			results = WebUtils.getLocationData(arg);
		else
			results = WebUtils.getLocationData(userChannelDao.getUser(arg).getHostmask());
		
		String response;
		if (results.get("statusCode").equals("OK"))
		{
			response = String.format("Location of %s: %s, %s (%s)",
					results.get("ipAddress"), Utils.toTitleCase(results.get("cityName")), 
					Utils.toTitleCase(results.get("regionName")), results.get("countryCode"));
		}
		else
		{
			response = "Error: " + results.get("statusMessage");
		}
		
		event.respond(response);
	}
}
