package com.r2d2warrior.c3p0j.old;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;

import lombok.Getter;

import org.pircbotx.hooks.events.MessageEvent;

import com.google.common.collect.Maps;
import com.r2d2warrior.c3p0j.command.CommandInfo;

public class NewCommandHandler
{
	@Getter
	private HashMap<CommandInfo, MethodObjectPair> commandMap;
	
	public NewCommandHandler()
	{
		this.commandMap = Maps.newHashMap();
	}
	
	public void parse(Object object)
	{
		Class<?> cls = object.getClass();
		for (Method method : cls.getMethods())
		{
			if (method.isAnnotationPresent(MessageCommand.class))
			{
				MessageCommand msgCmd = method.getAnnotation(MessageCommand.class);
/*				commandMap.put(
						new CommandInfo(msgCmd.name(), msgCmd.info(), msgCmd.adminOnly()),
						new MethodObjectPair(object, method)
						);*/
			}
		}
	}
	
	public void executeCommand(MessageEvent<?> event) throws NullPointerException
	{
		String commandName = event.getMessage().split(" ", 2)[0];
			MethodObjectPair pair = getEntry(commandName).getValue();
			try
			{
				pair.getMethod().invoke(pair.getObject(), event, event.getMessage().split(" ", 2)[1].split(" "));
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				e.printStackTrace();
			}
	}
	
	public Entry<CommandInfo, MethodObjectPair> getEntry(String commandName)
	{
		for (Entry<CommandInfo, MethodObjectPair> entry : commandMap.entrySet())
			if (entry.getKey().getName().equals(commandName))
				return entry;
		return null;
	}
}
