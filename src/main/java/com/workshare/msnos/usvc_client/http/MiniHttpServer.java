package com.workshare.msnos.usvc_client.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;
import com.workshare.msnos.usvc.Microcloud;
import com.workshare.msnos.usvc.Microservice;
import com.workshare.msnos.usvc.api.RestApi;

@SuppressWarnings("restriction")
public class MiniHttpServer {

    public static final String URI_WASSUP = "/wassup";
    public static final String URI_HEALTH = "/health";
    public static final String URI_STICKY = "/sticky";
    public static final String URI_GREET = "/hello";
    public static final String URI_MSNOS = "/msnos";

    public static final String URI_ADMIN_MESSAGES = "/admin/messages";

    private final HttpServer httpServer;
    private final RestApi[] apis;

    private final HealthcheckHandler healthcheckHandler;
    private final GreeterHandler greeterHandler;

    public MiniHttpServer(Microcloud cloud, Microservice micro, int port) throws IOException {
        healthcheckHandler = new HealthcheckHandler();
        greeterHandler = new GreeterHandler(micro, "");

        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.setExecutor(Executors.newCachedThreadPool());
        httpServer.createContext(URI_HEALTH, healthcheckHandler);
        httpServer.createContext(URI_GREET, greeterHandler);
        httpServer.createContext(URI_STICKY, new GreeterHandler(micro, "And I am sticky like a chewie!"));
        httpServer.createContext(URI_WASSUP, new WassupHandler(micro));
        httpServer.createContext(URI_MSNOS, new MsnosHandler(cloud));
        
        apis = new RestApi[] {
            new RestApi(URI_GREET, port),
            new RestApi(URI_WASSUP, port),
            new RestApi(URI_STICKY, port).withAffinity(),
            new RestApi(URI_HEALTH, port).asHealthCheck(),
            new RestApi(URI_MSNOS, port).asMsnosEndpoint(),
        };

        httpServer.createContext(URI_ADMIN_MESSAGES, new MsnosLogger(cloud));
        httpServer.createContext("/admin", new AdminHandler(cloud, micro, this));
    }

    public void start() {
        httpServer.start();
    }

    public RestApi[] apis() {
        return apis;
    }

    public void stop() {
        httpServer.stop(0);
    }
    
    public void setFaulty(boolean faulty) {
        healthcheckHandler.setFaulty(faulty);
    }
    
    public boolean isFaulty() {
        return healthcheckHandler.isFaulty();
    }
    
    public void setHelloFaulty(boolean faulty) {
        greeterHandler.setFaulty(faulty);
    }

    public boolean isHelloFaulty() {
        return greeterHandler.isFaulty();
    }

    public void setHelloDelayInSeconds(long seconds) {
        greeterHandler.setDelay(seconds);
    }
}
