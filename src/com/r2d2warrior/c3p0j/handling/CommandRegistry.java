package com.r2d2warrior.c3p0j.handling;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

import org.pircbotx.PircBotX;
import org.reflections.Reflections;

import com.r2d2warrior.c3p0j.commands.Command;
import com.r2d2warrior.c3p0j.commands.Commands;
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
		try
		{
			parseAnnotations();
		}
		catch (ReflectiveOperationException e)
		{
			e.printStackTrace();
		}
	}
	
	private void parseAnnotations() throws ReflectiveOperationException
	{
		Reflections reflections = new Reflections(Utils.getPackageName(GenericCommand.class));
		Set<Class<? extends GenericCommand>> commandClasses = reflections.getSubTypesOf(GenericCommand.class);
		
		for (Class<? extends GenericCommand> cls : commandClasses)
		{
			if (cls.isAnnotationPresent(Command.class))
			{
				Command cmd = cls.getAnnotation(Command.class);
				Reflections classRefs = new Reflections(cls);
				
				HashMap<String, Method> methodMap = new HashMap<>();
				if (classRefs.getMethodsAnnotatedWith(Command.Sub.class).size() > 0)
					methodMap = processMethods(cls);

				commands.add(new CommandInfo<T>(
						cmd.name(), cmd.alt(), cmd.desc(),
						cmd.syntax(), cmd.adminOnly(), cmd.requiresArgs(),
						cls.getDeclaredMethod(cmd.method()), methodMap, cls)
						);
			}
			else if (cls.isAnnotationPresent(Commands.class))
			{
				for (Command cmd : cls.getAnnotation(Commands.class).value())
					commands.add(new CommandInfo<T>(
							cmd.name(), cmd.alt(), cmd.desc(),
							cmd.syntax(), cmd.adminOnly(), cmd.requiresArgs(),
							cls.getDeclaredMethod(cmd.method()),
							new HashMap<String, Method>(), cls)
							);
			}
		}
	}
	
	private HashMap<String, Method> processMethods(Class<? extends GenericCommand> cls)
	{
		Reflections classRefs = new Reflections(cls);
		HashMap<String, Method> map = new HashMap<>();
		for (Method method : classRefs.getMethodsAnnotatedWith(Command.Sub.class))
		{
			String name = method.getAnnotation(Command.Sub.class).name();
			map.put(name.toLowerCase(), method);
		}
		return map;
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
		CommandInfo<T> info = getCommandInfo(event.getCommandName());
		
		if (info.isAdminOnly() && !event.getUser().isAdmin())
			return noPermissionError;
		
		if (info.requiresArgs() && event.hasNoArgs())
			return needsArgsError + info.getSyntax();
		
		try
		{
			Constructor<T> constuct = cls.getConstructor(CommandEvent.class);
			constuct.setAccessible(true);
			Method method = info.getExecuteMethod();
			method.setAccessible(true);
			method.invoke(constuct.newInstance(event));
		}
		//TODO Throw all exceptions during command execution to execute() to be caught here
		catch (ReflectiveOperationException e)
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
	
	public CommandInfo<T> getCommandInfo(String name)
	{
		for (CommandInfo<T> info : commands)
			if (info.getName().equals(name) || (info.hasAlt() && info.getAlt().equals(name)))
				return info;
		
		return null;	
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
