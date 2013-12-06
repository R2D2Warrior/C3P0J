package com.r2d2warrior.c3p0j.old;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageCommand
{
	String name();
	String info() default "";
	boolean adminOnly() default false;
}
