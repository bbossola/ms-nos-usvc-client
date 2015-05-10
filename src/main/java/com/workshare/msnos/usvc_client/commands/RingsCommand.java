package com.workshare.msnos.usvc_client.commands;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.workshare.msnos.core.Agent;
import com.workshare.msnos.core.Cloud;
import com.workshare.msnos.core.Ring;
import com.workshare.msnos.core.protocols.ip.Endpoint;
import com.workshare.msnos.usvc.IMicroservice;
import com.workshare.msnos.usvc.Microcloud;
import com.workshare.msnos.usvc.Microservice;
import com.workshare.msnos.usvc.RemoteMicroservice;
import com.workshare.msnos.usvc_client.Command;
import com.workshare.msnos.usvc_client.ui.Console;

@SuppressWarnings("unchecked")
public class RingsCommand implements Command {

    private final Microcloud cloud;
    private final Microservice micro;
    private final Console console;
    
    public RingsCommand(Console console, Microcloud ucloud, Microservice micro) {
        super();
        this.cloud = ucloud;
        this.micro = micro;
        this.console = console;
    }

    @Override
    public String description() {
        return "Rings status";
    }

    @Override
    public void execute() throws Exception {

        Cloud grid = cloud.getCloud();
        final Map<Ring, Set<Agent>> rings = createRingMap(grid.getRemoteAgents(), grid.getLocalAgents());
        
        console.out().println("Number of rings: " + rings.size());
        console.out().println();
        for (Ring ring : rings.keySet()) {
            console.out().println("- Ring: " + ring);
            Set<Agent> agents = rings.get(ring);
            for (Agent agent : agents) {
                dump("    ", agent);
            }
        }

        console.out().println();
    }

    private Map<Ring, Set<Agent>> createRingMap(Collection<? extends Agent>... agents_array) {
        final Set<Agent> all = new HashSet<Agent>();
        for (Collection<? extends Agent> collection : agents_array) {
            for (Agent agent : collection) {
                all.add(agent);
            }
        }
        
        final Map<Ring, Set<Agent>> rings = new HashMap<Ring, Set<Agent>>();
        for (Agent agent : all) {
            Ring ring = agent.getRing();
            Set<Agent> agents = rings.get(ring);
            if (agents == null) {
                agents = new HashSet<Agent>();
                rings.put(ring, agents);
            }
            agents.add(agent);
        }
        
        return rings;
    }

    private void dump(final String prefix, final Agent agent) {

        console.out().println(prefix + "Agent: " + agent.getIden().getUUID());
       
        IMicroservice uservice = findMicroservice(agent);
        if (uservice != null)
            console.out().println(prefix + "  UService: " + uservice.getName());

        console.out().println(prefix + "  Last seen: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(agent.getAccessTime())));

        final Set<Endpoint> points = agent.getEndpoints();
        console.out().println(prefix + "  Endpoints: " + points.size());
        for (Endpoint ep : points) {
            console.out().println(prefix + "    " + ep);
        }

        console.out().println();
    }

    private IMicroservice findMicroservice(Agent agent) {
        if (micro.getAgent().equals(agent)) {
            return micro;
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
