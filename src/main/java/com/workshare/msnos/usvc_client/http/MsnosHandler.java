package com.workshare.msnos.usvc_client.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.workshare.msnos.core.Message;
import com.workshare.msnos.core.protocols.ip.Endpoint;
import com.workshare.msnos.core.serializers.WireJsonSerializer;
import com.workshare.msnos.usvc.Microcloud;

@SuppressWarnings("restriction")
public class MsnosHandler implements HttpHandler {
    private static final String CHARSET_STRING = "charset=";

    private static final Logger log = LoggerFactory.getLogger(MsnosHandler.class);

    private final Microcloud cloud;
    private final WireJsonSerializer serializer;

    public MsnosHandler(Microcloud cloud) {
        this.cloud = cloud;
        this.serializer = new WireJsonSerializer();

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String encoding = getRequestEncoding(exchange, "UTF-8");
            Reader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), encoding));
            try {
                Message message = serializer.fromReader(reader, Message.class);
                log.debug("Received message {}", message);
                cloud.process(message, Endpoint.Type.HTTP);
            } finally {
                reader.close();
            }

        } catch (Exception ex) {
            log.warn("Error handling http request", ex);
            exchange.sendResponseHeaders(400, 0);
        }

        exchange.sendResponseHeaders(200, 0);
        exchange.getResponseBody().close();
    }

    private String getRequestEncoding(HttpExchange exchange, String defval) {

        Headers reqHeaders = exchange.getRequestHeaders();
        String contentType = reqHeaders.getFirst("Content-Type");
        String encoding = defval;
        if (contentType != null) {
            String[] values = contentType.split(";");
            for (String value : values) {
                value = value.trim();
                if (value.toLowerCase().startsWith(CHARSET_STRING)) {
                    encoding = value.substring(CHARSET_STRING.length());
                    break;
                }
            }
        }

        log.trace("Content type header: {}, encoding: {} ",contentType, encoding);
        return encoding;
    }
}
