package com.r2d2warrior.c3p0j.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command
{
	String name();
	String desc() default "";
	boolean adminOnly() default false;
}
