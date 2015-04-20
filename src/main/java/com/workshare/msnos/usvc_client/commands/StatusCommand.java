package com.workshare.msnos.usvc_client.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.workshare.msnos.core.Agent;
import com.workshare.msnos.core.Cloud;
import com.workshare.msnos.core.RemoteAgent;
import com.workshare.msnos.core.protocols.ip.Endpoint;
import com.workshare.msnos.usvc.IMicroservice;
import com.workshare.msnos.usvc.Microcloud;
import com.workshare.msnos.usvc.Microservice;
import com.workshare.msnos.usvc.RemoteMicroservice;
import com.workshare.msnos.usvc.api.RestApi;
import com.workshare.msnos.usvc_client.Command;
import com.workshare.msnos.usvc_client.Console;

public class StatusCommand implements Command {

    private final Microservice usvc;
    private final Microcloud cloud;
    private final boolean detailed;

    public StatusCommand(Microcloud ucloud, Microservice usvc, boolean detailed) {
        super();
        this.usvc = usvc;
        this.cloud = ucloud;
        this.detailed = detailed;
    }

    @Override
    public String description() {
        return "Cloud status"+(detailed ? " (detailed) " : "");
    }

    @Override
    public void execute() throws Exception {

        Console.out.println();
        Console.out.print("= Local microservice");
        Console.out.print(" - Joined: " + ((usvc.getCloud() == null) ? "NO": "Yes"));
        Console.out.println();
        dump("", usvc, true);

        List<RemoteMicroservice> remoteServices = cloud.getMicroServices();
        Console.out.println("= Remote microservices: " + remoteServices.size());
        for (RemoteMicroservice microService : remoteServices) {
            dump("  ", microService, false);
        }
        Console.out.println();

        if (detailed) {
            Cloud grid = cloud.getCloud();
            final Collection<RemoteAgent> remoteAgents = grid.getRemoteAgents();
            Console.out.println("= Remote Agents: " + remoteAgents.size());
            for (Agent agent : remoteAgents) {
                dump("  ", agent);
            }
        }
    }

    private void dump(String prefix, IMicroservice usvc, boolean withAgentDetails) {
        final Agent agent = usvc.getAgent();

        Console.out.print(prefix + "Name: " + usvc.getName());
        Console.out.print(" - Location: " + usvc.getLocation());
        Console.out.print(" - Agent: " + agent.getIden().getUUID());
        Console.out.println();

        if (withAgentDetails) {
            Console.out.println(prefix + "- Last seen: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(agent.getAccessTime())));
            final Set<Endpoint> points = agent.getEndpoints();
            Console.out.println(prefix + "- Endpoints: " + points.size());
            for (Endpoint ep : points) {
                Console.out.println(prefix + "  - " + ep);
            }
        }

        List<RestApi> apis = listApis(usvc);
        Console.out.println(prefix + "- Apis: " + apis.size());
        for (RestApi api : apis) {
            String isfaulty = api.isFaulty() ? " (faulty)" : "";
            Console.out.println(prefix + "  - " + api.getPath() + ":" + isfaulty+ " ["+api.getUrl()+"]");
        }
        Console.out.println();
    }

    private List<RestApi> listApis(IMicroservice usvc) {
        List<RestApi> apis = new ArrayList<RestApi>(usvc.getApis());
        Collections.sort(apis, new Comparator<RestApi>(){
            @Override
            public int compare(RestApi a1, RestApi a2) {
                String n1 = a1.getPath();
                String n2 = a2.getPath();
                return n1.compareTo(n2);
            }});
        return apis;
    }

    private void dump(final String prefix, final Agent agent) {

        final IMicroservice micro = findMicroservice(agent);

        Console.out.println(prefix + "Agent: " + agent.getIden().getUUID() + " (usvc: "+(micro == null ? "n/a" : micro.getName())+")");
        Console.out.println(prefix + "  Last seen: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(agent.getAccessTime())));

        final Set<Endpoint> points = agent.getEndpoints();
        Console.out.println(prefix + "  Endpoints: " + points.size());
        for (Endpoint ep : points) {
            Console.out.println(prefix + "    " + ep);
        }

        Console.out.println();
    }

    private IMicroservice findMicroservice(Agent agent) {
        if (usvc.getAgent().equals(agent)) {
            return usvc;
        }

        List<RemoteMicroservice> services = cloud.getMicroServices();
        for (RemoteMicroservice service : services) {
            if (service.getAgent().equals(agent)) {
                return service;
            }
        }

        return null;
    }
}
