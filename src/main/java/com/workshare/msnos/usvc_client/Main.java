package com.workshare.msnos.usvc_client;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        String name;
        int port;
        try {
            name = args[0];
            port = Integer.parseInt(args[1]);
        } catch (Exception any) {
            System.err.println("You should specify a name and a port to be used for the microservice");
            System.exit(-1);
            return;
        }
        
        new Server(name, port).run();
    }
}
