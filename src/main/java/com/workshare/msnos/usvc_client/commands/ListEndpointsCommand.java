package com.workshare.msnos.usvc_client.commands;

import java.util.Set;

import com.workshare.msnos.core.Cloud;
import com.workshare.msnos.core.Gateway;
import com.workshare.msnos.core.protocols.ip.Endpoint;
import com.workshare.msnos.core.protocols.ip.Endpoints;
import com.workshare.msnos.core.protocols.ip.www.WWWGateway;
import com.workshare.msnos.usvc.Microcloud;
import com.workshare.msnos.usvc_client.Command;
import com.workshare.msnos.usvc_client.ui.Console;

public class ListEndpointsCommand implements Command {

    private final Cloud cloud;
    
    public ListEndpointsCommand(Microcloud ucloud) {
        super();
        this.cloud = ucloud.getCloud();
    }


    @Override
    public String description() {
        return "List all endpoints";
    }

    @Override
    public void execute() throws Exception {

        final Set<Gateway> gates = cloud.getGateways();
        Console.out.println("Gates: "+gates.size());
        for (Gateway gate : gates) {
            if (gate instanceof WWWGateway)
                continue;
            
            Endpoints endpoints = gate.endpoints();
            Console.out.println("  "+gate.name()+" gate:");
            for (Endpoint endpoint : endpoints.all()) {
                Console.out.println("    "+endpoint);
            }
        }
    }
    
}
