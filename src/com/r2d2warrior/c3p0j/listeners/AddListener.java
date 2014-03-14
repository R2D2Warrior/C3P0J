package com.r2d2warrior.c3p0j.listeners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AddListener
{
	/**
	 * Set to false to disable this listener
	 */
	boolean value() default true;
}
