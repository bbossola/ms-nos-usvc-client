package com.workshare.msnos.usvc_client.commands;

import com.workshare.msnos.usvc_client.Command;

public class ExitCommand implements Command {

	@Override
	public String description() {
		return "Exit";
	}

	@Override
	public void execute() throws Exception {
		System.out.println("\nExiting, thanks for all the fish :)");
		System.exit(0);
	}

}
