package com.r2d2warrior.c3p0j.handling;

import com.r2d2warrior.c3p0j.commands.GenericCommand;

import lombok.Getter;

@Getter
public class CommandInfo<T extends GenericCommand>
{
	private String name;
	private String desc;
	private boolean isAdminOnly;
	private Class<T> commandClass;
	
	@SuppressWarnings("unchecked")
	public CommandInfo(String name, String desc, boolean adminOnly, Class<?> commandClass)
	{
		this.name = name;
		this.desc = desc;
		this.isAdminOnly = adminOnly;
		this.commandClass = (Class<T>)commandClass;
	}
}
