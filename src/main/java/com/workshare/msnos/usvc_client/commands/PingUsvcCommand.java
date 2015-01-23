package com.workshare.msnos.usvc_client.commands;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import com.workshare.msnos.core.Agent;
import com.workshare.msnos.core.Cloud.Listener;
import com.workshare.msnos.core.Message;
import com.workshare.msnos.core.Message.Status;
import com.workshare.msnos.core.Message.Type;
import com.workshare.msnos.core.MessageBuilder;
import com.workshare.msnos.core.Receipt;
import com.workshare.msnos.usvc.IMicroService;
import com.workshare.msnos.usvc.Microcloud;
import com.workshare.msnos.usvc.Microservice;
import com.workshare.msnos.usvc_client.Command;
import com.workshare.msnos.usvc_client.Console;

public class PingUsvcCommand implements Command {

    private final Microcloud ucloud;
    private final Microservice usvc;
    private final ArrayBlockingQueue<Message> pongs;

    private volatile boolean running = false;

    public PingUsvcCommand(Microcloud ucloud, Microservice usvc) {
        super();
        this.ucloud = ucloud;
        this.usvc= usvc;
        this.pongs = new ArrayBlockingQueue<Message>(10);
        
        ucloud.addListener(new Listener() {
            @Override
            public void onMessage(Message message) {
                if (!running)
                    return;
                
                if (message.getType() == Message.Type.PON)
                    try {
                        pongs.put(message);
                    } catch (InterruptedException e) {
                        Thread.interrupted();
                    }
            }});
    }

    @Override
    public String description() {
        return "Sends an ping to a specific microservice";
    }

    @Override
    public void execute() throws Exception {
        running = true;
        try {
            doExecute();
        } finally {
            running = false;
        }
        
    }
    
    public void doExecute() throws Exception {
        Console.out.print("Enter the name of the agent: ");
        Console.out.flush();
        String name = Console.in.readLine().trim();

        IMicroService target = find(name);
        if (target == null) {
            Console.out.println("Sorry, target ["+name+" not found!");
            return;
        }
        
        pongs.clear();
        
        Agent agent = target.getAgent();
        Console.out.println("Sending PING to service "+name+" on agent "+agent.getIden().getUUID());
        final Message ping = new MessageBuilder(Type.PIN, usvc.getAgent(), agent).make();
        final Receipt receipt = ucloud.send(ping);

        Console.out.println("Message sent, waiting for PONG back...");
        Message pong = null;
        long end = System.currentTimeMillis() + 20000L;
        while(end > System.currentTimeMillis()) {
            Console.out.println("= Gates: "+receipt.getGate());
            Console.out.println("= Status: "+receipt.getStatus());

            while((pong=pongs.poll()) != null)
                if (pong.getFrom().equals(agent.getIden())) {
                    break;
            }
            
            if (pong != null) {
                Console.out.println("= Pong: received! "+pong);
            } else {
                Console.out.println("Pong: waiting...");
            }
 
            if (pong != null && receipt.getStatus() == Status.DELIVERED)
                break;
            
            Thread.sleep(1000L);
        }
    }

    private IMicroService find(String name) {
        IMicroService res = find(name, ucloud.getMicroServices());
        if (res == null)
            res = find(name, ucloud.getPassiveServices());
        
        return res;
    }

    private IMicroService find(String name, List<? extends IMicroService> micros) {
        for (IMicroService micro : micros) {
            if (name.equals(micro.getName()))
                return micro;
        }
        return null;
    }

}
