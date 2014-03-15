package com.r2d2warrior.c3p0j.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command
{
	/** The name of the command*/
	String name();
	/** An alternate name for the command*/
	String alt() default "";
	/** The command's description*/
	String desc() default "";
	/** Required: {@code<arg>}, Optional: [arg]*/
	String syntax() default "";
	/** If the command requires any arguments or not. Default <code>false</code>*/
	boolean requiresArgs() default false;
	/** If the command requires admin status to use. Default <code>false</code>*/
	boolean adminOnly() default false;
}
