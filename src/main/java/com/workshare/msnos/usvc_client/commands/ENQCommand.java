package com.workshare.msnos.usvc_client.commands;

import com.workshare.msnos.core.Message;
import com.workshare.msnos.core.Message.Type;
import com.workshare.msnos.core.MessageBuilder;
import com.workshare.msnos.usvc.Microcloud;
import com.workshare.msnos.usvc.Microservice;
import com.workshare.msnos.usvc_client.Command;

public class ENQCommand implements Command {

    private final Microcloud cloud;
    private final Microservice usvc;
    
    public ENQCommand(Microcloud cloud, Microservice usvc) {
        super();
        this.cloud = cloud;
        this.usvc = usvc;
    }

    @Override
    public String description() {
        return "Sends an ENQ to the cloud";
    }

    @Override
    public void execute() throws Exception {
        Message message = new MessageBuilder(Type.ENQ, usvc.getAgent(), cloud.getCloud()).make();
        cloud.send(message);
    }

}
