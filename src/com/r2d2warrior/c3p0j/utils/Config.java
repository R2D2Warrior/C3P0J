package com.r2d2warrior.c3p0j.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.reflections.Reflections;

import com.cedarsoftware.util.io.JsonWriter;
import com.r2d2warrior.c3p0j.listeners.AddListener;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Config
{
	@Getter
	private Map<String, Map<String, Object>> map;
	private String fileName;
	
	public Config(String fileName)
	{
		this.fileName = fileName;
		this.map = getFileMap();
	}
	
	public void update(PircBotX bot)
	{
		updateFileMap(map);
		updateBotConfiguration(bot);
	}
	
	public void updateFile()
	{
		updateFileMap(map);
	}
	
	public String getString(String superKey, String subKey)
	{
		return map.get(superKey).get(subKey).toString();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getStringList(String superKey, String subKey)
	{
		return (List<String>) map.get(superKey).get(subKey);
	}
	
	public Configuration<PircBotX> buildBotConfiguration() 
	{
		Configuration.Builder<PircBotX> builder = new Configuration.Builder<>();
		
		for (String superKey : map.keySet())
		{
			for (String key : map.get(superKey).keySet())
			{
				try
				{
					Field f = builder.getClass().getDeclaredField(key);
					String setterName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
					
					Object o = map.get(superKey).get(key);
					Class<?> type = f.getType();
					
					if (key.equals("autoJoinChannels")) // Special case
					{
						@SuppressWarnings("unchecked")
						List<String> channels = (List<String>) o;
						for (String c : channels)
							builder.addAutoJoinChannel(c);
						continue;
					}
					
					Method setter = builder.getClass().getMethod(setterName, type);
					setter.setAccessible(true);
					
					if (type.equals(String.class))
						setter.invoke(builder, o.toString());
					else if (type.equals(boolean.class))
						setter.invoke(builder, (boolean)o);
					else if (type.equals(List.class))
						setter.invoke(builder, (List<?>)o);
					else if (type.equals(Map.class))
						setter.invoke(builder, (Map<?, ?>)o);
					
					
				}
				catch (NoSuchFieldException e)
				{
					continue;
				}
				catch (ReflectiveOperationException e)
				{
					e.printStackTrace();
					continue;
				}
			}
		}
		addListeners(builder);
		return builder.buildConfiguration();
	}
	
	public void updateBotConfiguration(PircBotX bot)
	{
		updateFileMap(map);
		try
		{
			Field confField = bot.getClass().getDeclaredField("configuration");
			confField.setAccessible(true);
			confField.set(bot, buildBotConfiguration());
		}
		catch (ReflectiveOperationException e)
		{
			e.printStackTrace();
		}
	}
	
	public void addListeners(Configuration.Builder<PircBotX> builder)
	{
 		try
 		{
			Reflections reflections = new Reflections(Utils.getPackageName(AddListener.class));
			for (Class<?> cls : reflections.getTypesAnnotatedWith(AddListener.class))
			{
				if (cls.getAnnotation(AddListener.class).value())
				{
					// TODO Figure out how to avoid this warning: "Unchecked cast from capture#1-of ? to Listener<PircBotX>"
					@SuppressWarnings("unchecked")
					Listener<PircBotX> listener = (Listener<PircBotX>) cls.newInstance();
					builder.addListener(listener);
				}
			}
 		}
 		catch (ReflectiveOperationException e)
 		{
 			e.printStackTrace();
 		}
	}
	
	private Map<String, Map<String, Object>> getFileMap()
	{
		try
		{
			@SuppressWarnings("unchecked")
			Map<String, Map<String, Object>> config = 
				(Map<String, Map<String, Object>>) new JSONParser().parse(new FileReader(fileName));
			
			return config;
		}
		catch (ParseException | IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private void updateFileMap(Map<String, Map<String, Object>> newMap)
	{
		
		try
		{
			FileWriter writer = new FileWriter(fileName);
			String json = JsonWriter.formatJson(JSONObject.toJSONString(newMap));
			writer.write(json);
			writer.flush();
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
