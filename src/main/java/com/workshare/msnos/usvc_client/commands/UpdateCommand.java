package com.workshare.msnos.usvc_client.commands;

import java.util.concurrent.TimeUnit;

import com.workshare.msnos.usvc.Microcloud;
import com.workshare.msnos.usvc_client.Command;

public class UpdateCommand implements Command {

    private final Microcloud cloud;
    
    public UpdateCommand(Microcloud cloud) {
        super();
        this.cloud = cloud;
    }

    @Override
    public String description() {
        return "Updates the information of this cloud";
    }

    @Override
    public void execute() throws Exception {
        cloud.update(5, TimeUnit.SECONDS);
    }

}
