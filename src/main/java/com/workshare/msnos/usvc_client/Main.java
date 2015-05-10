package com.workshare.msnos.usvc_client;


public class Main {

    public static void main(String[] args) throws Exception {
        String name;
        int port;
        try {
            name = args[0];
            port = Integer.parseInt(args[1]);
        } catch (Exception any) {
            System.err.println("You should specify a name and a port to be used for the microservice");
            System.err.println("Optionally you can add HEADLESS to prevent the user console to show: in that case he service automatically joins the cloud");
            return;
        }
        
        Server server = new Server(name, port).init();
        
        if (args.length > 2 && args[2].equalsIgnoreCase("HEADLESS"))
            server.runHeadless();
        else
            server.runWithConsole();
    }
}
