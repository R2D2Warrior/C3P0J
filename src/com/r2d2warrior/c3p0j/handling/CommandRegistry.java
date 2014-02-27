package com.r2d2warrior.c3p0j.handling;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

import org.pircbotx.PircBotX;
import org.reflections.Reflections;

import com.r2d2warrior.c3p0j.commands.Command;
import com.r2d2warrior.c3p0j.commands.GenericCommand;

public class CommandRegistry<T extends GenericCommand>
{
	protected PircBotX bot;
	@Getter
	protected Set<CommandInfo<T>> commands;
	
	public CommandRegistry(PircBotX bot)
	{
		this.bot = bot;
		this.commands = new HashSet<CommandInfo<T>>();
		parseAnnotations();
	}
	
	public void parseAnnotations()
	{
		Reflections reflections = new Reflections("com.r2d2warrior.c3p0j");
		Command cmd;
		for (Class<?> cls : reflections.getTypesAnnotatedWith(Command.class))
		{
			cmd = cls.getAnnotation(Command.class);
			commands.add(
					new CommandInfo<T>(cmd.name(), cmd.desc(), cmd.syntax(), cmd.adminOnly(), cls)
					);
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean executeCommand(CommandEvent<PircBotX> event)
	{		
		try
		{
			if (!isCommand(event.getCommandName()))
				return true;
			Class<T> cls = getCommandClass(event.getCommandName());
			CommandInfo<T> info = getCommandInfo(cls);
	
			if (info.isAdminOnly() && !event.getUser().isAdmin())
				return true;
			
			Constructor<T> constuct = (Constructor<T>) cls.getConstructors()[0];
			constuct.setAccessible(true);

			constuct.newInstance(event).execute();
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	public Class<T> getCommandClass(String name)
	{
		for (CommandInfo<T> info : commands)
			if (info.getName().equals(name))
				return info.getCommandClass();
		
		return null;
	}
	
	public CommandInfo<T> getCommandInfo(String name)
	{
		for (CommandInfo<T> info : commands)
			if (info.getName().equals(name))
				return info;
		
		return null;
	}
	
	public CommandInfo<T> getCommandInfo(Class<T> cls)
	{
		for (CommandInfo<T> info : commands)
			if (info.getCommandClass().equals(cls))
				return info;
		
		return null;
	}
	
	public CommandInfo<T> getCommandInfo(CommandEvent<PircBotX> event)
	{
		return getCommandInfo(event.getCommandName());
	}
	
	public String getCommandName(Class<T> cls)
	{
		for (CommandInfo<T> info : commands)
			if (info.getCommandClass().equals(cls))
				return info.getName();
		
		return null;
	}
	
	public boolean isCommand(String name)
	{
		return getCommandClass(name) != null;
	}
}
