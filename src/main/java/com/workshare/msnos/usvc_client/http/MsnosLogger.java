package com.workshare.msnos.usvc_client.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.workshare.msnos.core.Cloud.Listener;
import com.workshare.msnos.core.Message;
import com.workshare.msnos.soup.json.Json;
import com.workshare.msnos.usvc.Microcloud;

@SuppressWarnings("restriction")
public class MsnosLogger implements HttpHandler {
    private static final Logger log = LoggerFactory.getLogger(MsnosHandler.class);

    private final Microcloud cloud;

    public MsnosLogger(Microcloud cloud) {
        this.cloud = cloud;
     }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        log.info("Started long polling to the client...");
        final Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type","text/plain");
        exchange.sendResponseHeaders(200,0);
        
        final PrintWriter pw = new PrintWriter(new OutputStreamWriter(exchange.getResponseBody(), "UTF-8"));
        try {
            final Listener listener = new Listener() {
                @Override
                public void onMessage(Message message) {
                    final String text = Json.toJsonString(message);
                    log.debug("Sending {}", text);
                    pw.println(text);
                    pw.flush();
                    try {
                        exchange.getResponseBody().flush();
                    } catch (IOException ignore) {
                    }
                }};

            cloud.addListener(listener);
            try {
                while(true) {
                    try {
                        Thread.sleep(10000L);
                        pw.println("{}");
                        pw.flush();
                        log.debug("Sending noop");
                    } catch (InterruptedException e) {
                        Thread.interrupted();
                        throw new RuntimeException(e);
                    }
                }
            }
            finally {
                cloud.removeListener(listener);
            }
        }
        finally {
            log.info("Polling finished!");
            exchange.getResponseBody().close();
        }
    }
}
