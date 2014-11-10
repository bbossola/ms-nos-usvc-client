package com.workshare.msnos.usvc_client.commands;

import com.workshare.msnos.usvc.Microservice;
import com.workshare.msnos.usvc_client.Command;

public class LeaveCommand implements Command {

    private final Microservice usvc;
    
    public LeaveCommand(Microservice usvc) {
        super();
        this.usvc = usvc;
    }

    @Override
    public String description() {
        return "Leave the cloud";
    }

    @Override
    public void execute() throws Exception {
        usvc.leave();
    }

}
