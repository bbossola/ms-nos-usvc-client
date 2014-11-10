package com.workshare.msnos.usvc_client;


public interface Command {
	
	public String description()
	;

	public void execute() throws Exception
	;
}
