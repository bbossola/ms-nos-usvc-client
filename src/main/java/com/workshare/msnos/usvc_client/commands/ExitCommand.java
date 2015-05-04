package com.workshare.msnos.usvc_client.commands;

import com.workshare.msnos.usvc_client.Command;
import com.workshare.msnos.usvc_client.ui.Console;

public class ExitCommand implements Command {

    private Command[] commands;

    public ExitCommand(Command... commands) {
        this.commands = commands;
    }

    @Override
	public String description() {
		return "Exit";
	}

	@Override
	public void execute() throws Exception {
	    try {
	        Console.out.println("Running exit commands");
	        for (Command command: commands) {
	            Console.out.println("- "+command.description());
	            command.execute();
	        }
	    } finally {
	        Console.out.println("\nExiting, thanks for all the fish :)");
    	    System.exit(0);
	    }
	}

}
