package com.workshare.msnos.usvc_client;

import com.workshare.msnos.core.Cloud;
import com.workshare.msnos.core.MsnosException;
import com.workshare.msnos.core.security.KeysStore;
import com.workshare.msnos.core.security.Signer;
import com.workshare.msnos.usvc.Microcloud;
import com.workshare.msnos.usvc.Microservice;
import com.workshare.msnos.usvc_client.commands.*;
import com.workshare.msnos.usvc_client.http.MiniHttpServer;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final Logger logger = Logger.getLogger("com.workshare");

    private final Microcloud cloud;
    private final Microservice micro;
    private final MiniHttpServer http;
    private final Command[] commands;

    public Server(String name, int port) throws IOException {
        cloud = createMicrocloud();
        micro = new Microservice(name);
        http = new MiniHttpServer(cloud, micro, port);
        commands = createCommands(cloud, micro, http);
    }
    
    public void run() throws IOException {
        
        http.start();
        
        while (true) {
            showMenu();
            try {
                command().execute();
            } catch (Exception ex) {
                logger.log(Level.WARNING, "An error occured!", ex);
            }

            sleep(500L);
        }
    }

    private Command command() throws IOException {
        String line = Console.in.readLine();

        Command result;
        try {
            int index = Integer.parseInt(line);
            result = commands[index];
        } catch (Exception ignore) {
            result = commands[0];
        }

        return result;
    }

    private void showMenu() {
        System.out.println();
        System.out.println("Action? ");
        for (int i = 0; i < commands.length; i++) {
            System.out.printf("%d) %s\n", i, commands[i].description());
        }

        System.out.flush();
    }

    private void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
    }
    
    private Command[] createCommands(Microcloud cloud, Microservice usvc, MiniHttpServer http) {
        Command[] commands = {
            new StatusCommand(cloud, micro),
            new JoinCommand(cloud, micro, http.apis()),
            new LeaveCommand(micro),
            new ENQCommand(cloud, micro),
            new PingAllCommand(cloud, micro),
            new PingUsvcCommand(cloud, micro),
            new RingsCommand(cloud, micro),

            new ProtocolLogControl(),
            new ExitCommand(new LeaveCommand(micro)),
        };
        
        return commands;
    }
    
    private Microcloud createMicrocloud() throws MsnosException {
        String signid = getSecurityId();
        Cloud icloud;
        if (signid != null) {
            Console.out.println("ATTENTION! Using secured cloud by id '"+signid+"'");
            icloud = new Cloud(new UUID(111, 222), signid);
        }
        else {
            Console.out.println("Using open cloud :)");
            icloud = new Cloud(new UUID(111, 222));
        }

        final Microcloud cloud = new Microcloud(icloud);
        return cloud;
    }

    private String getSecurityId() {
        String res = null;
        KeysStore keystore = Signer.DEFAULT_KEYSSTORE;
        if (!keystore.isEmpty()) {
            if (keystore.get("test") != null)
                res = "test";
        }

        return res;
    }
}
