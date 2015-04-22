package com.workshare.msnos.usvc_client.commands;

import com.workshare.msnos.usvc.Microcloud;
import com.workshare.msnos.usvc.Microservice;
import com.workshare.msnos.usvc.api.RestApi;
import com.workshare.msnos.usvc_client.Command;

public class JoinCommand implements Command {

    private final Microcloud cloud;
    private final Microservice usvc;
    private final RestApi[] apis;
    
    public JoinCommand(Microcloud cloud, Microservice usvc, RestApi[] apis) {
        super();
        this.cloud = cloud;
        this.usvc = usvc;
        this.apis = apis;
    }

    @Override
    public String description() {
        return "Join the cloud";
    }

    @Override
    public void execute() throws Exception {
        usvc.join(cloud);
        sleep(500);
        usvc.publish(apis);
    }

    private void sleep(int millis) throws InterruptedException {
        Thread.sleep(millis);
    }

}
