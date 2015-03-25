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
        sayHello(getQueryParameter(exchange, "name", "anonymous"), usvc, sb);
        
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

    static void sayHello(final String to, final Microservice from, StringBuilder buffer) {
        buffer.append("Hello ");
        buffer.append(to);
        buffer.append(", here is ");
        buffer.append(from.getName());
        if (from.getLocation() != Location.UNKNOWN) {
            buffer.append(" speaking from ");
            buffer.append(from.getLocation());
        }
        buffer.append("\n");
    }


}
