package com.r2d2warrior.c3p0j.commands;

import org.pircbotx.PircBotX;

import bsh.EvalError;
import bsh.Interpreter;

import com.r2d2warrior.c3p0j.handling.CommandEvent;
import com.r2d2warrior.c3p0j.utils.Utils;

@Command(name="eval", desc="Evaluate a method within PircBotX", syntax="eval <code>", requiresArgs=true, minGroup="admin")
public class Eval extends GenericCommand
{
	
	public Eval(CommandEvent<PircBotX> event)
	{
		super(event);
	}
	
	public void execute()
	{
		String eval = event.getArguments();
			
		String result = "";
		Interpreter i = Utils.createDefaultInterpreter(event);

		try
		{	
			i.eval("item = " + eval);
			result = i.get("item").toString();
		}
		catch (EvalError e)
		{
			event.respondToUser("Error occurred during evaluation.");
			e.printStackTrace();
		}
		
		event.respond(result);
	}
}
