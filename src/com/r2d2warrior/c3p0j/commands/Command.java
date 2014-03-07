package com.r2d2warrior.c3p0j.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command
{
	String name(); // TODO Command aliases
	String alt() default "";
	String desc() default "";
	/** Required: {@code<arg>}, Optional: [arg]*/
	String syntax() default "";
	boolean requiresArgs() default false;
	boolean adminOnly() default false;
}
