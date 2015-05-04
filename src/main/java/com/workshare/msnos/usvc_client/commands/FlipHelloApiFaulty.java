package com.workshare.msnos.usvc_client.commands;

import com.workshare.msnos.usvc_client.Command;
import com.workshare.msnos.usvc_client.http.MiniHttpServer;
import com.workshare.msnos.usvc_client.ui.Console;

public class FlipHelloApiFaulty implements Command {

    private final MiniHttpServer http;

    private volatile boolean faulty = false;
    
    public FlipHelloApiFaulty(MiniHttpServer http) {
        this.http = http;
    }

    @Override
    public String description() {
        return "Flip /hello API faulty";
    }

    @Override
    public void execute() throws Exception {
        final boolean status = !faulty;
        Console.out.println("Setting hello API to " + (status ? "FAULTY" : "okay"));
        http.setHelloFaulty(status);
        faulty = status;
    }

}
