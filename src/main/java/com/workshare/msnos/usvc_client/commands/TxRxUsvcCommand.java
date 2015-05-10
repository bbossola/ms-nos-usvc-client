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
import com.workshare.msnos.usvc.IMicroservice;
import com.workshare.msnos.usvc.Microcloud;
import com.workshare.msnos.usvc.Microservice;
import com.workshare.msnos.usvc_client.Command;
import com.workshare.msnos.usvc_client.ui.SysConsole;

public class TxRxUsvcCommand implements Command {

    private final Microcloud ucloud;
    private final Microservice usvc;
    private final ArrayBlockingQueue<Message> received;

    private volatile boolean running = false;
    private final Type tosend;
    private final Type expect;

    public TxRxUsvcCommand(Microcloud ucloud, Microservice usvc) {
        this(ucloud, usvc, Message.Type.PIN, Message.Type.PON);
    }

    public TxRxUsvcCommand(Microcloud ucloud, Microservice usvc, Type totx, Type torx) {
        super();
        this.ucloud = ucloud;
        this.usvc= usvc;        
        this.tosend = totx;
        this.expect = torx;
        this.received = new ArrayBlockingQueue<Message>(10);

        ucloud.addListener(new Listener() {
            @Override
            public void onMessage(Message message) {
                if (!running)
                    return;
                
                if (message.getType() == expect)
                    try {
                        received.put(message);
                    } catch (InterruptedException e) {
                        Thread.interrupted();
                    }
            }});
    }


    @Override
    public String description() {
        return "Sends an "+tosend+" to a specific microservice, expecting "+expect;
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
        SysConsole.out.print("Enter the name of the agent: ");
        SysConsole.out.flush();
        String name = SysConsole.in.readLine().trim();

        IMicroservice target = find(name);
        if (target == null) {
            SysConsole.out.println("Sorry, target ["+name+" not found!");
            return;
        }
        
        received.clear();
        
        Agent agent = target.getAgent();
        SysConsole.out.println("Sending "+tosend+" to service "+name+" on agent "+agent.getIden().getUUID());
        final Message tx = new MessageBuilder(tosend, usvc.getAgent(), agent).make();
        final Receipt receipt = ucloud.send(tx);

        SysConsole.out.println("Message sent, waiting for PONG back...");
        Message rx = null;
        long end = System.currentTimeMillis() + 20000L;
        while(end > System.currentTimeMillis()) {
            SysConsole.out.println("= Gates: "+receipt.getGate());
            SysConsole.out.println("= Status: "+receipt.getStatus());

            while((rx=received.poll()) != null)
                if (rx.getFrom().equals(agent.getIden())) {
                    break;
            }
            
            if (rx != null) {
                SysConsole.out.println("= Answer received! \n"+rx+"\n");
            } else {
                SysConsole.out.println("Waiting for answer...");
            }
 
            if (rx != null && receipt.getStatus() == Status.DELIVERED)
                break;
            
            Thread.sleep(1000L);
        }
    }

    private IMicroservice find(String name) {
        IMicroservice res = find(name, ucloud.getMicroServices());
        if (res == null)
            res = find(name, ucloud.getPassiveServices());
        
        return res;
    }

    private IMicroservice find(String name, List<? extends IMicroservice> micros) {
        for (IMicroservice micro : micros) {
            if (name.equals(micro.getName()))
                return micro;
        }
        return null;
    }

}
