package com.workshare.msnos.usvc_client.commands;

import com.workshare.msnos.usvc_client.Command;
import com.workshare.msnos.usvc_client.http.MiniHttpServer;
import com.workshare.msnos.usvc_client.ui.Console;

public class FlipHealthcheckFaulty implements Command {

    private final Console console;
    private final MiniHttpServer http;
    
    public FlipHealthcheckFaulty(Console console, MiniHttpServer http) {
        this.http = http;
        this.console = console;
    }

    @Override
    public String description() {
        return "Flip healthcheck faulty";
    }

    @Override
    public void execute() throws Exception {
        final boolean status = !http.isFaulty();
        console.out().println("Setting healthcheck to " + (status ? "FAULTY" : "okay"));
        http.setFaulty(status);
    }

}
