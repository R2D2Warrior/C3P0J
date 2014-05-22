package com.r2d2warrior.c3p0j.handling;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Getter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;
import org.reflections.Reflections;

import com.r2d2warrior.c3p0j.commands.Command;
import com.r2d2warrior.c3p0j.commands.GenericCommand;
import com.r2d2warrior.c3p0j.utils.Permissions;
import com.r2d2warrior.c3p0j.utils.Permissions.Group;
import com.r2d2warrior.c3p0j.utils.Utils;
import com.sun.xml.internal.txw2.IllegalAnnotationException;

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
		
		for (Class<? extends GenericCommand> cls : reflections.getSubTypesOf(GenericCommand.class))
		{
			if (cls.isAnnotationPresent(Command.class))
			{
				Command cmd = cls.getAnnotation(Command.class);
				
				HashMap<String, Method> methodMap = new HashMap<>();
				methodMap = processMethods(cls);

				commands.add(new CommandInfo<T>(
						cmd.name(), cmd.alias(), cmd.desc(), cmd.syntax(),
						cmd.minGroup(), cmd.requiresArgs(), methodMap, cls)
						);
			}
		}
	}
	
	private HashMap<String, Method> processMethods(Class<? extends GenericCommand> cls) throws ReflectiveOperationException
	{
		HashMap<String, Method> map = new HashMap<>();
		for (Method method : cls.getMethods())
		{
			if (method.isAnnotationPresent(Command.Sub.class))
			{
				String name = method.getAnnotation(Command.Sub.class).name();
				map.put(name.toLowerCase(), method);
			}
			if (method.isAnnotationPresent(Command.Default.class))
			{
				if (map.containsKey("DEFAULT"))
					throw new IllegalAnnotationException("Only one @Command.Default annotation is allowed per class.\n"
														+ "Using first method annotated with @Command.Default.");
				else
					map.put("DEFAULT", method);
			}
		}
		if (!map.containsKey("DEFAULT"))
		{
			try
			{
				map.put("DEFAULT", cls.getMethod("execute"));
			}
			catch (NoSuchMethodException e)
			{
				throw new IllegalArgumentException("Command class " + cls.getCanonicalName() + " has no default method.\nSpecify an execute() method or use @Command.Default.");
			}
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
		
		Group minGroup = event.getBot().getPermissions().getGroup(info.getMinGroup());
		if (minGroup.getRank() < event.getUser().getGroup().getRank())
			return noPermissionError;
		
		if (info.requiresArgs() && event.hasNoArgs())
			return needsArgsError + info.getSyntax();
		
		Method method = info.getMethods().get("DEFAULT");
		if (info.hasSubCommands() && !event.hasNoArgs())
		{
			String possibleSub = event.getArgumentList().get(0);
			if (info.hasSub(possibleSub))
			{
				CommandInfo<T>.Sub sub = info.getSub(possibleSub);
				event.setArguments(event.getArgRange(1));
				method = info.getMethods().get(sub.getName());
				
				Group minGroupSub = event.getBot().getPermissions().getGroup(sub.getMinGroup());
				if (minGroupSub.getRank() < event.getUser().getGroup().getRank())
					return noPermissionError;
				if (sub.requiresArgs() && event.hasNoArgs())
					return needsArgsError.replace("command", "subcommand") + info.getSyntax();
			}
		}
			
		try
		{
			Constructor<T> constuct = cls.getConstructor(CommandEvent.class);
			constuct.setAccessible(true);
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
	
	public HashMap<String, List<String>> getCommandsMinGroup(String minGroupName)
	{
		HashMap<String, List<String>> commands = new HashMap<>();
		for (CommandInfo<T> info : this.commands)
		{
			if (!info.hasSubCommands())
			{
				if (info.getMinGroup().equalsIgnoreCase(minGroupName))
					commands.put(info.getName(), null);
			}
			else
			{
				List<String> subNames = getSubCommandsMinGroup(minGroupName, info.getName());
				if (subNames.size() > 0)
					commands.put(info.getName(), subNames);
			}
		}
		return commands;
	}
	
	private List<String> getSubCommandsMinGroup(String minGroupName, String commandName)
	{
		List<String> subNames = new ArrayList<>();
		CommandInfo<T> info = getCommandInfo(commandName.toLowerCase());
		for (CommandInfo<T>.Sub sub : info.getSubs().values())
			if (sub.getMinGroup().equalsIgnoreCase(minGroupName))
				subNames.add(sub.getName());
		return subNames;
	}
	
	public List<String> getAllCommandsForGroup(String groupName)
	{
		Permissions.Group group = bot.getPermissions().getGroup(groupName);
		HashMap<String, List<String>> commands = getCommandsMinGroup(groupName);
		for (Permissions.Group curGroup : bot.getPermissions().getGroups())
		{
			if (curGroup.getRank() > group.getRank())
			{
				HashMap<String, List<String>> curGroupCommands = getCommandsMinGroup(curGroup.getName());
				for (Entry<String, List<String>> entry : curGroupCommands.entrySet())
				{
					if (commands.containsKey(entry.getKey()))
						commands.get(entry.getKey()).addAll(entry.getValue());
					else
						commands.put(entry.getKey(), entry.getValue());
				}
			}
		}
		
		List<String> finalList = new ArrayList<>();
		for (Entry<String, List<String>> entry : commands.entrySet())
		{
			if (entry.getValue() != null)
				finalList.add(entry.getKey() + " (" + StringUtils.join(entry.getValue(), "/") + ")");
			else
				finalList.add(entry.getKey());
		}
		Collections.sort(finalList);
		return finalList;
	}
	
	public Class<T> getCommandClass(String name)
	{
		for (CommandInfo<T> info : commands)
			if (info.getName().equals(name) || (ArrayUtils.contains(info.getAliases(), name)))
				return info.getCommandClass();
		
		return null;
	}
	
	public String getCommandName(Class<? extends GenericCommand> cls)
	{
		for (CommandInfo<T> info : commands)
			if (info.getCommandClass().equals(cls))
				return info.getName();
		
		return null;
	}
	
	public CommandInfo<T> getCommandInfo(String name)
	{
		for (CommandInfo<T> info : commands)
			if (info.getName().equals(name) || (ArrayUtils.contains(info.getAliases(), name)))
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
