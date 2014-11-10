package com.workshare.msnos.usvc_client.http;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.workshare.msnos.usvc.Microservice;
import com.workshare.msnos.usvc.api.RestApi;
import com.workshare.msnos.usvc.api.RestApi.Type;

@SuppressWarnings("restriction")
public class MiniHttpServer {

    public static final String URI_HEALTH = "/health";
    public static final String URI_GREET = "/hello";
    public static final String URI_MSNOS = "/msnos";

    private HttpServer httpServer;
    private RestApi[] apis;

    public MiniHttpServer(Microservice micro, int port) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext(URI_HEALTH, new HealthcheckHandler());
        httpServer.createContext(URI_GREET, new GreeterHandler(micro));
        httpServer.createContext(URI_MSNOS, new MsnosHandler(micro.getCloud()));
        
        apis = new RestApi[] {
            new RestApi("health", URI_HEALTH, port).asHealthCheck(),
            new RestApi("hello", URI_GREET, port),
            new RestApi("msnos", URI_MSNOS, port, null, Type.MSNOS_HTTP, false),
        };
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
