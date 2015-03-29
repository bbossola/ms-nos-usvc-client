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
    public static final String URI_GREET = "/hello";
    public static final String URI_MSNOS = "/msnos";

    public static final String URI_ADMIN_MESSAGES = "/admin/messages";

    private HttpServer httpServer;
    private RestApi[] apis;

    public MiniHttpServer(Microcloud cloud, Microservice micro, int port) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.setExecutor(Executors.newCachedThreadPool());
        httpServer.createContext(URI_WASSUP, new WassupHandler(micro));
        httpServer.createContext(URI_HEALTH, new HealthcheckHandler());
        httpServer.createContext(URI_GREET, new GreeterHandler(micro));
        httpServer.createContext(URI_MSNOS, new MsnosHandler(cloud));
        
        apis = new RestApi[] {
            new RestApi(URI_GREET, port),
            new RestApi(URI_WASSUP, port),
            new RestApi(URI_HEALTH, port).asHealthCheck(),
            new RestApi(URI_MSNOS, port).asMsnosEndpoint(),
        };

        httpServer.createContext(URI_ADMIN_MESSAGES, new MsnosLogger(cloud));
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
}
