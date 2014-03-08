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
import com.r2d2warrior.c3p0j.utils.Utils;

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
	
	private void parseAnnotations()
	{
		Reflections reflections = new Reflections(Utils.getPackageName(Command.class));
		Command cmd;
		for (Class<?> cls : reflections.getTypesAnnotatedWith(Command.class))
		{
			cmd = cls.getAnnotation(Command.class);
			commands.add(new CommandInfo<T>(cmd.name(), cmd.alt(), cmd.desc(),
					cmd.syntax(), cmd.adminOnly(), cmd.requiresArgs(), cls));
		}
	}
	
	//TODO Figure out how to refresh command changes. This method only works for adding/removing them
	public void refresh()
	{
		commands = new HashSet<>();
		parseAnnotations();
	}
	
	public String executeCommand(CommandEvent<PircBotX> event)
	{
		String noPermissionError = "You don't have permission to use that command.";
		//String doesntExistError = "Command does not exist: " + event.getCommandName();
		String commandError = "Error while executing command: " + event.getCommandName();
		String needsArgsError = "Error: This command needs arguments. SYNTAX: ";
		
		if (!isCommand(event.getCommandName()))
			return "";
		
		Class<T> cls = getCommandClass(event.getCommandName());
		CommandInfo<T> info = getCommandInfo(cls);
		
		if (info.isAdminOnly() && !event.getUser().isAdmin())
			return noPermissionError;
		
		if (info.requiresArgs() && event.hasNoArgs())
			return needsArgsError + info.getSyntax();
		
		try
		{	
			@SuppressWarnings("unchecked")
			Constructor<T> constuct = (Constructor<T>) cls.getConstructors()[0];
			constuct.setAccessible(true);

			constuct.newInstance(event).execute();
		}
		//TODO Throw all exceptions during command execution to execute() to be caught here
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			e.printStackTrace();
			return commandError;
		}
		return "";
	}
	
	public Class<T> getCommandClass(String name)
	{
		for (CommandInfo<T> info : commands)
			if (info.getName().equals(name) || (info.hasAlt() && info.getAlt().equals(name)))
				return info.getCommandClass();
		
		return null;
	}
	
	public String getCommandName(Class<T> cls)
	{
		for (CommandInfo<T> info : commands)
			if (info.getCommandClass().equals(cls))
				return info.getName();
		
		return null;
	}
	
	public CommandInfo<T> getCommandInfo(Class<T> cls)
	{
		for (CommandInfo<T> info : commands)
			if (info.getCommandClass().equals(cls))
				return info;
		
		return null;
	}
	
	public CommandInfo<T> getCommandInfo(String name)
	{
		return getCommandInfo(getCommandClass(name));
	}
	
	public CommandInfo<T> getCommandInfo(CommandEvent<PircBotX> event)
	{
		return getCommandInfo(event.getCommandName());
	}

	public boolean isCommand(String name)
	{
		return getCommandClass(name) != null;
	}
}
