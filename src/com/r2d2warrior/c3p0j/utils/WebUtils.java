package com.r2d2warrior.c3p0j.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WebUtils
{
	public static List<Map<String, String>> getUrbanDictionDefinitions(String searchTerm)
	{
		try
		{
			searchTerm = searchTerm.replace(' ', '+');
			HttpURLConnection conn = (HttpURLConnection) new URL("http://api.urbandictionary.com/v0/define?term=" + searchTerm).openConnection();
			
			Scanner in = new Scanner(conn.getInputStream());
			
			String json = "";
			while (in.hasNext())
				json += in.nextLine() + "\n";
			
			JSONParser parser = new JSONParser();
			Map<?, ?> jMap = ((Map<?, ?>)parser.parse(json));
			
			@SuppressWarnings("unchecked")
			List<Map<String, String>> definitionMapList = (List<Map<String, String>>)jMap.get("list");
			
			in.close();
			return definitionMapList;
		}
		catch (IOException | ParseException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
