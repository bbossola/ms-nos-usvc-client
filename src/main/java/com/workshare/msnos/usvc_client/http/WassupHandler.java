package com.workshare.msnos.usvc_client.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.workshare.msnos.core.protocols.ip.HttpClientFactory;
import com.workshare.msnos.usvc.Microcloud;
import com.workshare.msnos.usvc.Microservice;
import com.workshare.msnos.usvc.RemoteMicroservice;
import com.workshare.msnos.usvc.api.RestApi;

@SuppressWarnings("restriction")
public class WassupHandler implements HttpHandler {

    private final Microservice usvc;
    private final HttpClient client;

    WassupHandler(Microservice usvc) {
        this.usvc = usvc;
        this.client = HttpClientFactory.newHttpClient();
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Microcloud cloud = usvc.getCloud();
        if (cloud == null) {
            respond(exchange, "Not in the cloud yet :(", "text/plain", 200);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        GreeterHandler.sayHello(getQueryParameter(exchange, "name", "anonymous"), usvc, sb);

        List<RemoteMicroservice> services = cloud.getMicroServices();
        sb.append("What about the others?\n");
        if (!services.isEmpty()) {
            for (RemoteMicroservice service : services) {
                sb.append("- ");
                sb.append(service.getName());
                sb.append(" says: \"");
                sb.append(callHello(service).trim());
                sb.append("\"\n");
            }
        } else {
            sb.append("Awww... we are alone!\n");
        }
        respond(exchange, sb.toString(), "text/plain", 200);
    }

    protected void respond(HttpExchange exchange, final String content, final String contentType, final int status) throws IOException {
        byte[] bytes = content.getBytes();

        final Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Content-Type", contentType );

        exchange.sendResponseHeaders ( status, bytes.length);

        final OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(bytes);
        responseBody.close();
    }

    private String getQueryParameter(HttpExchange exchange, String string, String defaultName) {
        return defaultName;
    }

    private String callHello(RemoteMicroservice service) throws IOException {
        RestApi api = findHelloApiOn(service);
        if (api == null) {
            return "No greetings there :(";
        } 

        HttpGet request = new HttpGet(api.getUrl());
        HttpResponse res = client.execute(request);
        final HttpEntity entity = res.getEntity();
        try {
            return EntityUtils.toString(entity, "UTF-8");
        } finally {
            EntityUtils.consume(entity);
        }
    }

    private RestApi findHelloApiOn(RemoteMicroservice service) {
        Set<RestApi> apis = service.getApis();
        for (RestApi api : apis) {
            if (api.getPath().equals("/hello"))
                return api;
        }

        return null;
    }
}
