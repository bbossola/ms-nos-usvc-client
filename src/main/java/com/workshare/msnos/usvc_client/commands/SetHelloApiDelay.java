package com.workshare.msnos.usvc_client.commands;

import com.workshare.msnos.usvc_client.Command;
import com.workshare.msnos.usvc_client.http.MiniHttpServer;
import com.workshare.msnos.usvc_client.ui.Console;

public class SetHelloApiDelay implements Command {

    private final MiniHttpServer http;
    
    public SetHelloApiDelay(MiniHttpServer http) {
        this.http = http;
    }

    @Override
    public String description() {
        return "Set a delay on /hello API response";
    }

    @Override
    public void execute() throws Exception {
        Console.out.print("Enter the delay in seconds: ");
        Console.out.flush();
        
        try {
            int seconds = Integer.parseInt(Console.in.readLine().trim());
            http.setHelloDelayInSeconds(seconds);
            Console.out.print("/hello is now delayed by "+seconds+" seconds");
        }
        catch (Exception any) {
            http.setHelloDelayInSeconds(0);
            Console.out.print("/hello is now NOT delayed");
        }
    }

}
