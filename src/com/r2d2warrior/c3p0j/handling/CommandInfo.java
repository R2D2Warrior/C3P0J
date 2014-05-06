package com.r2d2warrior.c3p0j.handling;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

import com.r2d2warrior.c3p0j.commands.GenericCommand;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

/**
 * Class to hold all information from a single <code>@Command</code> annotation
 */
@ToString
@Getter
public class CommandInfo<T extends GenericCommand>
{
	private String name;
	private String alt;
	private String desc;
	private String syntax;
	private boolean isAdminOnly;
	@Getter(AccessLevel.NONE)
	private boolean requiresArgs;
	private Method executeMethod;
	private Class<T> commandClass;
	
	@SuppressWarnings("unchecked")
	public CommandInfo(String name, String alt, String desc, String syntax,
			boolean adminOnly, boolean requiresArgs, Method executeMethod, Class<?> commandClass)
	{
		this.name = name;
		this.alt = alt;
		this.desc = desc;
		this.syntax = syntax;
		this.isAdminOnly = adminOnly;
		this.requiresArgs = requiresArgs;
		this.executeMethod = executeMethod;
		this.commandClass = (Class<T>)commandClass;
	}
	
	public boolean requiresArgs()
	{
		return requiresArgs;
	}
	
	public boolean hasAlt()
	{
		return StringUtils.isNotBlank(alt);
	}
}
