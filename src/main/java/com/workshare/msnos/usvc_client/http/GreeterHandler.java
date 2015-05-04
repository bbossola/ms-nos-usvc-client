package com.workshare.msnos.usvc_client.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.workshare.msnos.core.geo.Location;
import com.workshare.msnos.usvc.Microservice;

@SuppressWarnings("restriction")
public class GreeterHandler implements HttpHandler {

    private static final Logger log = Logger.getLogger("com.workshare");

    private final Microservice usvc;

    private volatile boolean faulty = false;
    private volatile long delayInSeconds = 0l;

    GreeterHandler(Microservice usvc) {
        this.usvc = usvc;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (delayInSeconds != 0l) {
            log.info("Delaying response for "+delayInSeconds+" seconds...");
            try {
                Thread.sleep(delayInSeconds*1000l);
            } catch (InterruptedException e) {
                Thread.interrupted();
                throw new IOException(e);
            }
        }
        
        if (!faulty) {
            String text = sayHello(getQueryParameter(exchange, "name", "anonymous"), usvc);
            respond(exchange, text, "text/plain", 200);
        } else {
            respond(exchange, "BOOM!", "text/plain", 500);
        }
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

    static String sayHello(final String to, final Microservice from) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Hello ");
        buffer.append(to);
        buffer.append(", here is ");
        buffer.append(from.getName());
        if (from.getLocation() != Location.UNKNOWN) {
            buffer.append(" speaking from ");
            buffer.append(from.getLocation());
        }
        buffer.append("\n");
        return buffer.toString();
    }

    public void setFaulty(boolean newFaulty) {
        this.faulty = newFaulty;
    }

    public void setDelay(long seconds) {
        this.delayInSeconds = seconds;
    }

}
