package com.workshare.msnos.usvc_client.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.workshare.msnos.usvc.Microcloud;
import com.workshare.msnos.usvc.Microservice;
import com.workshare.msnos.usvc_client.Command;
import com.workshare.msnos.usvc_client.commands.FlipHealthcheckFaulty;
import com.workshare.msnos.usvc_client.commands.FlipHelloApiFaulty;
import com.workshare.msnos.usvc_client.commands.RingsCommand;
import com.workshare.msnos.usvc_client.commands.StatusCommand;
import com.workshare.msnos.usvc_client.ui.BufferingOutputConsole;
import com.workshare.msnos.usvc_client.ui.Console;

@SuppressWarnings("restriction")
public class AdminHandler implements HttpHandler {

    private final Microservice usvc;
    private final Microcloud ucloud;
    private final MiniHttpServer http;

    AdminHandler(Microcloud ucloud, Microservice usvc, MiniHttpServer http) {
        this.ucloud = ucloud;
        this.usvc = usvc;
        this.http = http;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        BufferingOutputConsole console = new BufferingOutputConsole();
        
        Map<String, String> params = extractQueryParamters(exchange);
        String cmd = params.get("cmd");
        if ("flip-service-faulty".equals(cmd)) {
            runCommand(new FlipHealthcheckFaulty(console, http), console);
        } else if ("flip-hello-faulty".equals(cmd)) {
            runCommand(new FlipHelloApiFaulty(console, http), console);
        } else {
            console.out().println("Cloud status:");
            runCommand(new StatusCommand(console, ucloud, usvc, true), console);
            console.out().println();
            runCommand(new RingsCommand(console, ucloud, usvc), console);
        }
        
        respond(exchange, console.bytes(), "text/plain", 200);
    }

    private void runCommand(final Command command, Console console) {        
        try {
            command.execute();
        } catch (Exception e) {
            e.printStackTrace(console.err());
        }
    }

    protected void respond(HttpExchange exchange, final String content, final String contentType, final int status) throws IOException {
        respond(exchange, content.getBytes(), contentType, status);
    }

    private void respond(HttpExchange exchange, byte[] contentBytes, final String contentType, final int status) throws IOException {
        final Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Content-Type", contentType );

        exchange.sendResponseHeaders ( status, contentBytes.length);

        final OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(contentBytes);
        responseBody.close();
    }

    private Map<String, String> extractQueryParamters(HttpExchange exchange){
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = new HashMap<String, String>();
        if (query != null)
            for (String param : query.split("&")) {
                String keyval[] = param.split("=");
                if (keyval.length>1) {
                    params.put(keyval[0], keyval[1]);
                }else{
                    params.put(keyval[0], "");
                }
            }

        return params;
    }

}
