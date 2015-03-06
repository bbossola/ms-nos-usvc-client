package com.workshare.msnos.usvc_client.http;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public class HealthcheckHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Content-Type", "application/json" );

        if (exchange.getRequestMethod().equalsIgnoreCase("HEAD")) {
            exchange.sendResponseHeaders ( 200 , -1);
        } else {
            byte[] content = "{\"result\":\"ok\"}".getBytes();
    
            exchange.sendResponseHeaders ( 200, content.length);

            final OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(content);
            responseBody.close();
        }
    }
}
