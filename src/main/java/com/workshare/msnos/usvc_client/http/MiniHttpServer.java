package com.workshare.msnos.usvc_client.http;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.workshare.msnos.usvc.Microcloud;
import com.workshare.msnos.usvc.Microservice;
import com.workshare.msnos.usvc.api.RestApi;
import com.workshare.msnos.usvc.api.RestApi.Type;

@SuppressWarnings("restriction")
public class MiniHttpServer {

    public static final String URI_WASSUP = "/wassup";
    public static final String URI_HEALTH = "/health";
    public static final String URI_GREET = "/hello";
    public static final String URI_MSNOS = "/msnos";

    private HttpServer httpServer;
    private RestApi[] apis;

    public MiniHttpServer(Microcloud cloud, Microservice micro, int port) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext(URI_WASSUP, new WassupHandler(micro));
        httpServer.createContext(URI_HEALTH, new HealthcheckHandler());
        httpServer.createContext(URI_GREET, new GreeterHandler(micro));
        httpServer.createContext(URI_MSNOS, new MsnosHandler(cloud));
        
        apis = new RestApi[] {
            new RestApi("sample", URI_GREET, port),
            new RestApi("sample", URI_WASSUP, port),
            new RestApi("sample", URI_HEALTH, port).asHealthCheck(),
            new RestApi("sample", URI_MSNOS, port, null, Type.MSNOS_HTTP, false),
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
