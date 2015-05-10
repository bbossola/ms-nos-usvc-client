package com.workshare.msnos.usvc_client.commands;

import com.workshare.msnos.usvc_client.Command;
import com.workshare.msnos.usvc_client.http.MiniHttpServer;
import com.workshare.msnos.usvc_client.ui.Console;

public class FlipHelloApiFaulty implements Command {

    private final Console console;
    private final MiniHttpServer http;

    public FlipHelloApiFaulty(Console console, MiniHttpServer http) {
        this.http = http;
        this.console = console;
    }

    @Override
    public String description() {
        return "Flip /hello API faulty";
    }

    @Override
    public void execute() throws Exception {
        final boolean status = !http.isHelloFaulty();
        console.out().println("Setting hello API to " + (status ? "FAULTY" : "okay"));
        http.setHelloFaulty(status);
    }

}
