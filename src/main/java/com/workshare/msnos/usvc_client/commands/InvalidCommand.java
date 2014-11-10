package com.workshare.msnos.usvc_client.commands;

import com.workshare.msnos.usvc_client.Command;

public class InvalidCommand implements Command {

	@Override
	public String description() {
		return "Invalid action";
	}

	@Override
	public void execute() throws Exception {
		System.out.printf("Invalid action requested\n");	
	}
}
