package com.r2d2warrior.c3p0j.handling;

import com.r2d2warrior.c3p0j.commands.GenericCommand;

import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class CommandInfo<T extends GenericCommand>
{
	private String name;
	private String desc;
	private String syntax;
	private boolean isAdminOnly;
	@Getter(AccessLevel.NONE)
	private boolean requiresArgs;
	private Class<T> commandClass;
	
	@SuppressWarnings("unchecked")
	public CommandInfo(String name, String desc, String syntax, boolean adminOnly, boolean requiresArgs, Class<?> commandClass)
	{
		this.name = name;
		this.desc = desc;
		this.syntax = syntax;
		this.isAdminOnly = adminOnly;
		this.requiresArgs = requiresArgs;
		this.commandClass = (Class<T>)commandClass;
	}
	
	public boolean requiresArgs()
	{
		return requiresArgs;
	}
}
