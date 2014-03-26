package com.r2d2warrior.c3p0j.handling;

import java.util.HashMap;
import com.r2d2warrior.c3p0j.utils.Config;

public class FactoidManager
{
	private static Config config = new Config("config.json");
	
	static
	{
		// Check to make sure the "factoids" map exists in the config
		if (!config.getMap().keySet().contains("factoids"))
			// If it doesn't exist, create it
			config.getMap().put("factoids", new HashMap<String, String>());
	}
	
	public static void addFactoid(String name, String data)
	{
		config.getMap().get("factoids").put(name, data);
		config.update();
	}
	
	public static void removeFactoid(String name)
	{
		config.getMap().get("factoids").remove(name);
		config.update();	
	}
	
	public static boolean factoidExists(String name)
	{
		return config.getMap().get("factoids").keySet().contains(name);
	}
	
	public static String getFactoidData(String name)
	{
		return config.getMap().get("factoids").get(name);
	}
}
