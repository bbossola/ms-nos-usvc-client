package com.workshare.msnos.usvc_client.commands;

import java.util.Collection;

import com.workshare.msnos.core.Agent;
import com.workshare.msnos.core.Cloud;
import com.workshare.msnos.core.Message;
import com.workshare.msnos.core.Message.Type;
import com.workshare.msnos.core.MessageBuilder;
import com.workshare.msnos.core.RemoteAgent;
import com.workshare.msnos.usvc.Microcloud;
import com.workshare.msnos.usvc.Microservice;
import com.workshare.msnos.usvc_client.Command;

public class PingAllCommand implements Command {

    private final Cloud cloud;
    private final Agent local;
    
    public PingAllCommand(Microcloud ucloud, Microservice usvc) {
        super();
        this.cloud = ucloud.getCloud();
        this.local = usvc.getAgent();
    }

    @Override
    public String description() {
        return "Sends an ping to each agent";
    }

    @Override
    public void execute() throws Exception {
        final Collection<RemoteAgent> agents = cloud.getRemoteAgents();
        System.out.println("Remote agents: "+agents.size());
        for (Agent agent : agents) {
            System.out.println("Sending PING to agent "+agent.getIden().getUUID());
            Message message = new MessageBuilder(Type.PIN, local, agent).make();
            cloud.send(message);
        }
    }

}
