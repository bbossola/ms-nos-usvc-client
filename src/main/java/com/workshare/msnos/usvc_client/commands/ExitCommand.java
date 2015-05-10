package com.workshare.msnos.usvc_client.commands;

import com.workshare.msnos.usvc_client.Command;
import com.workshare.msnos.usvc_client.ui.SysConsole;

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
	        SysConsole.out.println("Running exit commands");
	        for (Command command: commands) {
	            SysConsole.out.println("- "+command.description());
	            command.execute();
	        }
	    } finally {
	        SysConsole.out.println("\nExiting, thanks for all the fish :)");
    	    System.exit(0);
	    }
	}

}
