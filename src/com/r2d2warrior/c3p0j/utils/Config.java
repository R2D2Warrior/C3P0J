package com.r2d2warrior.c3p0j.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import lombok.Getter;

public class Config
{
	@Getter
	private Map<String, Map<String, String>> map;
	private String fileName;
	
	public Config(String fileName)
	{
		this.fileName = fileName;
		this.map = getFileMap();
	}

	public void update()
	{
		updateFileMap(map);
	}
	
	private Map<String, Map<String, String>> getFileMap()
	{
		try
		{
			
			@SuppressWarnings("unchecked")
			Map<String, Map<String, String>> config =
			(Map<String, Map<String, String>>) new JSONParser().parse(new FileReader(fileName));
			
			return config;
		}
		catch (ParseException | IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private void updateFileMap(Map<String, Map<String, String>> newMap)
	{
		JSONObject obj = new JSONObject(newMap);
		
		try
		{
			FileWriter writer = new FileWriter(fileName);
			writer.write(obj.toJSONString());
			writer.flush();
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
