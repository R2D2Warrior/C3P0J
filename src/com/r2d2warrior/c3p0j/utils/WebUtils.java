package com.r2d2warrior.c3p0j.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

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
	
	public static Map<String, String> getLocationData(String ip)
	{
		String key = Utils.getConfigMap("config.json").get("apiKeys").get("geoip");
		String address = String.format("http://api.ipinfodb.com/v3/ip-city/?key=%s&ip=%s&format=json", key, ip);
		try
		{
			HttpURLConnection conn = (HttpURLConnection) new URL(address).openConnection();
			Scanner in = new Scanner(conn.getInputStream());
			
			String json = "";
			while (in.hasNext())
				json += in.nextLine();
			
			JSONParser parser = new JSONParser();
			@SuppressWarnings("unchecked")
			Map<String, String> data = (Map<String, String>)parser.parse(json);
			in.close();
			return data;
		}
		catch (IOException | ParseException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getRandomFML()
	{
		String address = "http://www.fmylife.com/random";
		
		try
		{
			Element element = Jsoup.connect(address).get().select("li[id]").first().select("p").first();
			String fml = StringEscapeUtils.unescapeHtml4(element.html()) + ".";
			return fml;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
