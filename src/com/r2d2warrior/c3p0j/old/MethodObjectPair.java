package com.r2d2warrior.c3p0j.old;

import java.lang.reflect.Method;

/**
 * 
 * @author Callum Errington https://bitbucket.org/kingrunes
 *
 */
public class MethodObjectPair
{
	private Object object;
	private Method method;
	
	public MethodObjectPair(Object object, Method method)
	{
		this.object = object;
		this.method = method;
	}

	public Object getObject()
	{
		return object;
	}

	public void setObject(Object object)
	{
		this.object = object;
	}

	public Method getMethod()
	{
		return method;
	}

	public void setMethod(Method method)
	{
		this.method = method;
	}
}