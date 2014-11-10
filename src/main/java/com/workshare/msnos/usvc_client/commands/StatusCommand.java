package com.workshare.msnos.usvc_client.commands;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.workshare.msnos.core.Agent;
import com.workshare.msnos.core.Cloud;
import com.workshare.msnos.core.RemoteAgent;
import com.workshare.msnos.core.protocols.ip.Endpoint;
import com.workshare.msnos.usvc.IMicroService;
import com.workshare.msnos.usvc.Microcloud;
import com.workshare.msnos.usvc.Microservice;
import com.workshare.msnos.usvc.RemoteMicroservice;
import com.workshare.msnos.usvc.api.RestApi;
import com.workshare.msnos.usvc_client.Command;
import com.workshare.msnos.usvc_client.Console;

public class StatusCommand implements Command {

    private final Microservice usvc;
    
    public StatusCommand(Microservice usvc) {
        super();
        this.usvc = usvc;
    }

    @Override
    public String description() {
        return "Cloud status";
    }

    @Override
    public void execute() throws Exception {

        Console.out.println();
        Console.out.println("= Local microservice");
        dump("", usvc);

        Microcloud cloud = usvc.getCloud();
        if (cloud != null) {
            List<RemoteMicroservice> remoteServices = cloud.getMicroServices();
            Console.out.println("= Remote microservices: " + remoteServices.size());
            for (RemoteMicroservice microService : remoteServices) {
                dump("  ", microService);
            }
            Console.out.println();

            Cloud grid = cloud.getCloud();
            final Collection<RemoteAgent> remoteAgents = grid.getRemoteAgents();
            Console.out.println("= Remote Agents: " + remoteAgents.size());
            for (Agent agent : remoteAgents) {
                dump("  ", agent);
            }

        }
    }

    private void dump(String prefix, IMicroService usvc) {
        Console.out.println(prefix+"Name: "+usvc.getName());
        Console.out.println(prefix+"Location: "+usvc.getLocation());
 
        final Agent agent = usvc.getAgent();
        Console.out.println(prefix+"Agent: "+agent.getIden().getUUID());
        Console.out.println(prefix+"- Last seen: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(agent.getAccessTime())));

        final Set<Endpoint> points = agent.getEndpoints();
        Console.out.println(prefix+"- Endpoints: "+points.size());
        for (Endpoint ep : points) {
            Console.out.println(prefix+"  - "+ep);
        }
        
        Set<RestApi> apis = usvc.getApis();
        Console.out.println(prefix+"- Apis: "+apis.size());
        for (RestApi api : apis) {
            String isfaulty = api.isFaulty() ? " (faulty)" : "";
            Console.out.println(prefix+"  - "+api.getName()+":"+api.getPath()+isfaulty);
        }
        Console.out.println();
    }

    private void dump(final String prefix, final Agent agent) {
        
        Console.out.println(prefix+"Agent: "+agent.getIden().getUUID());
        Console.out.println(prefix+"  Last seen: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(agent.getAccessTime())));

        final Set<Endpoint> points = agent.getEndpoints();
        Console.out.println(prefix+"  Endpoints: "+points.size());
        for (Endpoint ep : points) {
            Console.out.println(prefix+"    "+ep);
        }
        
        Console.out.println();
    }
}
