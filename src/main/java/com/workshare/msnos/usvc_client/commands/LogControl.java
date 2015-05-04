package com.workshare.msnos.usvc_client.commands;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

import com.workshare.msnos.usvc_client.Command;
import com.workshare.msnos.usvc_client.ui.Console;

public class LogControl implements Command {

    private final String name;

    public LogControl(String name) {
        this.name = name;
        setCurrentLevel(Level.INFO);
    }
    
	@Override
	public String description() {
		return "Change "+name+" log level";
	}

	@Override
	public void execute() throws Exception {
		Console.out.printf("Current level: %s\n", toString(getCurrentLevel()));
		Console.out.printf("New level? [e]rr/[w]ar/[i]nf/[d]eb ");
		
		String line = Console.in.readLine();
		Level level = fromString(line);
		
		if (level != null) {
			setCurrentLevel(level);
			Console.out.printf("New level: %s\n",toString(level));
            Console.out.println();
		}
		else
			Console.out.println("Level unchanged");
	}

	public Level getCurrentLevel() {
		return ((ch.qos.logback.classic.Logger)LoggerFactory.getLogger(name)).getLevel();
	}

	public void setCurrentLevel(Level level) {
        ((ch.qos.logback.classic.Logger)LoggerFactory.getLogger(name)).setLevel(level);
	}

	public String toString(Level level) {
        return (level == null) ? "unknown" : level.toString();
	}
	
	public Level fromString(String s) {
		if (s == null)
			return null;

		s = s.trim();
		if (s.length() == 0)
			return null;
		
		s = s.toLowerCase();
		
		Level result;
        if (s.startsWith("e"))
            result = Level.ERROR;
        else if (s.startsWith("w"))
			result = Level.WARN;
		else if (s.startsWith("i"))
			result = Level.INFO;
		else if (s.startsWith("d"))
			result = Level.DEBUG;
		else 
		    throw new RuntimeException("Invalid level name: "+s);
		
		return result;
	}

}
