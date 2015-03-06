package com.workshare.msnos.usvc_client.http;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.workshare.msnos.core.geo.Location;
import com.workshare.msnos.usvc.Microservice;

@SuppressWarnings("restriction")
public class GreeterHandler implements HttpHandler {

    private final Microservice usvc;

    GreeterHandler(Microservice usvc) {
        this.usvc = usvc;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello ");
        sb.append(getQueryParameter(exchange, "name", "anonymous"));
        sb.append(", here is ");
        sb.append(usvc.getName());
        if (usvc.getLocation() != Location.UNKNOWN) {
            sb.append(" speaking from ");
            sb.append(usvc.getLocation());
        }
        sb.append("\n");
        
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

}
