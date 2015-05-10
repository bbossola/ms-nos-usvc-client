package com.workshare.msnos.usvc_client;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.workshare.msnos.core.Cloud;
import com.workshare.msnos.core.Cloud.Listener;
import com.workshare.msnos.core.Message;
import com.workshare.msnos.core.Message.Type;
import com.workshare.msnos.core.MsnosException;
import com.workshare.msnos.core.security.KeysStore;
import com.workshare.msnos.core.security.Signer;
import com.workshare.msnos.usvc.Microcloud;
import com.workshare.msnos.usvc.Microservice;
import com.workshare.msnos.usvc_client.commands.ExitCommand;
import com.workshare.msnos.usvc_client.commands.FlipHealthcheckFaulty;
import com.workshare.msnos.usvc_client.commands.FlipHelloApiFaulty;
import com.workshare.msnos.usvc_client.commands.JoinCommand;
import com.workshare.msnos.usvc_client.commands.LeaveCommand;
import com.workshare.msnos.usvc_client.commands.ListEndpointsCommand;
import com.workshare.msnos.usvc_client.commands.LogControl;
import com.workshare.msnos.usvc_client.commands.PingAllCommand;
import com.workshare.msnos.usvc_client.commands.RingsCommand;
import com.workshare.msnos.usvc_client.commands.SendToCloudCommand;
import com.workshare.msnos.usvc_client.commands.SetHelloApiDelay;
import com.workshare.msnos.usvc_client.commands.StatusCommand;
import com.workshare.msnos.usvc_client.commands.SubmenuCommand;
import com.workshare.msnos.usvc_client.commands.TxRxUsvcCommand;
import com.workshare.msnos.usvc_client.commands.UpdateCommand;
import com.workshare.msnos.usvc_client.http.MiniHttpServer;
import com.workshare.msnos.usvc_client.ui.Menu;
import com.workshare.msnos.usvc_client.ui.SysConsole;

public class Server {

    private static final Logger logger = Logger.getLogger("com.workshare");

    private final Microcloud cloud;
    private final Microservice micro;
    private final MiniHttpServer http;
    private final Command[] commands;
    private final Menu menu;

    public Server(String name, int port) throws IOException {
        cloud = createMicrocloud();
        micro = new Microservice(name);
        http = new MiniHttpServer(cloud, micro, port);
        commands = createCommands(cloud, micro, http);
        menu = new Menu(commands);
    }
    
    public Server init() {
        http.start();
        return this;
    }

    public void runHeadless() throws Exception {
        SysConsole.out.println("Running join command...");
        new JoinCommand(cloud, micro, http.apis()).execute();
        SysConsole.out.println("Done - Running in headless mode\n");
        dumpStatus();
        
        cloud.addListener(new Listener() {
            @Override
            public void onMessage(Message message) {
                if (message.getType() == Type.PRS || message.getType() == Type.FLT || message.getType() == Type.QNE)
                    dumpStatus();
            }
        });
        
        waitPolitelyForever();
    }

    private synchronized void dumpStatus() {
        try {
            SysConsole.out.println("\n------------------------------------------------------------------------------------------------------------------------------");
            new StatusCommand(SysConsole.get(), cloud, micro, true).execute();
        } catch (Exception ignore) {
        }
    }
    
    private void waitPolitelyForever() throws IOException {
        System.in.read();
    }

    public void runWithConsole() {
        
        while (true) {
            menu.show();
            try {
                menu.selection().execute();
            } catch (Exception ex) {
                logger.log(Level.WARNING, "An error occured!", ex);
            }

            sleep(500L);
        }
    }
    
    private void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
    }
    
    private Command[] createCommands(Microcloud cloud, Microservice usvc, MiniHttpServer http) {
        Command[] advanced = {
            new SendToCloudCommand(cloud, micro, Message.Type.DSC),
            new SendToCloudCommand(cloud, micro, Message.Type.ENQ),
            new TxRxUsvcCommand(cloud, micro, Message.Type.PIN, Message.Type.PON),
            new TxRxUsvcCommand(cloud, micro, Message.Type.TRC, Message.Type.ACK),
            new StatusCommand(SysConsole.get(), cloud, micro, true),
            new FlipHelloApiFaulty(SysConsole.get(), http),
            new FlipHealthcheckFaulty(SysConsole.get(), http),
            new SetHelloApiDelay(http),
            new LogControl("protocol"),
            new LogControl("routing"),
        };
        
        Command[] commands = {
            new StatusCommand(SysConsole.get(), cloud, micro, false),
            new JoinCommand(cloud, micro, http.apis()),
            new LeaveCommand(micro),
            new RingsCommand(SysConsole.get(), cloud, micro),
            new UpdateCommand(cloud),
            new ListEndpointsCommand(cloud),
            new PingAllCommand(cloud, micro),
            new SubmenuCommand("advanced...", advanced),
            
            new ExitCommand(new LeaveCommand(micro)),
        };
        
        return commands;
    }
    
    private Microcloud createMicrocloud() throws MsnosException {
        String signid = getSecurityId();
        Cloud icloud;
        if (signid != null) {
            SysConsole.out.println("ATTENTION! Using secured cloud by id '"+signid+"'");
            icloud = new Cloud(new UUID(111, 222), signid);
        }
        else {
            SysConsole.out.println("Using open cloud :)");
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
