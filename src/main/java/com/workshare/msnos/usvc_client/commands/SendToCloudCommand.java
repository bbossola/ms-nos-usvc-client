package com.workshare.msnos.usvc_client.commands;

import com.workshare.msnos.core.Message;
import com.workshare.msnos.core.Message.Type;
import com.workshare.msnos.core.MessageBuilder;
import com.workshare.msnos.usvc.Microcloud;
import com.workshare.msnos.usvc.Microservice;
import com.workshare.msnos.usvc_client.Command;
import com.workshare.msnos.usvc_client.ui.Console;

public class SendToCloudCommand implements Command {

    private final Microcloud cloud;
    private final Microservice usvc;
    private final Type type;
    
    public SendToCloudCommand(Microcloud cloud, Microservice usvc, Type type) {
        super();
        this.cloud = cloud;
        this.usvc = usvc;
        this.type = type;
    }

    @Override
    public String description() {
        return "Sends a "+type+" to the cloud";
    }

    @Override
    public void execute() throws Exception {
        Message message;
        if (usvc.getAgent().getCloud() == null) {
            message = new MessageBuilder(type, cloud.getCloud(), cloud.getCloud()).make();
            Console.out.println("Sending message from the cloud");
        }
        else {
            message = new MessageBuilder(type, usvc.getAgent(), cloud.getCloud()).make();
            Console.out.println("Sending message from the agent");
        }
        cloud.send(message);
    }

}
